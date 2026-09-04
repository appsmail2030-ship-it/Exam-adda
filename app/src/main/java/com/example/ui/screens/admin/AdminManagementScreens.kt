package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamCategoryEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.TopicEntity
import com.example.ui.components.AppTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.screens.QuestionCardWithAnswerToggle
import com.example.ui.theme.*
import com.example.ui.viewmodel.ExamViewModel

@Composable
fun AdminManageExamsScreen(
    viewModel: ExamViewModel,
    onBack: () -> Unit
) {
    val exams by viewModel.exams.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingExam by remember { mutableStateOf<ExamCategoryEntity?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "परीक्षा श्रेणियाँ प्रबंधन (Manage Exams)",
                subtitle = "Exam Categories CRUD",
                showBack = true,
                onBack = onBack,
                actions = {
                    IconButton(onClick = { showAddDialog = true }, modifier = Modifier.testTag("add_exam_button")) {
                        Icon(Icons.Default.Add, contentDescription = "जोड़ें", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = RoyalNavy,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_exam")
            ) {
                Icon(Icons.Default.Add, contentDescription = "नई परीक्षा जोड़ें")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "कुल परीक्षा श्रेणियाँ: ${exams.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeutralDark
                )
            }

            items(exams) { exam ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = exam.nameHindi,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = exam.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = { editingExam = exam }) {
                            Icon(Icons.Default.Edit, contentDescription = "संपादित करें", tint = RoyalNavy)
                        }

                        IconButton(onClick = { viewModel.deleteExamCategory(exam) }) {
                            Icon(Icons.Default.Delete, contentDescription = "हटाएं", tint = TerracottaRed)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var nameHindi by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("नई परीक्षा श्रेणी जोड़ें", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = nameHindi,
                        onValueChange = { nameHindi = it },
                        label = { Text("हिंदी नाम (उदा. राजस्थान कनिष्ठ लेखाकार)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("अंग्रेजी नाम (e.g. Rajasthan Junior Accountant)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameHindi.isNotBlank()) {
                            viewModel.addExamCategory(
                                name = if (name.isBlank()) nameHindi else name,
                                hindiName = nameHindi
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
                ) {
                    Text("जोड़ें")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("रद्द करें")
                }
            }
        )
    }

    if (editingExam != null) {
        val ex = editingExam!!
        var editName by remember { mutableStateOf(ex.name) }
        var editHindi by remember { mutableStateOf(ex.nameHindi) }

        AlertDialog(
            onDismissRequest = { editingExam = null },
            title = { Text("परीक्षा श्रेणी संपादित करें", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editHindi,
                        onValueChange = { editHindi = it },
                        label = { Text("हिंदी नाम") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("अंग्रेजी नाम") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateExamCategory(ex.copy(name = editName, nameHindi = editHindi))
                        editingExam = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
                ) {
                    Text("सहेजें")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingExam = null }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

@Composable
fun AdminManageSubjectsScreen(
    viewModel: ExamViewModel,
    onBack: () -> Unit
) {
    val subjects by viewModel.subjects.collectAsState()
    val allTopics by viewModel.allTopics.collectAsState()

    var showAddTopicDialog by remember { mutableStateOf(false) }
    var selectedSubjectForTopic by remember { mutableStateOf<SubjectEntity?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "विषय एवं टॉपिक प्रबंधन",
                subtitle = "19 विषय व संबंधित टॉपिक",
                showBack = true,
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "विषय सूची (${subjects.size} विषय)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = NeutralDark
                )
            }

            items(subjects) { subject ->
                val topics = allTopics.filter { it.subjectId == subject.id }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = subject.nameHindi,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${subject.code} • ${topics.size} टॉपिक्स",
                                    fontSize = 12.sp,
                                    color = NeutralMedium
                                )
                            }

                            TextButton(
                                onClick = {
                                    selectedSubjectForTopic = subject
                                    showAddTopicDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("टॉपिक जोड़ें", fontSize = 12.sp)
                            }
                        }

                        if (topics.isNotEmpty()) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)
                            topics.take(4).forEach { t ->
                                Text(
                                    text = "• ${t.nameHindi}",
                                    fontSize = 13.sp,
                                    color = NeutralDark,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                            if (topics.size > 4) {
                                Text(
                                    text = "... और ${topics.size - 4} टॉपिक",
                                    fontSize = 11.sp,
                                    color = NeutralMedium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddTopicDialog && selectedSubjectForTopic != null) {
        val sub = selectedSubjectForTopic!!
        var topicHindi by remember { mutableStateOf("") }
        var topicEng by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddTopicDialog = false },
            title = { Text("${sub.nameHindi} में नया टॉपिक जोड़ें", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = topicHindi,
                        onValueChange = { topicHindi = it },
                        label = { Text("टॉपिक नाम (हिंदी)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = topicEng,
                        onValueChange = { topicEng = it },
                        label = { Text("Topic Name (English)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (topicHindi.isNotBlank()) {
                            viewModel.addTopic(sub.id, topicHindi, topicEng)
                            showAddTopicDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
                ) {
                    Text("जोड़ें")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTopicDialog = false }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

@Composable
fun AdminAutoTestScreen(
    viewModel: ExamViewModel,
    onBack: () -> Unit
) {
    val subjects by viewModel.subjects.collectAsState()
    val allTopics by viewModel.allTopics.collectAsState()
    val autoTestResult by viewModel.autoTestGenerationResult.collectAsState()

    var selectedSubject by remember { mutableStateOf<SubjectEntity?>(null) }
    var selectedTopic by remember { mutableStateOf<TopicEntity?>(null) }

    val topicsForSub = remember(selectedSubject, allTopics) {
        if (selectedSubject == null) emptyList()
        else allTopics.filter { it.subjectId == selectedSubject!!.id }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "ऑटोमैटिक टेस्ट जनरेटर",
                subtitle = "टॉपिक चुनें व 5 टेस्ट एक क्लिक में बनाएं",
                showBack = true,
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "टॉपिक-वाइज़ टेस्ट जनरेशन विज़ार्ड",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = RoyalNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "सिस्टम प्रश्न बैंक से बिना डुप्लीकेसी के 25-25 प्रश्नों के 5 टेस्ट स्वतः तैयार करेगा।",
                        fontSize = 12.sp,
                        color = NeutralMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("1. विषय चुनें (Select Subject):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyColumn(modifier = Modifier.heightIn(max = 140.dp)) {
                        items(subjects) { sub ->
                            Surface(
                                color = if (selectedSubject?.id == sub.id) Color(0xFFDBEAFE) else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable {
                                        selectedSubject = sub
                                        selectedTopic = null
                                    }
                            ) {
                                Text(
                                    text = sub.nameHindi,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedSubject?.id == sub.id) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedSubject?.id == sub.id) RoyalNavy else NeutralDark,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (selectedSubject != null) {
                        Text("2. टॉपिक चुनें (Select Topic):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))

                        if (topicsForSub.isEmpty()) {
                            Text("इस विषय में कोई टॉपिक उपलब्ध नहीं है।", fontSize = 12.sp, color = NeutralMedium)
                        } else {
                            LazyColumn(modifier = Modifier.heightIn(max = 140.dp)) {
                                items(topicsForSub) { top ->
                                    Surface(
                                        color = if (selectedTopic?.id == top.id) Color(0xFFFEF3C7) else Color(0xFFF8FAFC),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                            .clickable { selectedTopic = top }
                                    ) {
                                        Text(
                                            text = top.nameHindi,
                                            fontSize = 13.sp,
                                            fontWeight = if (selectedTopic?.id == top.id) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedTopic?.id == top.id) SaffronAmber else NeutralDark,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (selectedSubject != null && selectedTopic != null) {
                                viewModel.autoGenerateTopicTests(
                                    subjectId = selectedSubject!!.id,
                                    topicId = selectedTopic!!.id,
                                    topicTitle = selectedTopic!!.nameHindi,
                                    numTests = 5
                                )
                            }
                        },
                        enabled = selectedTopic != null,
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronAmber),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_auto_test_generator")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("5 टेस्ट जनरेट करें (25 प्रश्न प्रत्येक)", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (autoTestResult != null) {
                Surface(
                    color = GreenLight,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = autoTestResult ?: "",
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminQuestionBankScreen(
    viewModel: ExamViewModel,
    onBack: () -> Unit
) {
    val questions by viewModel.allQuestions.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "प्रश्न बैंक प्रबंधन (Question Bank)",
                subtitle = "कुल ${questions.size} प्रश्न उपलब्ध",
                showBack = true,
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(questions) { question ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusBadge(
                                text = "ID #${question.id} • ${question.examName}",
                                containerColor = Color(0xFFDBEAFE),
                                contentColor = RoyalNavy
                            )

                            IconButton(
                                onClick = { viewModel.deleteQuestion(question) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "हटाएं", tint = TerracottaRed)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = question.questionText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeutralDark
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text("A. ${question.optionA}", fontSize = 12.sp)
                        Text("B. ${question.optionB}", fontSize = 12.sp)
                        Text("C. ${question.optionC}", fontSize = 12.sp)
                        Text("D. ${question.optionD}", fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "सही उत्तर: विकल्प (${question.correctAnswer})",
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
