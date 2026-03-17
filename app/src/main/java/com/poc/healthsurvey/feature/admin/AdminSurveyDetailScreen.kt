package com.poc.healthsurvey.feature.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poc.healthsurvey.core.ui.EmptyStateScreen
import com.poc.healthsurvey.core.ui.ErrorBanner
import com.poc.healthsurvey.core.ui.LoadingScreen
import com.poc.healthsurvey.core.ui.ScorePill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSurveyDetailScreen(
    surveyId: String,
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.detailUiState.collectAsState()

    LaunchedEffect(surveyId) {
        viewModel.loadSurveyDetail(surveyId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Survey Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Inline error banner with retry
            if (uiState.errorMessage != null) {
                ErrorBanner(
                    message = uiState.errorMessage!!,
                    onRetry = {
                        viewModel.clearDetailError()
                        viewModel.loadSurveyDetail(surveyId)
                    }
                )
            }

            when {
                uiState.isLoading -> {
                    LoadingScreen(message = "Loading survey detail...")
                }

                uiState.surveyDetail != null -> {
                    val detail = uiState.surveyDetail!!
                    val maxScore = detail.questions.sumOf { question ->
                        question.options.maxOfOrNull { it.score } ?: 0
                    }
                    val actualScore = detail.questions.sumOf { q ->
                        q.options.firstOrNull { it.isSelected }?.score ?: 0
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Header card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = detail.user,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Survey ID: ${detail.id}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                ScorePill(
                                    score = actualScore,
                                    maxScore = maxScore
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Responses",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        detail.questions.forEachIndexed { qIndex, question ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "${qIndex + 1}. ${question.question}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    HorizontalDivider()

                                    question.options.forEach { option ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = option.isSelected,
                                                onClick = null,
                                                enabled = false
                                            )
                                            Text(
                                                text = option.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = if (option.isSelected)
                                                    MaterialTheme.colorScheme.primary
                                                else
                                                    MaterialTheme.colorScheme.onSurface,
                                                fontWeight = if (option.isSelected)
                                                    FontWeight.SemiBold
                                                else
                                                    FontWeight.Normal,
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        }
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {
                    EmptyStateScreen(message = "Survey detail not found.")
                }
            }
        }
    }
}