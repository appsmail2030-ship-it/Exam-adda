package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.ExtractedQuestionDraft
import com.example.ai.PdfQuestionExtractor
import com.example.data.db.AppDatabase
import com.example.data.db.DatabaseSeeder
import com.example.data.model.*
import com.example.data.repository.ExamRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExamViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = ExamRepository(
        examDao = db.examDao(),
        subjectDao = db.subjectDao(),
        topicDao = db.topicDao(),
        questionDao = db.questionDao(),
        testDao = db.testDao(),
        studyDao = db.studyDao()
    )

    // Initial database seed
    init {
        viewModelScope.launch {
            DatabaseSeeder.seedInitialData(db)
        }
    }

    // Core Data Streams
    val exams: StateFlow<List<ExamCategoryEntity>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subjects: StateFlow<List<SubjectEntity>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTopics: StateFlow<List<TopicEntity>> = repository.allTopics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTests: StateFlow<List<TestEntity>> = repository.allTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyTests: StateFlow<List<TestEntity>> = repository.dailyTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mockTests: StateFlow<List<TestEntity>> = repository.mockTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val testAttempts: StateFlow<List<TestAttemptEntity>> = repository.testAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedQuestions: StateFlow<List<QuestionEntity>> = repository.bookmarkedQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wrongQuestions: StateFlow<List<QuestionEntity>> = repository.wrongQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<AppNotificationEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allPdfUploads: StateFlow<List<PdfUploadEntity>> = repository.allPdfUploads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allQuestions: StateFlow<List<QuestionEntity>> = repository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingQuestionsCount: StateFlow<Int> = repository.pendingQuestionsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val approvedQuestionsCount: StateFlow<Int> = repository.approvedQuestionsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pyqQuestionsCount: StateFlow<Int> = repository.pyqQuestionsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Navigation / Selection State
    val selectedExam = MutableStateFlow<ExamCategoryEntity?>(null)
    val selectedSubject = MutableStateFlow<SubjectEntity?>(null)
    val selectedTopic = MutableStateFlow<TopicEntity?>(null)

    // Search
    val searchQuery = MutableStateFlow("")
    val searchResults: StateFlow<List<QuestionEntity>> = searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) flowOf(emptyList())
            else repository.searchQuestions(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Previous Year Questions Filter
    val pyqSelectedExamId = MutableStateFlow<Long?>(null)
    val pyqSelectedYear = MutableStateFlow<Int?>(null)
    val pyqSelectedSubjectId = MutableStateFlow<Long?>(null)
    val pyqSelectedTopicId = MutableStateFlow<Long?>(null)

    val pyqList: StateFlow<List<QuestionEntity>> = combine(
        pyqSelectedExamId,
        pyqSelectedYear,
        pyqSelectedSubjectId,
        pyqSelectedTopicId
    ) { examId, year, subId, topId ->
        Quadruple(examId, year, subId, topId)
    }.flatMapLatest { (examId, year, subId, topId) ->
        repository.getPreviousYearQuestions(examId, year, subId, topId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Topic-wise PYQs
    fun getTopicPyqs(topicId: Long): Flow<List<QuestionEntity>> = repository.getTopicPreviousYearQuestions(topicId)

    // Test System Execution
    val currentTest = MutableStateFlow<TestEntity?>(null)
    val currentQuestions = MutableStateFlow<List<QuestionEntity>>(emptyList())
    val currentQuestionIndex = MutableStateFlow(0)
    val userAnswers = MutableStateFlow<Map<Long, String>>(emptyMap())
    val reviewMarked = MutableStateFlow<Set<Long>>(emptySet())
    val visitedQuestions = MutableStateFlow<Set<Long>>(emptySet())
    val timeRemainingSeconds = MutableStateFlow(600L) // 10 minutes default
    val isTestActive = MutableStateFlow(false)
    val lastSubmittedResult = MutableStateFlow<TestAttemptEntity?>(null)

    private var timerJob: Job? = null

    fun startTest(test: TestEntity) {
        viewModelScope.launch {
            var questions = repository.getQuestionsForTest(test.id)
            if (questions.isEmpty()) {
                // If test has no cross refs yet, grab approved questions from topic or subject
                val topicQuestions = repository.questionDao.getApprovedQuestionsForTopic(test.topicId)
                if (topicQuestions.isNotEmpty()) {
                    questions = topicQuestions.take(test.totalQuestions)
                } else {
                    val all = repository.questionDao.getAllQuestions().first()
                    questions = all.shuffled().take(test.totalQuestions)
                }
            }

            currentTest.value = test
            currentQuestions.value = questions
            currentQuestionIndex.value = 0
            userAnswers.value = emptyMap()
            reviewMarked.value = emptySet()
            visitedQuestions.value = if (questions.isNotEmpty()) setOf(questions[0].id) else emptySet()
            val totalSeconds = (test.durationMinutes * 60).toLong()
            timeRemainingSeconds.value = totalSeconds
            isTestActive.value = true
            lastSubmittedResult.value = null

            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (timeRemainingSeconds.value > 0 && isTestActive.value) {
                delay(1000)
                timeRemainingSeconds.value -= 1
            }
            if (timeRemainingSeconds.value <= 0 && isTestActive.value) {
                // Auto-submit test immediately
                submitTest()
            }
        }
    }

    fun selectAnswer(questionId: Long, answer: String) {
        userAnswers.value = userAnswers.value.toMutableMap().apply { put(questionId, answer) }
    }

    fun clearAnswer(questionId: Long) {
        userAnswers.value = userAnswers.value.toMutableMap().apply { remove(questionId) }
    }

    fun toggleMarkForReview(questionId: Long) {
        val current = reviewMarked.value
        if (current.contains(questionId)) {
            reviewMarked.value = current - questionId
        } else {
            reviewMarked.value = current + questionId
        }
    }

    fun goToQuestion(index: Int) {
        val qList = currentQuestions.value
        if (index in qList.indices) {
            currentQuestionIndex.value = index
            val q = qList[index]
            visitedQuestions.value = visitedQuestions.value + q.id
        }
    }

    fun nextQuestion() {
        goToQuestion(currentQuestionIndex.value + 1)
    }

    fun previousQuestion() {
        goToQuestion(currentQuestionIndex.value - 1)
    }

    fun submitTest() {
        if (!isTestActive.value) return
        timerJob?.cancel()
        isTestActive.value = false

        val test = currentTest.value ?: return
        val questions = currentQuestions.value
        val answers = userAnswers.value
        val totalSecs = (test.durationMinutes * 60).toLong()
        val timeTaken = totalSecs - timeRemainingSeconds.value

        viewModelScope.launch {
            val result = repository.submitTestAttempt(
                testId = test.id,
                testTitle = test.title,
                topicId = test.topicId,
                subjectId = test.subjectId,
                totalQuestions = questions.size,
                userAnswers = answers,
                questions = questions,
                timeTakenSeconds = if (timeTaken > 0) timeTaken else 1L
            )
            lastSubmittedResult.value = result
        }
    }

    // Bookmark toggles
    fun toggleBookmark(questionId: Long, isCurrentlyBookmarked: Boolean) {
        viewModelScope.launch {
            repository.toggleBookmark(questionId, isCurrentlyBookmarked)
        }
    }

    fun isQuestionBookmarked(questionId: Long): Flow<Boolean> {
        return repository.isBookmarked(questionId)
    }

    fun removeWrongQuestion(questionId: Long) {
        viewModelScope.launch {
            repository.removeWrongQuestion(questionId)
        }
    }

    // Admin & Auth
    val isAdminLoggedIn = MutableStateFlow(false)
    val adminPassword = MutableStateFlow("")

    fun loginAdmin(pin: String): Boolean {
        if (pin == "1234" || pin == "admin" || pin == "rajasthan") {
            isAdminLoggedIn.value = true
            return true
        }
        return false
    }

    fun logoutAdmin() {
        isAdminLoggedIn.value = false
    }

    // Admin Exam Category CRUD
    fun addExamCategory(name: String, hindiName: String) {
        viewModelScope.launch {
            val currentCount = exams.value.size
            repository.insertExam(
                ExamCategoryEntity(
                    name = name,
                    nameHindi = hindiName,
                    isPopular = true,
                    orderIndex = currentCount + 1
                )
            )
        }
    }

    fun updateExamCategory(exam: ExamCategoryEntity) {
        viewModelScope.launch {
            repository.updateExam(exam)
        }
    }

    fun deleteExamCategory(exam: ExamCategoryEntity) {
        viewModelScope.launch {
            repository.deleteExam(exam)
        }
    }

    // Admin Subject CRUD
    fun addSubject(nameHindi: String, nameEnglish: String) {
        viewModelScope.launch {
            val count = subjects.value.size
            repository.insertSubject(
                SubjectEntity(
                    code = "SUB_${count + 1}",
                    nameHindi = nameHindi,
                    nameEnglish = nameEnglish,
                    orderIndex = count + 1
                )
            )
        }
    }

    fun updateSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            repository.updateSubject(subject)
        }
    }

    fun deleteSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }

    // Admin Topic CRUD
    fun addTopic(subjectId: Long, nameHindi: String, nameEnglish: String) {
        viewModelScope.launch {
            repository.insertTopic(
                TopicEntity(
                    subjectId = subjectId,
                    nameHindi = nameHindi,
                    nameEnglish = nameEnglish,
                    orderIndex = 100
                )
            )
        }
    }

    fun updateTopic(topic: TopicEntity) {
        viewModelScope.launch {
            repository.updateTopic(topic)
        }
    }

    fun deleteTopic(topic: TopicEntity) {
        viewModelScope.launch {
            repository.deleteTopic(topic)
        }
    }

    // Admin Question CRUD
    fun addOrUpdateQuestion(question: QuestionEntity) {
        viewModelScope.launch {
            if (question.id == 0L) {
                repository.insertQuestion(question)
            } else {
                repository.updateQuestion(question)
            }
        }
    }

    fun deleteQuestion(question: QuestionEntity) {
        viewModelScope.launch {
            repository.deleteQuestion(question)
        }
    }

    // PDF / Text Question Extraction
    val extractedDrafts = MutableStateFlow<List<ExtractedQuestionDraft>>(emptyList())
    val isExtracting = MutableStateFlow(false)
    val extractionMessage = MutableStateFlow("")

    fun processPdfText(
        rawText: String,
        fileName: String,
        examName: String = "Rajasthan Teacher Exams",
        examYear: Int = 2024
    ) {
        viewModelScope.launch {
            isExtracting.value = true
            extractionMessage.value = "दस्तावेज़ का विश्लेषण एवं प्रश्न निष्कर्षण जारी है..."
            delay(600) // Brief feedback for UI responsiveness

            val parsed = PdfQuestionExtractor.parseTextToQuestions(
                rawText = rawText,
                defaultExamName = examName,
                defaultExamYear = examYear,
                allSubjects = subjects.value,
                allTopics = allTopics.value
            )

            // Duplicate detection check
            val existing = repository.allQuestions.first()
            val drafts = parsed.map { draft ->
                val (isDup, reason) = PdfQuestionExtractor.checkDuplicate(draft, existing)
                draft.copy(isDuplicate = isDup, duplicateReason = reason)
            }

            extractedDrafts.value = drafts
            isExtracting.value = false

            // Record upload
            repository.insertPdfUpload(
                PdfUploadEntity(
                    fileName = fileName,
                    totalExtracted = drafts.size,
                    approvedCount = 0,
                    status = "REVIEW_PENDING"
                )
            )

            extractionMessage.value = "सफलतापूर्वक ${drafts.size} प्रश्न निकाले गए! कृपया समीक्षा करें।"
        }
    }

    fun updateExtractedDraft(updated: ExtractedQuestionDraft) {
        val current = extractedDrafts.value.toMutableList()
        val index = current.indexOfFirst { it.tempId == updated.tempId }
        if (index != -1) {
            current[index] = updated
            extractedDrafts.value = current
        }
    }

    fun deleteExtractedDraft(tempId: Long) {
        extractedDrafts.value = extractedDrafts.value.filter { it.tempId != tempId }
    }

    fun approveDraft(draft: ExtractedQuestionDraft) {
        viewModelScope.launch {
            val entity = QuestionEntity(
                questionText = draft.questionText,
                optionA = draft.optionA,
                optionB = draft.optionB,
                optionC = draft.optionC,
                optionD = draft.optionD,
                correctAnswer = draft.correctAnswer,
                explanation = draft.explanation,
                subjectId = draft.subjectId,
                topicId = draft.topicId,
                examId = draft.examId,
                examName = draft.examName,
                examYear = draft.examYear,
                difficulty = draft.difficulty,
                sourceType = "Imported",
                questionNumber = draft.questionNumber,
                status = "APPROVED"
            )
            repository.insertQuestion(entity)
            deleteExtractedDraft(draft.tempId)
        }
    }

    fun approveAllNonDuplicateDrafts() {
        viewModelScope.launch {
            val toApprove = extractedDrafts.value.filter { !it.isDuplicate }
            val entities = toApprove.map { draft ->
                QuestionEntity(
                    questionText = draft.questionText,
                    optionA = draft.optionA,
                    optionB = draft.optionB,
                    optionC = draft.optionC,
                    optionD = draft.optionD,
                    correctAnswer = draft.correctAnswer,
                    explanation = draft.explanation,
                    subjectId = draft.subjectId,
                    topicId = draft.topicId,
                    examId = draft.examId,
                    examName = draft.examName,
                    examYear = draft.examYear,
                    difficulty = draft.difficulty,
                    sourceType = "Imported",
                    questionNumber = draft.questionNumber,
                    status = "APPROVED"
                )
            }
            repository.insertQuestions(entities)
            extractedDrafts.value = extractedDrafts.value.filter { it.isDuplicate }
        }
    }

    // Auto-Generate Tests for Topic
    val autoTestGenerationResult = MutableStateFlow<String?>(null)

    fun autoGenerateTopicTests(subjectId: Long, topicId: Long, topicTitle: String, numTests: Int = 5) {
        viewModelScope.launch {
            val result = repository.autoGenerateTestsForTopic(
                subjectId = subjectId,
                topicId = topicId,
                numTests = numTests,
                questionsPerTest = 25,
                topicTitle = topicTitle
            )
            result.onSuccess { count ->
                autoTestGenerationResult.value = "$count नए टेस्ट सफलतापूर्वक जनरेट किए गए!"
            }.onFailure { err ->
                autoTestGenerationResult.value = "त्रुटि: ${err.message}"
            }
        }
    }

    fun clearAutoTestResult() {
        autoTestGenerationResult.value = null
    }

    // Send Notification
    fun sendNotification(title: String, message: String, targetSubjectId: Long? = null) {
        viewModelScope.launch {
            repository.sendNotification(title, message, targetSubjectId)
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    // User Profile
    fun updateUserProfile(name: String, email: String, phone: String, targetExam: String) {
        viewModelScope.launch {
            repository.saveUserProfile(
                UserProfileEntity(
                    id = "default_user",
                    name = name,
                    email = email,
                    phone = phone,
                    targetExam = targetExam,
                    role = "USER"
                )
            )
        }
    }
}

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
