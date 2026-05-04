package se.golfwatch.mobile.ui.courselist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import se.golfwatch.mobile.data.model.CourseOverview
import se.golfwatch.mobile.ui.theme.GolfColors
import se.golfwatch.mobile.ui.theme.GolfTypography

@Composable
fun CourseListScreen(
    onCourseClick: (CourseOverview) -> Unit,
    viewModel: CourseListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CourseListContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onCourseClick = onCourseClick,
    )
}

@Composable
private fun CourseListContent(
    uiState: CourseListUiState,
    onSearchQueryChange: (String) -> Unit,
    onCourseClick: (CourseOverview) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(GolfColors.surfaceBase),
    ) {
        // Header
        Column(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 48.dp),
        ) {
            Text(
                text = "SWEDEN",
                style = GolfTypography.caption,
                color = GolfColors.textCaption,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Courses",
                style = GolfTypography.headlineLarge,
                color = GolfColors.textPrimary,
            )
        }

        Spacer(Modifier.height(16.dp))

        CourseSearchBar(
            query = uiState.searchQuery,
            onQueryChange = onSearchQueryChange,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
        )

        Spacer(Modifier.height(20.dp))

        when {
            uiState.isLoading && uiState.courses.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GolfColors.green, strokeWidth = 2.dp)
                }
            }

            uiState.error != null && uiState.courses.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = uiState.error,
                        style = GolfTypography.bodyMedium,
                        color = GolfColors.textMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp),
                    )
                }
            }

            else -> {
                Text(
                    text = "ALL COURSES",
                    style = GolfTypography.caption,
                    color = GolfColors.textCaption,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
                Spacer(Modifier.height(10.dp))
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(uiState.filteredCourses, key = { it.id }) { course ->
                        CourseCard(course = course, onClick = { onCourseClick(course) })
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .background(GolfColors.surfaceRaised, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 11.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = GolfColors.textCaption,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(10.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = GolfTypography.bodyMedium.copy(color = GolfColors.textPrimary),
                cursorBrush = SolidColor(GolfColors.green),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = "Search courses",
                            style = GolfTypography.bodyMedium,
                            color = GolfColors.textCaption,
                        )
                    }
                    innerTextField()
                },
            )
        }
    }
}

@Composable
private fun CourseCard(
    course: CourseOverview,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(GolfColors.surfaceRaised, RoundedCornerShape(14.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.name,
                    style = GolfTypography.bodyLarge,
                    color = GolfColors.textPrimary,
                )
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Status dot — neutral until geometry is fetched in step 4
                    Box(
                        modifier =
                            Modifier
                                .size(5.dp)
                                .background(GolfColors.statusPartial, CircleShape),
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "Golf course",
                        style = GolfTypography.labelSmall,
                        color = GolfColors.textMuted,
                    )
                }
            }
            Text(
                text = "›",
                style = GolfTypography.headlineMedium,
                color = GolfColors.textPrimary.copy(alpha = 0.4f),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1410)
@Composable
private fun CourseListPreview() {
    CourseListContent(
        uiState =
            CourseListUiState(
                isLoading = false,
                courses =
                    listOf(
                        CourseOverview(1L, "way", "Stockholms Golfklubb", 59.3, 18.0, 0.0, 0.0, 0.0, 0.0),
                        CourseOverview(2L, "relation", "Åtvidabergs GK", 58.2, 16.0, 0.0, 0.0, 0.0, 0.0),
                        CourseOverview(3L, "way", "Bro Hof Slott GC", 59.5, 17.6, 0.0, 0.0, 0.0, 0.0),
                    ),
            ),
        onSearchQueryChange = {},
        onCourseClick = {},
    )
}
