package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.ui.components.AppTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.ExamViewModel

@Composable
fun PreviousYearScreen(
    viewModel: ExamViewModel,
    onBack: () -> Unit
) {
    val pyqQuestions by viewModel.pyqList.collectAsState()
    val exams by viewModel.exams.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val selectedExamId by viewModel.pyqSelectedExamId.collectAsState()
    val selectedYear by viewModel.pyqSelectedYear.collectAsState()
    val selectedSubjectId by viewModel.pyqSelectedSubjectId.collectAsState()

    val years = listOf(2024, 2023, 2022, 2021)

    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "विगत वर्ष प्रश्न (PYQ)",
                subtitle = "राजस्थान भर्ती परीक्षाओं के प्रामाणिक प्रश्न",
                showBack = true,
                onBack = onBack,
                actions = {
                    IconButton(
                        onClick = {
                            // Reset filters
                            viewModel.pyqSelectedExamId.value = null
                            viewModel.pyqSelectedYear.value = null
                            viewModel.pyqSelectedSubjectId.value = null
                        },
                        modifier = Modifier.testTag("reset_filters_button")
                    ) {
                        Icon(Icons.Default.FilterAltOff, contentDescription = "फ़िल्टर हटाएं", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
        ) {
            // Horizontal Year Chips
            Surface(color = Color.White, shadowElevation = 1.dp) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "परीक्षा वर्ष चुनें (Exam Year):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedYear == null,
                                onClick = { viewModel.pyqSelectedYear.value = null },
                                label = { Text("सभी वर्ष") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RoyalNavy,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        items(years) { yr ->
                            FilterChip(
                                selected = selectedYear == yr,
                                onClick = {
                                    viewModel.pyqSelectedYear.value = if (selectedYear == yr) null else yr
                                },
                                label = { Text("$yr") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RoyalNavy,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Exam categories row
                    Text(
                        text = "परीक्षा श्रेणी चुनें (Exam Category):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedExamId == null,
                                onClick = { viewModel.pyqSelectedExamId.value = null },
                                label = { Text("सभी परीक्षाएँ") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SaffronAmber,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        items(exams) { ex ->
                            FilterChip(
                                selected = selectedExamId == ex.id,
                                onClick = {
                                    viewModel.pyqSelectedExamId.value = if (selectedExamId == ex.id) null else ex.id
                                },
                                label = { Text(ex.nameHindi) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SaffronAmber,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Results count banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "उपलब्ध विगत वर्ष प्रश्न (${pyqQuestions.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeutralDark
                )

                if (selectedExamId != null || selectedYear != null) {
                    TextButton(onClick = {
                        viewModel.pyqSelectedExamId.value = null
                        viewModel.pyqSelectedYear.value = null
                        viewModel.pyqSelectedSubjectId.value = null
                    }) {
                        Text("फ़िल्टर साफ़ करें", fontSize = 12.sp, color = TerracottaRed)
                    }
                }
            }

            // PYQ Questions list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (pyqQuestions.isEmpty()) {
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
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    tint = NeutralLight,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "चयनित फ़िल्टर के लिए कोई प्रश्न नहीं मिला।",
                                    fontWeight = FontWeight.SemiBold,
                                    color = NeutralDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "कृपया वर्ष अथवा परीक्षा फ़िल्टर बदलें।",
                                    fontSize = 13.sp,
                                    color = NeutralMedium
                                )
                            }
                        }
                    }
                } else {
                    items(pyqQuestions) { question ->
                        QuestionCardWithAnswerToggle(
                            question = question,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}
