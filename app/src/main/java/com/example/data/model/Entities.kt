package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "exams")
data class ExamCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "name_hindi") val nameHindi: String,
    @ColumnInfo(name = "is_popular") val isPopular: Boolean = true,
    @ColumnInfo(name = "order_index") val orderIndex: Int = 0
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    @ColumnInfo(name = "name_hindi") val nameHindi: String,
    @ColumnInfo(name = "name_english") val nameEnglish: String,
    @ColumnInfo(name = "icon_name") val iconName: String = "menu_book",
    @ColumnInfo(name = "order_index") val orderIndex: Int = 0
)

@Entity(
    tableName = "topics",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subject_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subject_id"])]
)
data class TopicEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "subject_id") val subjectId: Long,
    @ColumnInfo(name = "name_hindi") val nameHindi: String,
    @ColumnInfo(name = "name_english") val nameEnglish: String = "",
    @ColumnInfo(name = "order_index") val orderIndex: Int = 0
)

@Entity(
    tableName = "questions",
    indices = [
        Index(value = ["subject_id"]),
        Index(value = ["topic_id"]),
        Index(value = ["exam_id"]),
        Index(value = ["exam_year"]),
        Index(value = ["status"])
    ]
)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "question_text") val questionText: String,
    @ColumnInfo(name = "option_a") val optionA: String,
    @ColumnInfo(name = "option_b") val optionB: String,
    @ColumnInfo(name = "option_c") val optionC: String,
    @ColumnInfo(name = "option_d") val optionD: String,
    @ColumnInfo(name = "correct_answer") val correctAnswer: String, // "A", "B", "C", "D"
    val explanation: String = "",
    @ColumnInfo(name = "subject_id") val subjectId: Long,
    @ColumnInfo(name = "topic_id") val topicId: Long,
    @ColumnInfo(name = "subtopic_id") val subtopicId: Long = 0,
    @ColumnInfo(name = "exam_id") val examId: Long = 1,
    @ColumnInfo(name = "exam_name") val examName: String = "Rajasthan Teacher Exams",
    @ColumnInfo(name = "exam_year") val examYear: Int = 2024,
    val difficulty: String = "Medium", // "Easy", "Medium", "Hard"
    @ColumnInfo(name = "source_type") val sourceType: String = "Previous Year", // "Previous Year", "Practice", "Imported"
    @ColumnInfo(name = "source_pdf") val sourcePdf: String = "",
    @ColumnInfo(name = "question_number") val questionNumber: Int = 1,
    val status: String = "APPROVED", // "APPROVED", "PENDING", "DRAFT"
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tests")
data class TestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    @ColumnInfo(name = "subject_id") val subjectId: Long,
    @ColumnInfo(name = "topic_id") val topicId: Long,
    @ColumnInfo(name = "total_questions") val totalQuestions: Int = 25,
    @ColumnInfo(name = "duration_minutes") val durationMinutes: Int = 10,
    @ColumnInfo(name = "is_daily_test") val isDailyTest: Boolean = false,
    @ColumnInfo(name = "is_mock_test") val isMockTest: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "test_questions",
    primaryKeys = ["test_id", "question_id"],
    indices = [Index(value = ["question_id"])]
)
data class TestQuestionCrossRef(
    @ColumnInfo(name = "test_id") val testId: Long,
    @ColumnInfo(name = "question_id") val questionId: Long,
    @ColumnInfo(name = "order_index") val orderIndex: Int
)

@Entity(tableName = "test_attempts")
data class TestAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "test_id") val testId: Long,
    @ColumnInfo(name = "test_title") val testTitle: String,
    @ColumnInfo(name = "topic_id") val topicId: Long,
    @ColumnInfo(name = "subject_id") val subjectId: Long,
    @ColumnInfo(name = "user_id") val userId: String = "default_user",
    val date: String,
    @ColumnInfo(name = "start_time") val startTime: Long,
    @ColumnInfo(name = "end_time") val endTime: Long,
    @ColumnInfo(name = "total_questions") val totalQuestions: Int,
    val attempted: Int,
    val correct: Int,
    val incorrect: Int,
    val unattempted: Int,
    val score: Int,
    val percentage: Float,
    val accuracy: Float,
    @ColumnInfo(name = "time_taken_seconds") val timeTakenSeconds: Long
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val questionId: Long,
    @ColumnInfo(name = "user_id") val userId: String = "default_user",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "wrong_questions")
data class WrongQuestionEntity(
    @PrimaryKey val questionId: Long,
    @ColumnInfo(name = "user_id") val userId: String = "default_user",
    @ColumnInfo(name = "last_attempted_at") val lastAttemptedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "wrong_count") val wrongCount: Int = 1
)

@Entity(tableName = "pdf_uploads")
data class PdfUploadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "file_name") val fileName: String,
    @ColumnInfo(name = "upload_date") val uploadDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "total_extracted") val totalExtracted: Int = 0,
    @ColumnInfo(name = "approved_count") val approvedCount: Int = 0,
    val status: String = "COMPLETED" // "PROCESSING", "REVIEW_PENDING", "COMPLETED"
)

@Entity(tableName = "notifications")
data class AppNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_read") val isRead: Boolean = false,
    @ColumnInfo(name = "target_subject_id") val targetSubjectId: Long? = null
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "default_user",
    val name: String = "अभ्यर्थी (Rajasthan Aspirant)",
    val email: String = "aspirant@rajasthan.gov.in",
    val phone: String = "98290XXXXX",
    @ColumnInfo(name = "target_exam") val targetExam: String = "Rajasthan 3rd Grade Teacher / REET",
    val role: String = "USER" // "USER", "ADMIN"
)
