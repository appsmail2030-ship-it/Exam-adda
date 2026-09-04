package com.example.ui.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubjectEntity
import com.example.ui.components.AppTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.ExamViewModel

@Composable
fun SubjectsScreen(
    viewModel: ExamViewModel,
    onBack: () -> Unit,
    onSelectTopic: (Long) -> Unit
) {
    val subjects by viewModel.subjects.collectAsState()
    val allTopics by viewModel.allTopics.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var expandedSubjectId by remember { mutableStateOf<Long?>(18L) } // Art & Culture open by default

    val filteredSubjects = remember(subjects, searchQuery) {
        if (searchQuery.isBlank()) subjects
        else subjects.filter {
            it.nameHindi.contains(searchQuery, ignoreCase = true) ||
            it.nameEnglish.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "विषय एवं टॉपिक सूची",
                subtitle = "राजस्थान पाठ्यक्रम के 19 प्रमुख विषय",
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
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("subjects_search_field"),
                placeholder = { Text("विषय खोजें (उदा. भूगोल, कला, इतिहास...)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RoyalNavy,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                items(filteredSubjects) { subject ->
                    val subjectTopics = allTopics.filter { it.subjectId == subject.id }
                    val isExpanded = expandedSubjectId == subject.id

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .testTag("subject_accordion_${subject.id}")
                    ) {
                        Column {
                            // Subject Header row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedSubjectId = if (isExpanded) null else subject.id
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isExpanded) RoyalNavy else Color(0xFFDBEAFE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = subject.code.removePrefix("SUB_"),
                                        color = if (isExpanded) Color.White else RoyalNavy,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = subject.nameHindi,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${subject.nameEnglish} • ${subjectTopics.size} टॉपिक्स",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = NeutralMedium
                                )
                            }

                            // Expanded Topics list
                            if (isExpanded) {
                                Divider(color = DividerColor)
                                if (subjectTopics.isEmpty()) {
                                    Text(
                                        text = "इस विषय के टॉपिक जल्द ही जोड़े जा रहे हैं।",
                                        color = NeutralMedium,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                } else {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        subjectTopics.forEach { topic ->
                                            Surface(
                                                color = Color(0xFFF8FAFC),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 3.dp)
                                                    .clickable { onSelectTopic(topic.id) }
                                                    .testTag("topic_item_${topic.id}")
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircleOutline,
                                                        contentDescription = null,
                                                        tint = ForestGreen,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Text(
                                                        text = topic.nameHindi,
                                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Icon(
                                                        imageVector = Icons.Default.PlayCircleOutline,
                                                        contentDescription = null,
                                                        tint = RoyalNavy,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
