package com.example.ui.screens.admin

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamCategoryEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.TopicEntity
import com.example.ui.components.AppTopBar
import com.example.ui.components.StatCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.ExamViewModel

@Composable
fun AdminLoginScreen(
    viewModel: ExamViewModel,
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "व्यवस्थापक लॉगिन (Admin Portal)",
                subtitle = "सुरक्षित प्रशासनिक नियंत्रण",
                showBack = true,
                onBack = onBack
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDBEAFE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = RoyalNavy,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "व्यवस्थापक सत्यापन",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = RoyalNavyDark
                    )
                    Text(
                        text = "प्रशासनिक पिन दर्ज करें (Default PIN: 1234)",
                        fontSize = 12.sp,
                        color = NeutralMedium
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = pin,
                        onValueChange = {
                            pin = it
                            errorMessage = ""
                        },
                        label = { Text("एडमिन पिन (Admin PIN)") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_pin_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMessage, color = TerracottaRed, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (viewModel.loginAdmin(pin)) {
                                onLoginSuccess()
                            } else {
                                errorMessage = "गलत पिन! कृपया पुनः प्रयास करें। (डिफ़ॉल्ट पिन: 1234)"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_login_button")
                    ) {
                        Text("प्रवेश करें (Login)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminDashboardScreen(
    viewModel: ExamViewModel,
    onNavigateToPdfImport: () -> Unit,
    onNavigateToQuestionBank: () -> Unit,
    onNavigateToManageExams: () -> Unit,
    onNavigateToManageSubjects: () -> Unit,
    onNavigateToAutoTest: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val totalQuestions by viewModel.allQuestions.collectAsState()
    val approvedQuestions by viewModel.approvedQuestionsCount.collectAsState()
    val pendingQuestions by viewModel.pendingQuestionsCount.collectAsState()
    val pyqCount by viewModel.pyqQuestionsCount.collectAsState()
    val totalTests by viewModel.allTests.collectAsState()
    val pdfUploads by viewModel.allPdfUploads.collectAsState()

    var showNotificationDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "व्यवस्थापक डैशबोर्ड (Admin Portal)",
                subtitle = "राजस्थान एग्जाम मास्टर कंट्रोल रूम",
                showBack = true,
                onBack = onBack,
                actions = {
                    IconButton(onClick = onLogout, modifier = Modifier.testTag("admin_logout_button")) {
                        Icon(Icons.Default.Logout, contentDescription = "लॉगआउट", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Metrics Section
            item {
                Text(
                    text = "सिस्टम आंकड़े (System Overview)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NeutralDark
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "कुल प्रश्न",
                        value = "${totalQuestions.size}",
                        icon = Icons.Default.Quiz,
                        iconBgColor = Color(0xFFDBEAFE),
                        iconColor = RoyalNavy,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "स्वीकृत प्रश्न",
                        value = "$approvedQuestions",
                        icon = Icons.Default.CheckCircle,
                        iconBgColor = GreenLight,
                        iconColor = ForestGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "विगत वर्ष प्रश्न (PYQ)",
                        value = "$pyqCount",
                        icon = Icons.Default.HistoryEdu,
                        iconBgColor = Color(0xFFFEF3C7),
                        iconColor = SaffronAmber,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "कुल टेस्ट",
                        value = "${totalTests.size}",
                        icon = Icons.Default.Assignment,
                        iconBgColor = Color(0xFFF3E8FF),
                        iconColor = PurpleReview,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Management Features Menu
            item {
                Text(
                    text = "प्रशासनिक मॉड्यूल (Admin Modules)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NeutralDark,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Module 1: PDF Upload & AI Extractor
            item {
                AdminModuleCard(
                    title = "PDF अपलोड एवं AI प्रश्न निष्कर्षण",
                    subtitle = "दस्तावेज़ से प्रश्न, विकल्प, उत्तर निकालें व डुप्लीकेट पहचानें",
                    icon = Icons.Default.PictureAsPdf,
                    badge = "AI Powered",
                    accentColor = TerracottaRed,
                    tag = "admin_pdf_import_menu",
                    onClick = onNavigateToPdfImport
                )
            }

            // Module 2: Auto Test Generator
            item {
                AdminModuleCard(
                    title = "ऑटोमैटिक टेस्ट जनरेटर",
                    subtitle = "किसी भी टॉपिक के लिए 25 प्रश्नों के 5 टेस्ट एक क्लिक में बनाएं",
                    icon = Icons.Default.AutoAwesome,
                    badge = "5 Tests / 25 Qs",
                    accentColor = SaffronAmber,
                    tag = "admin_auto_test_menu",
                    onClick = onNavigateToAutoTest
                )
            }

            // Module 3: Exam Categories
            item {
                AdminModuleCard(
                    title = "परीक्षा श्रेणियाँ प्रबंधन (Manage Exams)",
                    subtitle = "1st Grade, 2nd Grade, 3rd Grade, REET, CET जोड़ें/संशोधित करें",
                    icon = Icons.Default.School,
                    badge = "10 श्रेणियाँ",
                    accentColor = RoyalNavy,
                    tag = "admin_manage_exams_menu",
                    onClick = onNavigateToManageExams
                )
            }

            // Module 4: Subjects & Topics
            item {
                AdminModuleCard(
                    title = "विषय एवं टॉपिक प्रबंधन",
                    subtitle = "राजस्थान पाठ्यक्रम के 19 विषय व उप-विषय संपादित करें",
                    icon = Icons.Default.MenuBook,
                    badge = "19 विषय",
                    accentColor = ForestGreen,
                    tag = "admin_manage_subjects_menu",
                    onClick = onNavigateToManageSubjects
                )
            }

            // Module 5: Question Bank
            item {
                AdminModuleCard(
                    title = "प्रश्न बैंक प्रबंधन (Question Bank)",
                    subtitle = "सभी प्रश्नों को देखें, संपादित करें, हटाएं अथवा स्वीकृति दें",
                    icon = Icons.Default.Storage,
                    badge = "${totalQuestions.size} प्रश्न",
                    accentColor = PurpleReview,
                    tag = "admin_question_bank_menu",
                    onClick = onNavigateToQuestionBank
                )
            }

            // Module 6: Send Notifications
            item {
                AdminModuleCard(
                    title = "अभ्यर्थियों को सूचना भेजें (Notifications)",
                    subtitle = "नए टेस्ट अथवा परीक्षा तिथि की सूचना तुरंत पुश करें",
                    icon = Icons.Default.Campaign,
                    badge = "सूचनाएं",
                    accentColor = Color(0xFF0284C7),
                    tag = "admin_send_notif_menu",
                    onClick = { showNotificationDialog = true }
                )
            }
        }
    }

    if (showNotificationDialog) {
        var notifTitle by remember { mutableStateOf("") }
        var notifMsg by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = { Text("अभ्यर्थियों को नई सूचना भेजें", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = notifTitle,
                        onValueChange = { notifTitle = it },
                        label = { Text("शीर्षक (Title)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = notifMsg,
                        onValueChange = { notifMsg = it },
                        label = { Text("संदेश (Message)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (notifTitle.isNotBlank() && notifMsg.isNotBlank()) {
                            viewModel.sendNotification(notifTitle, notifMsg)
                            showNotificationDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
                ) {
                    Text("भेजें (Send)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotificationDialog = false }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

@Composable
fun AdminModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    accentColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        color = accentColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = NeutralMedium)
        }
    }
}
