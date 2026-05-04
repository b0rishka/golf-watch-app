package se.golfwatch.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import se.golfwatch.mobile.ui.courselist.CourseListScreen
import se.golfwatch.mobile.ui.theme.GolfTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GolfTheme {
                // TODO Step 4: replace with NavHost when course detail screen exists
                CourseListScreen(onCourseClick = { /* Step 4 */ })
            }
        }
    }
}
