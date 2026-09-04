package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.ExamViewModel

@Composable
fun BookmarksScreen(
    viewModel: ExamViewModel,
    onBack: () -> Unit
) {
    val bookmarks by viewModel.bookmarkedQuestions.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "सहेजे गए प्रश्न (Bookmarks)",
                subtitle = "त्वरित रिवीज़न हेतु सहेजे गए महत्वपूर्ण प्रश्न",
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
                    text = "कुल सहेजे गए प्रश्न: ${bookmarks.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeutralDark
                )
            }

            if (bookmarks.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = NeutralLight,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "अभी कोई प्रश्न सहेजा नहीं गया है।",
                                fontWeight = FontWeight.SemiBold,
                                color = NeutralDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "प्रश्न के ऊपर बुकमार्क आइकन दबाकर प्रश्न सहेजें।",
                                fontSize = 13.sp,
                                color = NeutralMedium
                            )
                        }
                    }
                }
            } else {
                items(bookmarks) { question ->
                    QuestionCardWithAnswerToggle(
                        question = question,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun WrongQuestionsScreen(
    viewModel: ExamViewModel,
    onBack: () -> Unit
) {
    val wrongQuestions by viewModel.wrongQuestions.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "मेरे गलत प्रश्न अभ्यास",
                subtitle = "टेस्ट में गलत हुए प्रश्नों का पुनः रिवीज़न",
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
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = TerracottaRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "कमजोर क्षेत्रों पर विशेष ध्यान दें",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TerracottaRed
                            )
                            Text(
                                text = "यहाँ वे सभी प्रश्न संग्रहित हैं जो आपने टेस्ट में गलत किए थे। सही उत्तर सीखकर तैयारी पुख्ता करें।",
                                fontSize = 12.sp,
                                color = NeutralDark
                            )
                        }
                    }
                }
            }

            if (wrongQuestions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "बधाई! अभी कोई गलत प्रश्न सूची में नहीं है।",
                                fontWeight = FontWeight.SemiBold,
                                color = NeutralDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "टेस्ट देने पर गलत उत्तर स्वतः यहाँ जुड़ जाएंगे।",
                                fontSize = 13.sp,
                                color = NeutralMedium
                            )
                        }
                    }
                }
            } else {
                items(wrongQuestions) { question ->
                    QuestionCardWithAnswerToggle(
                        question = question,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
