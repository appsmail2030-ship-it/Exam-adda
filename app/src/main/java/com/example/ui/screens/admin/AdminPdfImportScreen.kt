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
import com.example.ai.ExtractedQuestionDraft
import com.example.ui.components.AppTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.ExamViewModel

@Composable
fun AdminPdfImportScreen(
    viewModel: ExamViewModel,
    onBack: () -> Unit
) {
    val drafts by viewModel.extractedDrafts.collectAsState()
    val isExtracting by viewModel.isExtracting.collectAsState()
    val message by viewModel.extractionMessage.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val topics by viewModel.allTopics.collectAsState()
    val exams by viewModel.exams.collectAsState()

    var rawInputText by remember { mutableStateOf("") }
    var selectedExamName by remember { mutableStateOf("Rajasthan 3rd Grade Teacher") }
    var selectedYear by remember { mutableIntStateOf(2024) }
    var editingDraft by remember { mutableStateOf<ExtractedQuestionDraft?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "PDF अपलोड एवं AI प्रश्न निष्कर्षण",
                subtitle = "दस्तावेज़ से प्रश्न निकालें, समीक्षा करें व जोड़ें",
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
            // Input Box & Setup Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "1. दस्तावेज़ पाठ (PDF / Document Text)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = RoyalNavy
                        )
                        Text(
                            text = "PDF से कॉपी किया गया पाठ यहाँ चिपकाएँ अथवा सैंपल पेपर लोड करें:",
                            fontSize = 12.sp,
                            color = NeutralMedium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = rawInputText,
                            onValueChange = { rawInputText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .testTag("raw_pdf_text_input"),
                            placeholder = {
                                Text(
                                    "Q1. राजस्थान का राज्य पशु कौन सा है?\nA. चिंकारा\nB. बाघ\nC. हाथी\nD. गाय\nउत्तर: A\nव्याख्या: चिंकारा को 1981 में वन्यजीव श्रेणी में राज्य पशु घोषित किया गया।"
                                )
                            },
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Sample text button for quick testing
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    rawInputText = """
Q1. राजस्थान का राज्य खेल कौन सा है जिसे 1948 में राज्य खेल घोषित किया गया था?
A. बास्केटबॉल
B. कबड्डी
C. हॉकी
D. क्रिकेट
उत्तर: A
व्याख्या: 1948 में बास्केटबॉल को राजस्थान का राज्य खेल घोषित किया गया था।

Q2. मकराना में मिलने वाले विश्व प्रसिद्ध सफेद संगमरमर की किस्म कौन सी है?
A. कैल्साइट
B. डोलोमाइट
C. सिलिसियस
D. गार्नेट
उत्तर: A
व्याख्या: नागौर के मकराना में उच्च श्रेणी का कैल्साइट मार्बल पाया जाता है जिससे ताजमहल का निर्माण हुआ था।

Q3. राजस्थान की प्रथम महिला राज्यपाल कौन थीं?
A. श्रीमती प्रतिभा पाटिल
B. श्रीमती प्रभा राव
C. श्रीमती मार्गरेट अल्वा
D. श्रीमती कमला बेनीवाल
उत्तर: A
व्याख्या: श्रीमती प्रतिभा देवीसिंह पाटिल 2004 से 2007 तक राजस्थान की प्रथम महिला राज्यपाल रहीं, जो बाद में देश की प्रथम महिला राष्ट्रपति बनीं।
                                    """.trimIndent()
                                },
                                modifier = Modifier.testTag("load_sample_paper_button")
                            ) {
                                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("सैंपल राजस्थान प्रश्न लोड करें", fontSize = 12.sp)
                            }

                            if (rawInputText.isNotEmpty()) {
                                TextButton(onClick = { rawInputText = "" }) {
                                    Text("साफ़ करें", color = TerracottaRed, fontSize = 12.sp)
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)

                        // Exam & Year metadata selectors
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = selectedExamName,
                                onValueChange = { selectedExamName = it },
                                label = { Text("परीक्षा नाम") },
                                modifier = Modifier.weight(2f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = "$selectedYear",
                                onValueChange = { selectedYear = it.toIntOrNull() ?: 2024 },
                                label = { Text("वर्ष") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (rawInputText.isNotBlank()) {
                                    viewModel.processPdfText(
                                        rawText = rawInputText,
                                        fileName = "Question_Upload_${System.currentTimeMillis() % 1000}.pdf",
                                        examName = selectedExamName,
                                        examYear = selectedYear
                                    )
                                }
                            },
                            enabled = rawInputText.isNotBlank() && !isExtracting,
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("extract_questions_button")
                        ) {
                            if (isExtracting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AI प्रश्न निष्कर्षण जारी...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("दस्तावेज़ विश्लेषण व AI वर्गीकरण करें")
                            }
                        }
                    }
                }
            }

            if (message.isNotEmpty()) {
                item {
                    Surface(
                        color = if (drafts.isNotEmpty()) GreenLight else Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = message,
                            color = if (drafts.isNotEmpty()) ForestGreen else RoyalNavy,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Preview & Approval Section
            if (drafts.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. निकाले गए प्रश्नों की समीक्षा (${drafts.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NeutralDark
                        )

                        val nonDuplicates = drafts.count { !it.isDuplicate }
                        Button(
                            onClick = { viewModel.approveAllNonDuplicateDrafts() },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("approve_all_button")
                        ) {
                            Text("सभी स्वीकृत करें ($nonDuplicates)")
                        }
                    }
                }

                items(drafts) { draft ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Header tags
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    StatusBadge(
                                        text = draft.subjectName.take(15) + "...",
                                        containerColor = Color(0xFFDBEAFE),
                                        contentColor = RoyalNavy
                                    )
                                    StatusBadge(
                                        text = draft.topicName.take(15) + "...",
                                        containerColor = SaffronLight,
                                        contentColor = SaffronAmber
                                    )
                                }

                                if (draft.isDuplicate) {
                                    StatusBadge(
                                        text = "संभावित डुप्लीकेट",
                                        containerColor = TerracottaLight,
                                        contentColor = TerracottaRed
                                    )
                                } else {
                                    StatusBadge(
                                        text = "नया प्रश्न",
                                        containerColor = GreenLight,
                                        contentColor = ForestGreen
                                    )
                                }
                            }

                            if (draft.isDuplicate) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "चेतावनी: ${draft.duplicateReason}",
                                    color = TerracottaRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "प्र. ${draft.questionNumber}. ${draft.questionText}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Options
                            Text("A. ${draft.optionA}", fontSize = 12.sp)
                            Text("B. ${draft.optionB}", fontSize = 12.sp)
                            Text("C. ${draft.optionC}", fontSize = 12.sp)
                            Text("D. ${draft.optionD}", fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "सही उत्तर: विकल्प (${draft.correctAnswer})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = ForestGreen
                            )
                            if (draft.explanation.isNotBlank()) {
                                Text(
                                    text = "व्याख्या: ${draft.explanation}",
                                    fontSize = 11.sp,
                                    color = NeutralMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = DividerColor)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Action buttons: Edit, Delete, Approve
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { editingDraft = draft },
                                    modifier = Modifier.testTag("edit_draft_button_${draft.tempId}")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("संपादित करें")
                                }

                                TextButton(
                                    onClick = { viewModel.deleteExtractedDraft(draft.tempId) },
                                    modifier = Modifier.testTag("delete_draft_button_${draft.tempId}")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = TerracottaRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("हटाएं", color = TerracottaRed)
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                Button(
                                    onClick = { viewModel.approveDraft(draft) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.testTag("approve_draft_button_${draft.tempId}")
                                ) {
                                    Text("स्वीकृत करें")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Draft Dialog
    if (editingDraft != null) {
        val draft = editingDraft!!
        var qText by remember { mutableStateOf(draft.questionText) }
        var optA by remember { mutableStateOf(draft.optionA) }
        var optB by remember { mutableStateOf(draft.optionB) }
        var optC by remember { mutableStateOf(draft.optionC) }
        var optD by remember { mutableStateOf(draft.optionD) }
        var ans by remember { mutableStateOf(draft.correctAnswer) }
        var exp by remember { mutableStateOf(draft.explanation) }

        AlertDialog(
            onDismissRequest = { editingDraft = null },
            title = { Text("प्रश्न संपादित करें", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        OutlinedTextField(
                            value = qText,
                            onValueChange = { qText = it },
                            label = { Text("प्रश्न (Question)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = optA,
                            onValueChange = { optA = it },
                            label = { Text("विकल्प A") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = optB,
                            onValueChange = { optB = it },
                            label = { Text("विकल्प B") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = optC,
                            onValueChange = { optC = it },
                            label = { Text("विकल्प C") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = optD,
                            onValueChange = { optD = it },
                            label = { Text("विकल्प D") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = ans,
                            onValueChange = { ans = it.uppercase() },
                            label = { Text("सही उत्तर (A, B, C, D)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = exp,
                            onValueChange = { exp = it },
                            label = { Text("विस्तृत व्याख्या (Explanation)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = draft.copy(
                            questionText = qText,
                            optionA = optA,
                            optionB = optB,
                            optionC = optC,
                            optionD = optD,
                            correctAnswer = ans,
                            explanation = exp
                        )
                        viewModel.updateExtractedDraft(updated)
                        editingDraft = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
                ) {
                    Text("सहेजें")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingDraft = null }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}
