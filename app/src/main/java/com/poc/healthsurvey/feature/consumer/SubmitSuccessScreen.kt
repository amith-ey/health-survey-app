package com.poc.healthsurvey.feature.consumer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poc.healthsurvey.core.SurveyConstants

@Composable
fun SubmitSuccessScreen(
    score: Int,
    onBackToHome: () -> Unit
) {
    val maxScore = SurveyConstants.MAX_SCORE
    val percentage = if (maxScore > 0) (score.toFloat() / maxScore.toFloat()) * 100 else 0f

    val scoreColor = when {
        percentage >= 70 -> MaterialTheme.colorScheme.primaryContainer
        percentage >= 40 -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.errorContainer
    }

    val scoreLabel = when {
        percentage >= 70 -> "Great Result!"
        percentage >= 40 -> "Good Effort!"
        else -> "Needs Improvement"
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Survey Complete!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Thank you for completing the health survey.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            Surface(
                shape = CircleShape,
                color = scoreColor,
                modifier = Modifier.size(180.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$score",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "/ $maxScore",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = scoreLabel,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onBackToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Back to Home")
            }
        }
    }
}