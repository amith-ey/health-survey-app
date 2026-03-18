package com.poc.healthsurvey.feature.consumer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poc.healthsurvey.core.network.NetworkResult
import com.poc.healthsurvey.domain.usecase.GetSurveyTemplateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsumerEmailViewModel @Inject constructor(
    private val getSurveyTemplateUseCase: GetSurveyTemplateUseCase
) : ViewModel() {

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }

    private val _uiState = MutableStateFlow(ConsumerEmailUiState())
    val uiState: StateFlow<ConsumerEmailUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                isEmailValid = EMAIL_REGEX.matches(email)
            )
        }
    }

    fun loadSurveyTemplate() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = getSurveyTemplateUseCase()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            surveyTemplate = result.data
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Failed to load survey"
                        )
                    }
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}