package com.poc.healthsurvey.feature.consumer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poc.healthsurvey.core.ui.ErrorBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyQuestionsScreen(
    onSubmitSuccess: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: ConsumerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val allQuestions = viewModel.getAllQuestions()
    val allAnswered = viewModel.allQuestionsAnswered()

    LaunchedEffect(uiState.submitScore) {
        uiState.submitScore?.let { score ->
            onSubmitSuccess(score)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Survey") },
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
            // Inline error banner
            if (uiState.errorMessage != null) {
                ErrorBanner(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.clearError() }
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                allQuestions.forEachIndexed { qIndex, question ->
                    val selectedOptionIndex = uiState.answers[question.id]

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "${qIndex + 1}. ${question.question}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            HorizontalDivider()

                            Column(modifier = Modifier.selectableGroup()) {
                                question.options.forEachIndexed { index, option ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .selectable(
                                                selected = selectedOptionIndex == index,
                                                onClick = {
                                                    viewModel.onOptionSelected(
                                                        question.id,
                                                        index
                                                    )
                                                },
                                                role = Role.RadioButton
                                            )
                                            .padding(vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedOptionIndex == index,
                                            onClick = null
                                        )
                                        Text(
                                            text = option.title,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.padding(start = 12.dp)
                                        )
                                    }
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = { viewModel.submitSurvey() },
                enabled = allAnswered && !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp)
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .height(20.dp)
                            .padding(end = 8.dp)
                    )
                }
                Text(
                    text = if (allAnswered) "Submit Survey"
                    else "Answer all questions to submit",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
