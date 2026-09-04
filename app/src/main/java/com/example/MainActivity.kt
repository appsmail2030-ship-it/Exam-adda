package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.screens.*
import com.example.ui.screens.admin.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ExamViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ExamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppNavHost(viewModel = viewModel)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "होम", Icons.Default.Home)
    object Subjects : Screen("subjects", "विषय", Icons.Default.MenuBook)
    object TopicDetail : Screen("topic_detail/{topicId}", "टॉपिक विवरण")
    object TestScreen : Screen("test", "ऑनलाइन टेस्ट")
    object ResultScreen : Screen("result", "परीक्षा परिणाम")
    object PYQ : Screen("pyq", "विगत वर्ष (PYQ)", Icons.Default.HistoryEdu)
    object Bookmarks : Screen("bookmarks", "सहेजे गए प्रश्न")
    object WrongQuestions : Screen("wrong_questions", "गलत प्रश्न अभ्यास")
    object Search : Screen("search", "खोजें")
    object Analytics : Screen("analytics", "प्रगति", Icons.Default.BarChart)
    object AdminLogin : Screen("admin_login", "व्यवस्थापक लॉगिन")
    object AdminDashboard : Screen("admin_dashboard", "व्यवस्थापक पैनल")
    object AdminPdfImport : Screen("admin_pdf_import", "PDF निष्कर्षण")
    object AdminManageExams : Screen("admin_manage_exams", "परीक्षा प्रबंधन")
    object AdminManageSubjects : Screen("admin_manage_subjects", "विषय प्रबंधन")
    object AdminAutoTest : Screen("admin_auto_test", "ऑटो टेस्ट जनरेटर")
    object AdminQuestionBank : Screen("admin_question_bank", "प्रश्न बैंक")
}

@Composable
fun MainAppNavHost(viewModel: ExamViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Subjects,
        Screen.PYQ,
        Screen.Analytics
    )

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Subjects.route,
        Screen.PYQ.route,
        Screen.Analytics.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                screen.icon?.let {
                                    Icon(
                                        imageVector = it,
                                        contentDescription = screen.title
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = RoyalNavy,
                                selectedTextColor = RoyalNavy,
                                indicatorColor = Color(0xFFDBEAFE),
                                unselectedIconColor = NeutralMedium,
                                unselectedTextColor = NeutralMedium
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToSubjects = { navController.navigate(Screen.Subjects.route) },
                    onNavigateToSubjectDetail = { subjectId ->
                        // Navigate to first topic of this subject
                        val topics = viewModel.allTopics.value.filter { it.subjectId == subjectId }
                        if (topics.isNotEmpty()) {
                            navController.navigate("topic_detail/${topics[0].id}")
                        } else {
                            navController.navigate(Screen.Subjects.route)
                        }
                    },
                    onNavigateToPyq = { navController.navigate(Screen.PYQ.route) },
                    onNavigateToDailyTest = {
                        val daily = viewModel.dailyTests.value.firstOrNull()
                        if (daily != null) {
                            viewModel.startTest(daily)
                            navController.navigate(Screen.TestScreen.route)
                        } else {
                            val all = viewModel.allTests.value.firstOrNull()
                            if (all != null) {
                                viewModel.startTest(all)
                                navController.navigate(Screen.TestScreen.route)
                            }
                        }
                    },
                    onNavigateToMockTests = {
                        val mock = viewModel.mockTests.value.firstOrNull()
                        if (mock != null) {
                            viewModel.startTest(mock)
                            navController.navigate(Screen.TestScreen.route)
                        } else {
                            val all = viewModel.allTests.value.firstOrNull()
                            if (all != null) {
                                viewModel.startTest(all)
                                navController.navigate(Screen.TestScreen.route)
                            }
                        }
                    },
                    onNavigateToBookmarks = { navController.navigate(Screen.Bookmarks.route) },
                    onNavigateToWrongQuestions = { navController.navigate(Screen.WrongQuestions.route) },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                    onNavigateToAdmin = {
                        if (viewModel.isAdminLoggedIn.value) {
                            navController.navigate(Screen.AdminDashboard.route)
                        } else {
                            navController.navigate(Screen.AdminLogin.route)
                        }
                    }
                )
            }

            composable(Screen.Subjects.route) {
                SubjectsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSelectTopic = { topicId ->
                        navController.navigate("topic_detail/$topicId")
                    }
                )
            }

            composable(
                route = Screen.TopicDetail.route,
                arguments = listOf(navArgument("topicId") { type = NavType.LongType })
            ) { backStackEntry ->
                val topicId = backStackEntry.arguments?.getLong("topicId") ?: 1801L
                TopicDetailScreen(
                    topicId = topicId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onStartTest = { test ->
                        viewModel.startTest(test)
                        navController.navigate(Screen.TestScreen.route)
                    }
                )
            }

            composable(Screen.TestScreen.route) {
                ExamTestScreen(
                    viewModel = viewModel,
                    onTestSubmitted = {
                        navController.navigate(Screen.ResultScreen.route) {
                            popUpTo(Screen.TestScreen.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ResultScreen.route) {
                ResultScreen(
                    viewModel = viewModel,
                    onRetryTest = {
                        navController.navigate(Screen.TestScreen.route) {
                            popUpTo(Screen.ResultScreen.route) { inclusive = true }
                        }
                    },
                    onBackToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.PYQ.route) {
                PreviousYearScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Bookmarks.route) {
                BookmarksScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.WrongQuestions.route) {
                WrongQuestionsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Analytics.route) {
                AnalyticsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // Admin Routes
            composable(Screen.AdminLogin.route) {
                AdminLoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.AdminLogin.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AdminDashboard.route) {
                AdminDashboardScreen(
                    viewModel = viewModel,
                    onNavigateToPdfImport = { navController.navigate(Screen.AdminPdfImport.route) },
                    onNavigateToQuestionBank = { navController.navigate(Screen.AdminQuestionBank.route) },
                    onNavigateToManageExams = { navController.navigate(Screen.AdminManageExams.route) },
                    onNavigateToManageSubjects = { navController.navigate(Screen.AdminManageSubjects.route) },
                    onNavigateToAutoTest = { navController.navigate(Screen.AdminAutoTest.route) },
                    onLogout = {
                        viewModel.logoutAdmin()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AdminPdfImport.route) {
                AdminPdfImportScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AdminManageExams.route) {
                AdminManageExamsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AdminManageSubjects.route) {
                AdminManageSubjectsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AdminAutoTest.route) {
                AdminAutoTestScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AdminQuestionBank.route) {
                AdminQuestionBankScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
