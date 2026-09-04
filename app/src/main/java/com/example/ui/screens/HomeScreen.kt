package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamCategoryEntity
import com.example.data.model.SubjectEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.ExamViewModel

@Composable
fun HomeScreen(
    viewModel: ExamViewModel,
    onNavigateToSubjects: () -> Unit,
    onNavigateToSubjectDetail: (Long) -> Unit,
    onNavigateToPyq: () -> Unit,
    onNavigateToDailyTest: () -> Unit,
    onNavigateToMockTests: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToWrongQuestions: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val exams by viewModel.exams.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val selectedExam by viewModel.selectedExam.collectAsState()
    val testAttempts by viewModel.testAttempts.collectAsState()
    val bookmarkedCount by viewModel.bookmarkedQuestions.collectAsState()
    val wrongQuestionsCount by viewModel.wrongQuestions.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    var showNotificationsDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("home_screen_container"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Royal Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(RoyalNavy, RoyalNavyDark)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(SaffronAmber)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "राजस्थान प्रतियोगी परीक्षा पोर्टल",
                                    color = SaffronLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rajasthan Exam Master",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "शिक्षक भर्ती, REET, CET, पुलिस एवं पटवारी तैयारी",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp
                            )
                        }

                        // Notification Icon with badge
                        Box(contentAlignment = Alignment.TopEnd) {
                            IconButton(
                                onClick = { showNotificationsDialog = true },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .testTag("notification_bell_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "सूचनाएं",
                                    tint = Color.White
                                )
                            }
                            if (notifications.any { !it.isRead }) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(TerracottaRed)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search Trigger Bar
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToSearch() }
                            .testTag("search_trigger_bar")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = NeutralMedium
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "प्रश्न, टॉपिक, विगत वर्ष या परीक्षा खोजें...",
                                color = NeutralLight,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. Exam Categories Selector (Horizontal Scroll)
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = "लक्षित परीक्षा चुनें (Target Exam)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedExam == null,
                            onClick = { viewModel.selectedExam.value = null },
                            label = { Text("सभी परीक्षाएँ (All Exams)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoyalNavy,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    items(exams) { exam ->
                        FilterChip(
                            selected = selectedExam?.id == exam.id,
                            onClick = { viewModel.selectedExam.value = exam },
                            label = { Text(exam.nameHindi) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoyalNavy,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // 3. Quick Action Hub (6 Core Study Features)
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "तैयारी मॉड्यूल (Study Modules)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickActionCard(
                        title = "दैनिक टेस्ट",
                        subtitle = "25 प्रश्न • 10 मिनट",
                        icon = Icons.Default.Timer,
                        bgColor = Color(0xFFEFF6FF),
                        accentColor = RoyalBlueLight,
                        modifier = Modifier.weight(1f),
                        tag = "daily_test_button",
                        onClick = onNavigateToDailyTest
                    )
                    QuickActionCard(
                        title = "विषयवार तैयारी",
                        subtitle = "19 मुख्य विषय",
                        icon = Icons.Default.MenuBook,
                        bgColor = Color(0xFFFEF3C7),
                        accentColor = SaffronAmber,
                        modifier = Modifier.weight(1f),
                        tag = "subjects_hub_button",
                        onClick = onNavigateToSubjects
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickActionCard(
                        title = "विगत वर्ष प्रश्न (PYQ)",
                        subtitle = "2021-2024 प्रश्न",
                        icon = Icons.Default.HistoryEdu,
                        bgColor = Color(0xFFECFDF5),
                        accentColor = ForestGreen,
                        modifier = Modifier.weight(1f),
                        tag = "pyq_hub_button",
                        onClick = onNavigateToPyq
                    )
                    QuickActionCard(
                        title = "फुल मॉक टेस्ट",
                        subtitle = "परीक्षा स्तर के पेपर",
                        icon = Icons.Default.AssignmentTurnedIn,
                        bgColor = Color(0xFFF3E8FF),
                        accentColor = PurpleReview,
                        modifier = Modifier.weight(1f),
                        tag = "mock_test_button",
                        onClick = onNavigateToMockTests
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickActionCard(
                        title = "सहेजे गए प्रश्न",
                        subtitle = "${bookmarkedCount.size} प्रश्न सहेजे",
                        icon = Icons.Default.Bookmark,
                        bgColor = Color(0xFFF1F5F9),
                        accentColor = NeutralMedium,
                        modifier = Modifier.weight(1f),
                        tag = "bookmarks_button",
                        onClick = onNavigateToBookmarks
                    )
                    QuickActionCard(
                        title = "गलत प्रश्न अभ्यास",
                        subtitle = "${wrongQuestionsCount.size} प्रश्न सुधारें",
                        icon = Icons.Default.ErrorOutline,
                        bgColor = Color(0xFFFEE2E2),
                        accentColor = TerracottaRed,
                        modifier = Modifier.weight(1f),
                        tag = "wrong_questions_button",
                        onClick = onNavigateToWrongQuestions
                    )
                }
            }
        }

        // 4. Featured Practice / Daily Test Highlight Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = SaffronAmber,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "आज का दैनिक मूल्यांकन टेस्ट",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "25 प्रश्न • समय 10:00 मिनट • नेगेटिव मार्किंग",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = onNavigateToDailyTest,
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronAmber),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("start_daily_test_button")
                    ) {
                        Text("शुरू करें", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 5. Main Subjects Section (A to S)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "प्रमुख विषय सूची (Main Subjects)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = onNavigateToSubjects) {
                        Text("सभी 19 विषय देखें", color = RoyalNavy, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        // Display first 6 subjects with progress and action
        items(subjects.take(6)) { subject ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { onNavigateToSubjectDetail(subject.id) }
                    .testTag("subject_card_${subject.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFDBEAFE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = RoyalNavy,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = subject.nameHindi,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = subject.nameEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = NeutralMedium
                    )
                }
            }
        }

        // 6. User Stats & Performance Shortcut
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .clickable { onNavigateToAnalytics() }
                    .testTag("analytics_shortcut_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "मेरी प्रगति रिपोर्ट (My Progress)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "विस्तृत देखें >",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = RoyalNavy
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${testAttempts.size}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = RoyalNavy
                            )
                            Text(
                                text = "टेस्ट दिए",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeutralMedium
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val totalCorrect = testAttempts.sumOf { it.correct }
                            Text(
                                text = "$totalCorrect",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = ForestGreen
                            )
                            Text(
                                text = "सही उत्तर",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeutralMedium
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val avgAcc = if (testAttempts.isNotEmpty()) {
                                testAttempts.map { it.accuracy }.average().toInt()
                            } else 0
                            Text(
                                text = "$avgAcc%",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = SaffronAmber
                            )
                            Text(
                                text = "औसत सटीकता",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeutralMedium
                            )
                        }
                    }
                }
            }
        }

        // 7. Admin Portal Access Section
        item {
            Surface(
                color = RoyalNavyDark,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onNavigateToAdmin() }
                    .testTag("admin_portal_access_button")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SaffronAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "व्यवस्थापक पोर्टल (Admin Portal)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "PDF से प्रश्न निकालें, परीक्षाएँ व टेस्ट प्रबंधित करें",
                            color = SaffronLight,
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = RoyalNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("नवीनतम सूचनाएं (Notifications)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    if (notifications.isEmpty()) {
                        item {
                            Text("कोई नई सूचना उपलब्ध नहीं है।", color = NeutralMedium)
                        }
                    } else {
                        items(notifications) { notif ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = notif.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = RoyalNavy
                                )
                                Text(
                                    text = notif.message,
                                    fontSize = 13.sp,
                                    color = NeutralDark
                                )
                                Divider(modifier = Modifier.padding(top = 8.dp), color = DividerColor)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("बंद करें", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    bgColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
