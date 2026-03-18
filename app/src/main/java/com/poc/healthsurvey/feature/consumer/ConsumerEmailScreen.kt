package com.poc.healthsurvey.feature.consumer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poc.healthsurvey.core.ui.ErrorBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsumerEmailScreen(
    onSurveyLoaded: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ConsumerEmailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.surveyTemplate) {
        if (uiState.surveyTemplate != null) {
            onSurveyLoaded(uiState.email)
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
            if (uiState.errorMessage != null) {
                ErrorBanner(
                    message = uiState.errorMessage!!,
                    onRetry = {
                        viewModel.clearError()
                        viewModel.loadSurveyTemplate()
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Enter your email to begin",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChanged,
                    label = { Text("Email address") },
                    placeholder = { Text("you@example.com") },
                    isError = uiState.email.isNotEmpty() && !uiState.isEmailValid,
                    supportingText = {
                        if (uiState.email.isNotEmpty() && !uiState.isEmailValid) {
                            Text(
                                text = "Please enter a valid email address",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.loadSurveyTemplate() },
                    enabled = uiState.isEmailValid && !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .height(20.dp)
                                .padding(end = 8.dp)
                        )
                    }
                    Text("Start Survey")
                }
            }
        }
    }
}