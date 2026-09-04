package com.example.data.repository

import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class ExamRepository(
    val examDao: ExamDao,
    val subjectDao: SubjectDao,
    val topicDao: TopicDao,
    val questionDao: QuestionDao,
    val testDao: TestDao,
    val studyDao: StudyDao
) {
    // Exams
    val allExams: Flow<List<ExamCategoryEntity>> = examDao.getAllExams()
    suspend fun getExamById(id: Long) = examDao.getExamById(id)
    suspend fun insertExam(exam: ExamCategoryEntity) = examDao.insertExam(exam)
    suspend fun updateExam(exam: ExamCategoryEntity) = examDao.updateExam(exam)
    suspend fun deleteExam(exam: ExamCategoryEntity) = examDao.deleteExam(exam)

    // Subjects
    val allSubjects: Flow<List<SubjectEntity>> = subjectDao.getAllSubjects()
    suspend fun getSubjectById(id: Long) = subjectDao.getSubjectById(id)
    suspend fun insertSubject(subject: SubjectEntity) = subjectDao.insertSubject(subject)
    suspend fun updateSubject(subject: SubjectEntity) = subjectDao.updateSubject(subject)
    suspend fun deleteSubject(subject: SubjectEntity) = subjectDao.deleteSubject(subject)

    // Topics
    fun getTopicsForSubject(subjectId: Long): Flow<List<TopicEntity>> = topicDao.getTopicsForSubject(subjectId)
    val allTopics: Flow<List<TopicEntity>> = topicDao.getAllTopics()
    suspend fun getTopicById(id: Long) = topicDao.getTopicById(id)
    suspend fun insertTopic(topic: TopicEntity) = topicDao.insertTopic(topic)
    suspend fun updateTopic(topic: TopicEntity) = topicDao.updateTopic(topic)
    suspend fun deleteTopic(topic: TopicEntity) = topicDao.deleteTopic(topic)

    // Questions
    fun getQuestionsForTopic(topicId: Long): Flow<List<QuestionEntity>> = questionDao.getQuestionsForTopic(topicId)
    fun getQuestionsForSubject(subjectId: Long): Flow<List<QuestionEntity>> = questionDao.getQuestionsForSubject(subjectId)
    val allQuestions: Flow<List<QuestionEntity>> = questionDao.getAllQuestions()
    fun getQuestionsByStatus(status: String): Flow<List<QuestionEntity>> = questionDao.getQuestionsByStatus(status)
    val pendingQuestionsCount: Flow<Int> = questionDao.getPendingQuestionsCount()
    val approvedQuestionsCount: Flow<Int> = questionDao.getApprovedQuestionsCount()
    val pyqQuestionsCount: Flow<Int> = questionDao.getPyqQuestionsCount()

    fun getPreviousYearQuestions(
        examId: Long?,
        examYear: Int?,
        subjectId: Long?,
        topicId: Long?
    ): Flow<List<QuestionEntity>> = questionDao.getPreviousYearQuestions(examId, examYear, subjectId, topicId)

    fun getTopicPreviousYearQuestions(topicId: Long): Flow<List<QuestionEntity>> = questionDao.getTopicPreviousYearQuestions(topicId)

    fun searchQuestions(query: String): Flow<List<QuestionEntity>> = questionDao.searchQuestions(query)

    suspend fun getQuestionById(id: Long) = questionDao.getQuestionById(id)
    suspend fun insertQuestion(question: QuestionEntity) = questionDao.insertQuestion(question)
    suspend fun insertQuestions(questions: List<QuestionEntity>) = questionDao.insertQuestions(questions)
    suspend fun updateQuestion(question: QuestionEntity) = questionDao.updateQuestion(question)
    suspend fun deleteQuestion(question: QuestionEntity) = questionDao.deleteQuestion(question)
    suspend fun deleteQuestionById(id: Long) = questionDao.deleteQuestionById(id)

    suspend fun findExactDuplicate(text: String): QuestionEntity? = questionDao.findExactDuplicate(text.trim())
    suspend fun findNearDuplicates(keyword: String): List<QuestionEntity> = questionDao.findNearDuplicates(keyword.trim())

    // Tests
    val allTests: Flow<List<TestEntity>> = testDao.getAllTests()
    fun getTestsForTopic(topicId: Long): Flow<List<TestEntity>> = testDao.getTestsForTopic(topicId)
    val dailyTests: Flow<List<TestEntity>> = testDao.getDailyTests()
    val mockTests: Flow<List<TestEntity>> = testDao.getMockTests()
    suspend fun getTestById(id: Long) = testDao.getTestById(id)
    suspend fun getQuestionsForTest(testId: Long) = testDao.getQuestionsForTest(testId)

    suspend fun createCustomTest(
        title: String,
        subjectId: Long,
        topicId: Long,
        questionIds: List<Long>,
        durationMinutes: Int = 10
    ): Long {
        val test = TestEntity(
            title = title,
            subjectId = subjectId,
            topicId = topicId,
            totalQuestions = questionIds.size,
            durationMinutes = durationMinutes
        )
        val testId = testDao.insertTest(test)
        val refs = questionIds.mapIndexed { index, qId ->
            TestQuestionCrossRef(testId = testId, questionId = qId, orderIndex = index + 1)
        }
        testDao.insertTestQuestionCrossRefs(refs)
        return testId
    }

    suspend fun autoGenerateTestsForTopic(
        subjectId: Long,
        topicId: Long,
        numTests: Int = 5,
        questionsPerTest: Int = 25,
        topicTitle: String
    ): Result<Int> {
        val approved = questionDao.getApprovedQuestionsForTopic(topicId)
        if (approved.isEmpty()) {
            return Result.failure(Exception("इस टॉपिक में कोई स्वीकृत (Approved) प्रश्न उपलब्ध नहीं हैं।"))
        }

        var createdCount = 0
        val shuffledPool = approved.shuffled()

        for (i in 1..numTests) {
            val selected = if (shuffledPool.size >= questionsPerTest) {
                val start = ((i - 1) * questionsPerTest) % shuffledPool.size
                val sub = mutableListOf<QuestionEntity>()
                for (j in 0 until questionsPerTest) {
                    sub.add(shuffledPool[(start + j) % shuffledPool.size])
                }
                sub
            } else {
                shuffledPool.take(questionsPerTest)
            }

            val testTitle = "$topicTitle - टेस्ट $i"
            val test = TestEntity(
                title = testTitle,
                subjectId = subjectId,
                topicId = topicId,
                totalQuestions = selected.size,
                durationMinutes = 10
            )
            val testId = testDao.insertTest(test)
            val refs = selected.mapIndexed { index, q ->
                TestQuestionCrossRef(testId = testId, questionId = q.id, orderIndex = index + 1)
            }
            testDao.insertTestQuestionCrossRefs(refs)
            createdCount++
        }

        return Result.success(createdCount)
    }

    // Results & Attempts
    val testAttempts: Flow<List<TestAttemptEntity>> = testDao.getTestAttempts()
    suspend fun getBestScoreForTopic(topicId: Long) = testDao.getBestScoreForTopic(topicId)
    suspend fun getLatestAttemptForTest(testId: Long) = testDao.getLatestAttemptForTest(testId)

    suspend fun submitTestAttempt(
        testId: Long,
        testTitle: String,
        topicId: Long,
        subjectId: Long,
        totalQuestions: Int,
        userAnswers: Map<Long, String>,
        questions: List<QuestionEntity>,
        timeTakenSeconds: Long
    ): TestAttemptEntity {
        var correct = 0
        var incorrect = 0

        for (q in questions) {
            val userAns = userAnswers[q.id]
            if (userAns != null) {
                if (userAns.equals(q.correctAnswer, ignoreCase = true)) {
                    correct++
                } else {
                    incorrect++
                    // Record in wrong questions
                    studyDao.recordWrongQuestion(
                        WrongQuestionEntity(
                            questionId = q.id,
                            lastAttemptedAt = System.currentTimeMillis()
                        )
                    )
                }
            }
        }

        val attempted = userAnswers.size
        val unattempted = totalQuestions - attempted
        val score = correct
        val percentage = if (totalQuestions > 0) (correct.toFloat() / totalQuestions) * 100f else 0f
        val accuracy = if (attempted > 0) (correct.toFloat() / attempted) * 100f else 0f
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateStr = dateFormat.format(Date())

        val attempt = TestAttemptEntity(
            testId = testId,
            testTitle = testTitle,
            topicId = topicId,
            subjectId = subjectId,
            date = dateStr,
            startTime = System.currentTimeMillis() - (timeTakenSeconds * 1000),
            endTime = System.currentTimeMillis(),
            totalQuestions = totalQuestions,
            attempted = attempted,
            correct = correct,
            incorrect = incorrect,
            unattempted = unattempted,
            score = score,
            percentage = percentage,
            accuracy = accuracy,
            timeTakenSeconds = timeTakenSeconds
        )

        val attemptId = testDao.insertTestAttempt(attempt)
        return attempt.copy(id = attemptId)
    }

    // Bookmarks
    val bookmarkedQuestions: Flow<List<QuestionEntity>> = studyDao.getBookmarkedQuestions()
    fun isBookmarked(questionId: Long): Flow<Boolean> = studyDao.isBookmarked(questionId)
    suspend fun toggleBookmark(questionId: Long, isCurrentlyBookmarked: Boolean) {
        if (isCurrentlyBookmarked) {
            studyDao.removeBookmark(questionId)
        } else {
            studyDao.addBookmark(BookmarkEntity(questionId = questionId))
        }
    }

    // Wrong Questions
    val wrongQuestions: Flow<List<QuestionEntity>> = studyDao.getWrongQuestions()
    suspend fun removeWrongQuestion(questionId: Long) = studyDao.removeWrongQuestion(questionId)

    // Notifications
    val allNotifications: Flow<List<AppNotificationEntity>> = studyDao.getAllNotifications()
    suspend fun sendNotification(title: String, message: String, targetSubjectId: Long? = null) {
        studyDao.insertNotification(
            AppNotificationEntity(
                title = title,
                message = message,
                targetSubjectId = targetSubjectId
            )
        )
    }
    suspend fun markNotificationRead(id: Long) = studyDao.markNotificationRead(id)

    // User Profile
    val userProfile: Flow<UserProfileEntity?> = studyDao.getUserProfile()
    suspend fun saveUserProfile(profile: UserProfileEntity) = studyDao.saveUserProfile(profile)

    // PDF Uploads
    val allPdfUploads: Flow<List<PdfUploadEntity>> = studyDao.getAllPdfUploads()
    suspend fun insertPdfUpload(upload: PdfUploadEntity) = studyDao.insertPdfUpload(upload)
    suspend fun updatePdfUpload(upload: PdfUploadEntity) = studyDao.updatePdfUpload(upload)
}
