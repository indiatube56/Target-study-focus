package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AnalyticsParentScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FocusScreen
import com.example.ui.screens.PlannerScreen
import com.example.ui.screens.StudyHubScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.utils.TtsHelper
import com.example.viewmodel.StudyViewModel
import com.example.viewmodel.StudyViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: StudyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize voice assistant TTS helper
        TtsHelper.init(this)

        // Instantiate database, repository, factory and VM cleanly
        val factory = StudyViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[StudyViewModel::class.java]

        enableEdgeToEdge()

        setContent {
            val studentProfile by viewModel.studentProfile.collectAsStateWithLifecycle()
            val themeMode by viewModel.selectedTheme.collectAsStateWithLifecycle()
            val language by viewModel.selectedLanguage.collectAsStateWithLifecycle()
            val isHindi = language == "Hindi"

            MyApplicationTheme(darkTheme = themeMode == "Dark") {
                var selectedScreenIndex by remember { mutableStateOf(0) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.testTag("app_navigation_bar")
                        ) {
                            val items = listOf(
                                Triple(
                                    if (isHindi) "डैशबोर्ड" else "Dashboard",
                                    Icons.Default.Dashboard,
                                    "nav_dashboard"
                                ),
                                Triple(
                                    if (isHindi) "योजनाकार" else "Planner",
                                    Icons.Default.Schedule,
                                    "nav_planner"
                                ),
                                Triple(
                                    if (isHindi) "फोकस रूम" else "Focus Mode",
                                    Icons.Default.Timer,
                                    "nav_focus"
                                ),
                                Triple(
                                    if (isHindi) "स्टडी हब" else "Study Hub",
                                    Icons.Default.Assignment,
                                    "nav_study_hub"
                                ),
                                Triple(
                                    if (isHindi) "अभिभावक" else "Analytics",
                                    Icons.Default.Analytics,
                                    "nav_analytics"
                                )
                            )

                            items.forEachIndexed { index, (label, icon, tag) ->
                                NavigationBarItem(
                                    selected = selectedScreenIndex == index,
                                    onClick = { selectedScreenIndex = index },
                                    icon = { Icon(icon, contentDescription = label) },
                                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.testTag(tag)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    val screenModifier = Modifier.padding(innerPadding)
                    when (selectedScreenIndex) {
                        0 -> DashboardScreen(viewModel = viewModel, modifier = screenModifier)
                        1 -> PlannerScreen(viewModel = viewModel, modifier = screenModifier)
                        2 -> FocusScreen(viewModel = viewModel, modifier = screenModifier)
                        3 -> StudyHubScreen(viewModel = viewModel, modifier = screenModifier)
                        4 -> AnalyticsParentScreen(viewModel = viewModel, modifier = screenModifier)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        TtsHelper.stop()
        super.onDestroy()
    }
}
