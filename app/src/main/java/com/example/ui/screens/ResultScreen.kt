package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.ExamViewModel
import java.util.Locale

@Composable
fun ResultScreen(
    viewModel: ExamViewModel,
    onRetryTest: () -> Unit,
    onBackToHome: () -> Unit
) {
    val result by viewModel.lastSubmittedResult.collectAsState()
    val questions by viewModel.currentQuestions.collectAsState()
    val userAnswers by viewModel.userAnswers.collectAsState()
    val currentTest by viewModel.currentTest.collectAsState()

    var showReviewList by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "परीक्षा परिणाम व विश्लेषण",
                subtitle = result?.testTitle ?: "टेस्ट पूर्ण",
                showBack = true,
                onBack = onBackToHome
            )
        }
    ) { padding ->
        if (result == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("परिणाम लोड हो रहा है...")
            }
        } else {
            val res = result!!
            val timeMinutes = res.timeTakenSeconds / 60
            val timeSecs = res.timeTakenSeconds % 60
            val timeStr = String.format(Locale.getDefault(), "%02d:%02d", timeMinutes, timeSecs)

            val percentile = when {
                res.percentage >= 90f -> "99.2%"
                res.percentage >= 80f -> "95.5%"
                res.percentage >= 70f -> "88.4%"
                res.percentage >= 60f -> "76.1%"
                else -> "58.0%"
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(BackgroundLight),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Score Summary Hero Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("result_summary_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(if (res.percentage >= 60f) GreenLight else Color(0xFFFEF3C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (res.percentage >= 60f) Icons.Default.EmojiEvents else Icons.Default.ThumbUp,
                                    contentDescription = null,
                                    tint = if (res.percentage >= 60f) ForestGreen else SaffronAmber,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (res.percentage >= 75f) "शानदार प्रदर्शन! (Excellent)"
                                else if (res.percentage >= 50f) "अच्छा प्रयास! (Good Effort)"
                                else "अधिक अभ्यास की आवश्यकता है",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "प्राप्तांक: ${res.score} / ${res.totalQuestions}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28.sp,
                                color = RoyalNavy
                            )
                            Text(
                                text = "प्रतिशत: ${String.format(Locale.getDefault(), "%.1f", res.percentage)}% • अनुमानित पर्सेंटाइल: $percentile",
                                fontSize = 13.sp,
                                color = NeutralMedium
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = DividerColor)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Grid of 4 Key Stats
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                ResultStatBox(label = "सही उत्तर", value = "${res.correct}", color = ForestGreen)
                                ResultStatBox(label = "गलत उत्तर", value = "${res.incorrect}", color = TerracottaRed)
                                ResultStatBox(label = "छोड़े गए", value = "${res.unattempted}", color = NeutralMedium)
                                ResultStatBox(label = "सटीकता", value = "${res.accuracy.toInt()}%", color = RoyalBlueLight)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "लिया गया समय: $timeStr मिनट",
                                fontSize = 12.sp,
                                color = NeutralMedium
                            )
                        }
                    }
                }

                // 2. Action Buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showReviewList = !showReviewList },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("review_answers_button")
                        ) {
                            Icon(
                                imageVector = if (showReviewList) Icons.Default.VisibilityOff else Icons.Default.Assignment,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (showReviewList) "समीक्षा बंद करें" else "उत्तर एवं व्याख्या देखें")
                        }

                        OutlinedButton(
                            onClick = {
                                if (currentTest != null) {
                                    viewModel.startTest(currentTest!!)
                                    onRetryTest()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("retry_test_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("पुनः टेस्ट दें")
                        }
                    }
                }

                // 3. Detailed Question-by-Question Review Section
                if (showReviewList) {
                    item {
                        Text(
                            text = "प्रश्नवार विस्तृत समीक्षा (Question-by-Question Review)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = NeutralDark,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    itemsIndexed(questions) { idx, q ->
                        val userChoice = userAnswers[q.id]
                        val isCorrect = userChoice?.equals(q.correctAnswer, ignoreCase = true) == true
                        val isSkipped = userChoice == null

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("review_question_${idx + 1}")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StatusBadge(
                                        text = "प्रश्न ${idx + 1} / ${questions.size}",
                                        containerColor = Color(0xFFDBEAFE),
                                        contentColor = RoyalNavy
                                    )

                                    val (statusText, statusBg, statusColor) = when {
                                        isSkipped -> Triple("अनुत्तरित (Skipped)", Color(0xFFF1F5F9), NeutralMedium)
                                        isCorrect -> Triple("सही (+1)", GreenLight, ForestGreen)
                                        else -> Triple("गलत (-0.33)", TerracottaLight, TerracottaRed)
                                    }

                                    StatusBadge(
                                        text = statusText,
                                        containerColor = statusBg,
                                        contentColor = statusColor
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = q.questionText,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Options Review
                                val opts = listOf(
                                    "A" to q.optionA,
                                    "B" to q.optionB,
                                    "C" to q.optionC,
                                    "D" to q.optionD
                                )

                                opts.forEach { (key, text) ->
                                    val isUserChoice = userChoice == key
                                    val isAnswerKey = q.correctAnswer.equals(key, ignoreCase = true)

                                    val bg = when {
                                        isAnswerKey -> Color(0xFFD1FAE5)
                                        isUserChoice && !isAnswerKey -> Color(0xFFFEE2E2)
                                        else -> Color(0xFFF8FAFC)
                                    }

                                    val border = when {
                                        isAnswerKey -> ForestGreen
                                        isUserChoice && !isAnswerKey -> TerracottaRed
                                        else -> Color(0xFFE2E8F0)
                                    }

                                    Surface(
                                        color = bg,
                                        shape = RoundedCornerShape(8.dp),
                                        border = CardDefaults.outlinedCardBorder().copy(
                                            brush = androidx.compose.ui.graphics.SolidColor(border)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "($key) $text",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (isAnswerKey) {
                                                Text("सही उत्तर ✓", color = ForestGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            } else if (isUserChoice) {
                                                Text("आपका उत्तर ✗", color = TerracottaRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                // Detailed Explanation
                                if (q.explanation.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        color = Color(0xFFF0FDF4),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "विस्तृत व्याख्या:",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = ForestGreen
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = q.explanation,
                                                fontSize = 12.sp,
                                                color = NeutralDark,
                                                lineHeight = 18.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Return to Home button
                item {
                    OutlinedButton(
                        onClick = onBackToHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .testTag("back_to_home_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("होम स्क्रीन पर लौटें (Back to Home)")
                    }
                }
            }
        }
    }
}

@Composable
fun ResultStatBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = NeutralMedium
        )
    }
}
