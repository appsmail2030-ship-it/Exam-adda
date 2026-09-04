package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.QuestionEntity
import com.example.data.model.TestEntity
import com.example.ui.components.AppTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.ExamViewModel
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun TopicDetailScreen(
    topicId: Long,
    viewModel: ExamViewModel,
    onBack: () -> Unit,
    onStartTest: (TestEntity) -> Unit
) {
    val allTopics by viewModel.allTopics.collectAsState()
    val allSubjects by viewModel.subjects.collectAsState()
    val allTests by viewModel.allTests.collectAsState()
    val testAttempts by viewModel.testAttempts.collectAsState()
    val autoTestResult by viewModel.autoTestGenerationResult.collectAsState()

    val topic = remember(allTopics, topicId) {
        allTopics.find { it.id == topicId }
    }
    val subject = remember(allSubjects, topic) {
        allSubjects.find { it.id == topic?.subjectId }
    }

    val topicTests = remember(allTests, topicId) {
        allTests.filter { it.topicId == topicId }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("ऑनलाइन टेस्ट", "विगत वर्ष प्रश्न (PYQ)", "सभी प्रश्न अभ्यास")

    val pyqFlow = remember(topicId) { viewModel.getTopicPyqs(topicId) }
    val pyqList by pyqFlow.collectAsState(initial = emptyList())

    // All topic questions
    var topicQuestions by remember { mutableStateOf<List<QuestionEntity>>(emptyList()) }
    LaunchedEffect(topicId) {
        // Collect once or continuously
        val list = viewModel.allQuestions.value.filter { it.topicId == topicId }
        topicQuestions = list
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = topic?.nameHindi ?: "टॉपिक विवरण",
                subtitle = subject?.nameHindi ?: "राजस्थान सामान्य ज्ञान",
                showBack = true,
                onBack = onBack
            )
        },
        snackbarHost = {
            if (autoTestResult != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearAutoTestResult() }) {
                            Text("ठीक है", color = Color.White)
                        }
                    }
                ) {
                    Text(autoTestResult ?: "")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
        ) {
            // Tab row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = RoyalNavy
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> {
                    // Tests Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = RoyalNavy,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "प्रत्येक टेस्ट में 25 प्रश्न एवं 10:00 मिनट का समय दिया जाएगा। समय पूर्ण होने पर स्वतः सबमिट होगा।",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = RoyalNavyDark
                                    )
                                }
                            }
                        }

                        if (topicTests.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Quiz,
                                            contentDescription = null,
                                            tint = SaffronAmber,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "इस टॉपिक हेतु अभी कोई टेस्ट उपलब्ध नहीं है।",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 15.sp
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "आप प्रश्न बैंक से ऑटोमैटिक 5 टेस्ट जनरेट कर सकते हैं।",
                                            fontSize = 13.sp,
                                            color = NeutralMedium
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = {
                                                if (topic != null) {
                                                    viewModel.autoGenerateTopicTests(
                                                        subjectId = topic.subjectId,
                                                        topicId = topic.id,
                                                        topicTitle = topic.nameHindi,
                                                        numTests = 5
                                                    )
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("auto_generate_tests_button")
                                        ) {
                                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("5 टेस्ट ऑटो जनरेट करें")
                                        }
                                    }
                                }
                            }
                        } else {
                            items(topicTests) { test ->
                                val lastAttempt = testAttempts.firstOrNull { it.testId == test.id }
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("test_card_${test.id}")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(if (lastAttempt != null) GreenLight else Color(0xFFFEF3C7)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (lastAttempt != null) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = if (lastAttempt != null) ForestGreen else SaffronAmber
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = test.title,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${test.totalQuestions} प्रश्न • ${test.durationMinutes} मिनट",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (lastAttempt != null) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "पिछला स्कोर: ${lastAttempt.score}/${lastAttempt.totalQuestions} (${lastAttempt.accuracy.toInt()}%)",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                                                    color = ForestGreen
                                                )
                                            }
                                        }
                                        Button(
                                            onClick = { onStartTest(test) },
                                            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("start_test_button_${test.id}")
                                        ) {
                                            Text(if (lastAttempt != null) "पुनः दें" else "शुरू करें")
                                        }
                                    }
                                }
                            }

                            item {
                                OutlinedButton(
                                    onClick = {
                                        if (topic != null) {
                                            viewModel.autoGenerateTopicTests(
                                                subjectId = topic.subjectId,
                                                topicId = topic.id,
                                                topicTitle = topic.nameHindi,
                                                numTests = 3
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .testTag("generate_more_tests_button"),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.AddCircleOutline, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("और टेस्ट जनरेट करें (Auto-Generate 3 Tests)")
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Topic PYQs Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "इस टॉपिक से पूछे गए विगत वर्ष प्रश्न (${pyqList.size} प्रश्न)",
                                fontWeight = FontWeight.Bold,
                                color = NeutralDark,
                                fontSize = 15.sp
                            )
                        }

                        if (pyqList.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "इस टॉपिक हेतु अलग से PYQ अभी दर्ज नहीं हैं। 'सभी प्रश्न अभ्यास' में प्रश्न देखें।",
                                            color = NeutralMedium,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        } else {
                            items(pyqList) { q ->
                                QuestionCardWithAnswerToggle(
                                    question = q,
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // Practice All Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val questions = if (topicQuestions.isNotEmpty()) topicQuestions
                        else viewModel.allQuestions.value.filter { it.topicId == topicId }

                        item {
                            Text(
                                text = "विषय अभ्यास प्रश्न बैंक (${questions.size} प्रश्न)",
                                fontWeight = FontWeight.Bold,
                                color = NeutralDark,
                                fontSize = 15.sp
                            )
                        }

                        items(questions) { q ->
                            QuestionCardWithAnswerToggle(
                                question = q,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionCardWithAnswerToggle(
    question: QuestionEntity,
    viewModel: ExamViewModel
) {
    var showAnswer by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    val isBookmarked by viewModel.isQuestionBookmarked(question.id).collectAsState(initial = false)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("question_practice_card_${question.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusBadge(
                        text = question.examName,
                        containerColor = Color(0xFFDBEAFE),
                        contentColor = RoyalNavy
                    )
                    StatusBadge(
                        text = "${question.examYear}",
                        containerColor = SaffronLight,
                        contentColor = SaffronAmber
                    )
                    StatusBadge(
                        text = question.difficulty,
                        containerColor = Color(0xFFF1F5F9),
                        contentColor = NeutralDark
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleBookmark(question.id, isBookmarked) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "सहेजें",
                        tint = if (isBookmarked) SaffronAmber else NeutralLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Text
            Text(
                text = "${question.questionNumber}. ${question.questionText}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Options
            val options = listOf(
                "A" to question.optionA,
                "B" to question.optionB,
                "C" to question.optionC,
                "D" to question.optionD
            )

            options.forEach { (key, optText) ->
                val isSelected = selectedOption == key
                val isCorrect = question.correctAnswer.equals(key, ignoreCase = true)
                val optBg = when {
                    showAnswer && isCorrect -> Color(0xFFD1FAE5)
                    showAnswer && isSelected && !isCorrect -> Color(0xFFFEE2E2)
                    isSelected -> Color(0xFFEFF6FF)
                    else -> Color(0xFFF8FAFC)
                }
                val optBorder = when {
                    showAnswer && isCorrect -> ForestGreen
                    showAnswer && isSelected && !isCorrect -> TerracottaRed
                    isSelected -> RoyalNavy
                    else -> Color(0xFFE2E8F0)
                }

                Surface(
                    color = optBg,
                    shape = RoundedCornerShape(8.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(optBorder)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable {
                            selectedOption = key
                            showAnswer = true
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isSelected || (showAnswer && isCorrect)) optBorder else Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = key,
                                color = if (isSelected || (showAnswer && isCorrect)) Color.White else NeutralDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = optText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Show answer button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { showAnswer = !showAnswer }
                ) {
                    Icon(
                        imageVector = if (showAnswer) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (showAnswer) "उत्तर छिपाएँ" else "उत्तर एवं व्याख्या देखें")
                }
            }

            if (showAnswer) {
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0FDF4), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "सही उत्तर: विकल्प (${question.correctAnswer.uppercase()})",
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            fontSize = 13.sp
                        )
                    }
                    if (question.explanation.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "व्याख्या: ${question.explanation}",
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
