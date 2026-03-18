package com.poc.healthsurvey.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.poc.healthsurvey.core.SurveyConstants

@Composable
fun ScorePill(
    score: Int,
    maxScore: Int = SurveyConstants.MAX_SCORE,
    modifier: Modifier = Modifier
) {
    val percentage = if (maxScore > 0) (score.toFloat() / maxScore.toFloat()) * 100 else 0f

    val backgroundColor = when {
        percentage >= 70 -> MaterialTheme.colorScheme.primaryContainer
        percentage >= 40 -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.errorContainer
    }

    val textColor = when {
        percentage >= 70 -> MaterialTheme.colorScheme.onPrimaryContainer
        percentage >= 40 -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = backgroundColor,
        modifier = modifier
    ) {
        Text(
            text = "$score / $maxScore",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}