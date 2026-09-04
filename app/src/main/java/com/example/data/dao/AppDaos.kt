package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams ORDER BY order_index ASC")
    fun getAllExams(): Flow<List<ExamCategoryEntity>>

    @Query("SELECT * FROM exams WHERE id = :id LIMIT 1")
    suspend fun getExamById(id: Long): ExamCategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamCategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExams(exams: List<ExamCategoryEntity>)

    @Update
    suspend fun updateExam(exam: ExamCategoryEntity)

    @Delete
    suspend fun deleteExam(exam: ExamCategoryEntity)
}

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY order_index ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    suspend fun getSubjectById(id: Long): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)

    @Query("SELECT COUNT(*) FROM subjects")
    suspend fun getSubjectsCount(): Int
}

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics WHERE subject_id = :subjectId ORDER BY order_index ASC")
    fun getTopicsForSubject(subjectId: Long): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics ORDER BY order_index ASC")
    fun getAllTopics(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE id = :id LIMIT 1")
    suspend fun getTopicById(id: Long): TopicEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: TopicEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<TopicEntity>)

    @Update
    suspend fun updateTopic(topic: TopicEntity)

    @Delete
    suspend fun deleteTopic(topic: TopicEntity)

    @Query("SELECT COUNT(*) FROM topics")
    suspend fun getTopicsCount(): Int
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE topic_id = :topicId AND status = 'APPROVED' ORDER BY id ASC")
    fun getQuestionsForTopic(topicId: Long): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE topic_id = :topicId AND status = 'APPROVED'")
    suspend fun getApprovedQuestionsForTopic(topicId: Long): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE subject_id = :subjectId AND status = 'APPROVED'")
    fun getQuestionsForSubject(subjectId: Long): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions ORDER BY id DESC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE status = :status ORDER BY id DESC")
    fun getQuestionsByStatus(status: String): Flow<List<QuestionEntity>>

    @Query("""
        SELECT * FROM questions 
        WHERE source_type = 'Previous Year' 
          AND (:examId IS NULL OR exam_id = :examId)
          AND (:examYear IS NULL OR exam_year = :examYear)
          AND (:subjectId IS NULL OR subject_id = :subjectId)
          AND (:topicId IS NULL OR topic_id = :topicId)
        ORDER BY exam_year DESC, id ASC
    """)
    fun getPreviousYearQuestions(
        examId: Long?,
        examYear: Int?,
        subjectId: Long?,
        topicId: Long?
    ): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE topic_id = :topicId AND source_type = 'Previous Year' ORDER BY exam_year DESC, id ASC")
    fun getTopicPreviousYearQuestions(topicId: Long): Flow<List<QuestionEntity>>

    @Query("""
        SELECT * FROM questions 
        WHERE (question_text LIKE '%' || :query || '%' 
           OR explanation LIKE '%' || :query || '%'
           OR exam_name LIKE '%' || :query || '%')
        ORDER BY id DESC
    """)
    fun searchQuestions(query: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id IN (:ids)")
    suspend fun getQuestionsByIds(ids: List<Long>): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE id = :id LIMIT 1")
    suspend fun getQuestionById(id: Long): QuestionEntity?

    @Query("SELECT * FROM questions WHERE question_text = :text LIMIT 1")
    suspend fun findExactDuplicate(text: String): QuestionEntity?

    @Query("SELECT * FROM questions WHERE question_text LIKE '%' || :keyword || '%' LIMIT 5")
    suspend fun findNearDuplicates(keyword: String): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Delete
    suspend fun deleteQuestion(question: QuestionEntity)

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteQuestionById(id: Long)

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionsCount(): Int

    @Query("SELECT COUNT(*) FROM questions WHERE status = 'PENDING'")
    fun getPendingQuestionsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM questions WHERE status = 'APPROVED'")
    fun getApprovedQuestionsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM questions WHERE source_type = 'Previous Year'")
    fun getPyqQuestionsCount(): Flow<Int>
}

@Dao
interface TestDao {
    @Query("SELECT * FROM tests ORDER BY id DESC")
    fun getAllTests(): Flow<List<TestEntity>>

    @Query("SELECT * FROM tests WHERE topic_id = :topicId ORDER BY id ASC")
    fun getTestsForTopic(topicId: Long): Flow<List<TestEntity>>

    @Query("SELECT * FROM tests WHERE is_daily_test = 1 ORDER BY id DESC")
    fun getDailyTests(): Flow<List<TestEntity>>

    @Query("SELECT * FROM tests WHERE is_mock_test = 1 ORDER BY id DESC")
    fun getMockTests(): Flow<List<TestEntity>>

    @Query("SELECT * FROM tests WHERE id = :id LIMIT 1")
    suspend fun getTestById(id: Long): TestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: TestEntity): Long

    @Delete
    suspend fun deleteTest(test: TestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestQuestionCrossRefs(refs: List<TestQuestionCrossRef>)

    @Query("""
        SELECT q.* FROM questions q 
        INNER JOIN test_questions tq ON q.id = tq.question_id 
        WHERE tq.test_id = :testId 
        ORDER BY tq.order_index ASC
    """)
    suspend fun getQuestionsForTest(testId: Long): List<QuestionEntity>

    @Query("SELECT * FROM test_attempts ORDER BY start_time DESC")
    fun getTestAttempts(): Flow<List<TestAttemptEntity>>

    @Query("SELECT * FROM test_attempts WHERE topic_id = :topicId ORDER BY score DESC LIMIT 1")
    suspend fun getBestScoreForTopic(topicId: Long): TestAttemptEntity?

    @Query("SELECT * FROM test_attempts WHERE test_id = :testId ORDER BY start_time DESC LIMIT 1")
    suspend fun getLatestAttemptForTest(testId: Long): TestAttemptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestAttempt(attempt: TestAttemptEntity): Long

    @Query("SELECT COUNT(*) FROM tests")
    suspend fun getTestsCount(): Int
}

@Dao
interface StudyDao {
    @Query("""
        SELECT q.* FROM questions q 
        INNER JOIN bookmarks b ON q.id = b.questionId 
        ORDER BY b.timestamp DESC
    """)
    fun getBookmarkedQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE questionId = :questionId)")
    fun isBookmarked(questionId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE questionId = :questionId")
    suspend fun removeBookmark(questionId: Long)

    @Query("""
        SELECT q.* FROM questions q 
        INNER JOIN wrong_questions w ON q.id = w.questionId 
        ORDER BY w.last_attempted_at DESC
    """)
    fun getWrongQuestions(): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordWrongQuestion(wrongQuestion: WrongQuestionEntity)

    @Query("DELETE FROM wrong_questions WHERE questionId = :questionId")
    suspend fun removeWrongQuestion(questionId: Long)

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotificationEntity): Long

    @Query("UPDATE notifications SET is_read = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Long)

    @Query("SELECT * FROM user_profile WHERE id = 'default_user' LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM pdf_uploads ORDER BY upload_date DESC")
    fun getAllPdfUploads(): Flow<List<PdfUploadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPdfUpload(upload: PdfUploadEntity): Long

    @Update
    suspend fun updatePdfUpload(upload: PdfUploadEntity)
}
