package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.ExamViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamTestScreen(
    viewModel: ExamViewModel,
    onTestSubmitted: () -> Unit,
    onBack: () -> Unit
) {
    val currentTest by viewModel.currentTest.collectAsState()
    val questions by viewModel.currentQuestions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val userAnswers by viewModel.userAnswers.collectAsState()
    val reviewMarked by viewModel.reviewMarked.collectAsState()
    val visitedQuestions by viewModel.visitedQuestions.collectAsState()
    val timeRemaining by viewModel.timeRemainingSeconds.collectAsState()
    val isTestActive by viewModel.isTestActive.collectAsState()

    var showPaletteSheet by remember { mutableStateOf(false) }
    var showSubmitDialog by remember { mutableStateOf(false) }
    var showExitWarningDialog by remember { mutableStateOf(false) }

    // When test finishes automatically or submitted
    LaunchedEffect(isTestActive) {
        if (!isTestActive && viewModel.lastSubmittedResult.value != null) {
            onTestSubmitted()
        }
    }

    val currentQuestion = if (questions.isNotEmpty() && currentIndex in questions.indices) {
        questions[currentIndex]
    } else null

    // Format timer MM:SS
    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    val isTimeLow = timeRemaining <= 60

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentTest?.title ?: "ऑनलाइन टेस्ट",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = "प्रश्न ${currentIndex + 1} / ${questions.size}",
                            style = MaterialTheme.typography.bodySmall.copy(color = SaffronLight)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showExitWarningDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "परीक्षा छोड़ें",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Timer display chip
                    Surface(
                        color = if (isTimeLow) Color(0xFFFEE2E2) else Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("test_timer_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = if (isTimeLow) TerracottaRed else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = timeFormatted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isTimeLow) TerracottaRed else Color.White
                            )
                        }
                    }

                    // Question Palette Toggle button
                    IconButton(
                        onClick = { showPaletteSheet = true },
                        modifier = Modifier.testTag("question_palette_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = "प्रश्न सूची",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isTimeLow) Color(0xFF991B1B) else RoyalNavy
                )
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.previousQuestion() },
                        enabled = currentIndex > 0,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("prev_question_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("पिछला")
                    }

                    Button(
                        onClick = { showSubmitDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("submit_test_bar_button")
                    ) {
                        Text("सबमिट करें", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (currentIndex < questions.size - 1) {
                                viewModel.nextQuestion()
                            } else {
                                showSubmitDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("next_question_button")
                    ) {
                        Text(if (currentIndex < questions.size - 1) "अगला" else "अंतिम")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    ) { padding ->
        if (currentQuestion == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = RoyalNavy)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(BackgroundLight)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Badges & Action bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            color = Color(0xFFDBEAFE),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = currentQuestion.examName,
                                color = RoyalNavy,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(
                            color = SaffronLight,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "वर्ष ${currentQuestion.examYear}",
                                color = SaffronAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Mark for review chip button
                    val isMarked = reviewMarked.contains(currentQuestion.id)
                    FilterChip(
                        selected = isMarked,
                        onClick = { viewModel.toggleMarkForReview(currentQuestion.id) },
                        label = { Text(if (isMarked) "समीक्षा हेतु चिन्हित" else "Mark for Review", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isMarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PurpleLight,
                            selectedLabelColor = PurpleReview
                        ),
                        modifier = Modifier.testTag("mark_for_review_chip")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Question Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "प्र. ${currentIndex + 1}.  ${currentQuestion.questionText}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                lineHeight = 26.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "कृपया सही उत्तर विकल्प चुनें:",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = NeutralMedium,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Options A, B, C, D
                val selectedAns = userAnswers[currentQuestion.id]
                val options = listOf(
                    "A" to currentQuestion.optionA,
                    "B" to currentQuestion.optionB,
                    "C" to currentQuestion.optionC,
                    "D" to currentQuestion.optionD
                )

                options.forEach { (key, text) ->
                    val isSelected = selectedAns == key

                    Surface(
                        color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                        shape = RoundedCornerShape(10.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) RoyalNavy else Color(0xFFE2E8F0))
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable { viewModel.selectAnswer(currentQuestion.id, key) }
                            .testTag("option_${key}_button")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) RoyalNavy else Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = key,
                                    color = if (isSelected) Color.White else NeutralDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = RoyalNavy
                                )
                            }
                        }
                    }
                }

                // Clear Answer action
                if (selectedAns != null) {
                    TextButton(
                        onClick = { viewModel.clearAnswer(currentQuestion.id) },
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp)
                            .testTag("clear_answer_button")
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("उत्तर साफ़ करें (Clear Answer)", color = TerracottaRed, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    // Question Palette Bottom Sheet
    if (showPaletteSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaletteSheet = false },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .padding(bottom = 30.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "प्रश्न स्थिति ग्रिड (Question Palette)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = RoyalNavy
                    )
                    IconButton(onClick = { showPaletteSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "बंद करें")
                    }
                }

                // Legend
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LegendItem(color = ForestGreen, label = "उत्तर दिया")
                    LegendItem(color = TerracottaRed, label = "उत्तर नहीं दिया")
                    LegendItem(color = PurpleReview, label = "समीक्षा")
                    LegendItem(color = Color(0xFF94A3B8), label = "नहीं देखा")
                }

                Divider(color = DividerColor, modifier = Modifier.padding(vertical = 8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp)
                ) {
                    itemsIndexed(questions) { index, q ->
                        val isCurrent = index == currentIndex
                        val isAnswered = userAnswers.containsKey(q.id)
                        val isMarked = reviewMarked.contains(q.id)
                        val isVisited = visitedQuestions.contains(q.id)

                        val badgeColor = when {
                            isMarked -> PurpleReview
                            isAnswered -> ForestGreen
                            isVisited -> TerracottaRed
                            else -> Color(0xFFE2E8F0)
                        }

                        val textColor = when {
                            isMarked || isAnswered || isVisited -> Color.White
                            else -> NeutralDark
                        }

                        Surface(
                            color = badgeColor,
                            shape = RoundedCornerShape(8.dp),
                            border = if (isCurrent) CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(RoyalNavy),
                                width = 2.dp
                            ) else null,
                            modifier = Modifier
                                .size(44.dp)
                                .clickable {
                                    viewModel.goToQuestion(index)
                                    showPaletteSheet = false
                                }
                                .testTag("palette_item_$index")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${index + 1}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Submit Confirmation Dialog
    if (showSubmitDialog) {
        val total = questions.size
        val answered = userAnswers.size
        val unattempted = total - answered
        val marked = reviewMarked.size

        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = {
                Text("परीक्षा सबमिट करें?", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("क्या आप निश्चित रूप से टेस्ट सबमिट करना चाहते हैं?")
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("कुल प्रश्न: $total", fontWeight = FontWeight.SemiBold)
                            Text("उत्तर दिए: $answered", color = ForestGreen, fontWeight = FontWeight.SemiBold)
                            Text("अनुत्तरित: $unattempted", color = TerracottaRed, fontWeight = FontWeight.SemiBold)
                            Text("समीक्षा हेतु चिन्हित: $marked", color = PurpleReview, fontWeight = FontWeight.SemiBold)
                            Text("शेष समय: $timeFormatted", color = RoyalNavy, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        viewModel.submitTest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    modifier = Modifier.testTag("confirm_submit_button")
                ) {
                    Text("हाँ, सबमिट करें")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("जारी रखें")
                }
            }
        )
    }

    // Exit Warning Dialog
    if (showExitWarningDialog) {
        AlertDialog(
            onDismissRequest = { showExitWarningDialog = false },
            title = { Text("परीक्षा से बाहर निकलें?", fontWeight = FontWeight.Bold) },
            text = {
                Text("यदि आप बाहर निकलते हैं तो टेस्ट सबमिट हो जाएगा और अब तक का परिणाम सुरक्षित कर लिया जाएगा।")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitWarningDialog = false
                        viewModel.submitTest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaRed)
                ) {
                    Text("सबमिट कर बाहर निकलें")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitWarningDialog = false }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = label, fontSize = 11.sp, color = NeutralMedium)
    }
}
