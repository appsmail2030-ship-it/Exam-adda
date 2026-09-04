package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.ui.components.AppTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.ExamViewModel
import java.util.Locale

@Composable
fun SearchScreen(
    viewModel: ExamViewModel,
    onBack: () -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "प्रश्न एवं टॉपिक खोजें",
                subtitle = "पूरे प्रश्न बैंक में तुरंत खोजें",
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
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.searchQuery.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("global_search_input"),
                placeholder = { Text("प्रश्न, लोकदेवता, दुर्ग, नदी, एकीकरण, RPSC खोजें...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "साफ़ करें")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RoyalNavy,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            if (query.isBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ManageSearch,
                            contentDescription = null,
                            tint = NeutralLight,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "खोजने के लिए शब्द टाइप करें",
                            fontWeight = FontWeight.SemiBold,
                            color = NeutralMedium
                        )
                        Text(
                            text = "जैसे: 'पाबूजी', 'चित्तौड़गढ़', 'पंचायती राज', '1857'",
                            fontSize = 13.sp,
                            color = NeutralLight
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "मिले परिणाम: ${searchResults.size}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeutralDark
                        )
                    }

                    if (searchResults.isEmpty()) {
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
                                        text = "'$query' से संबंधित कोई प्रश्न नहीं मिला।",
                                        fontWeight = FontWeight.SemiBold,
                                        color = NeutralDark
                                    )
                                }
                            }
                        }
                    } else {
                        items(searchResults) { q ->
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
fun AnalyticsScreen(
    viewModel: ExamViewModel,
    onBack: () -> Unit
) {
    val attempts by viewModel.testAttempts.collectAsState()
    val wrongQuestions by viewModel.wrongQuestions.collectAsState()

    val totalAttempts = attempts.size
    val totalSolved = attempts.sumOf { it.attempted }
    val totalCorrect = attempts.sumOf { it.correct }
    val overallAccuracy = if (totalSolved > 0) (totalCorrect.toFloat() / totalSolved * 100f).toInt() else 0

    Scaffold(
        topBar = {
            AppTopBar(
                title = "प्रगति एवं विश्लेषण",
                subtitle = "मेरी तैयारी का सम्पूर्ण रिपोर्ट कार्ड",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stats Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "समग्र प्रदर्शन सारांश (Overall Performance)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = RoyalNavy
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$totalAttempts", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = RoyalNavy)
                                Text("कुल टेस्ट", fontSize = 12.sp, color = NeutralMedium)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$totalSolved", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = RoyalBlueLight)
                                Text("हल किए प्रश्न", fontSize = 12.sp, color = NeutralMedium)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$totalCorrect", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = ForestGreen)
                                Text("सही उत्तर", fontSize = 12.sp, color = NeutralMedium)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$overallAccuracy%", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = SaffronAmber)
                                Text("सटीकता", fontSize = 12.sp, color = NeutralMedium)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = DividerColor)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (overallAccuracy >= 75) "सुझाव: आपकी तैयारी उत्कृष्ट स्तर पर है। विगत वर्ष प्रश्नों का अभ्यास जारी रखें।"
                            else if (overallAccuracy >= 50) "सुझाव: अच्छा प्रदर्शन! गलत हुए प्रश्नों का रिवीज़न करें ताकि सटीकता 80%+ पहुँच सके।"
                            else "सुझाव: बुनियादी विषयों का अध्ययन करें एवं दैनिक 25 प्रश्नों का टेस्ट नियम से दें।",
                            fontSize = 12.sp,
                            color = NeutralDark,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Recent attempts history
            item {
                Text(
                    text = "हाल ही में दिए गए टेस्ट (Recent Test Attempts)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NeutralDark
                )
            }

            if (attempts.isEmpty()) {
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
                            Text("अभी कोई टेस्ट नहीं दिया गया है। होम स्क्रीन से पहला टेस्ट दें!", color = NeutralMedium)
                        }
                    }
                }
            } else {
                items(attempts) { att ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (att.accuracy >= 60f) GreenLight else Color(0xFFFEF3C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${att.accuracy.toInt()}%",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (att.accuracy >= 60f) ForestGreen else SaffronAmber
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = att.testTitle,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${att.date} • स्कोर: ${att.score}/${att.totalQuestions}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
