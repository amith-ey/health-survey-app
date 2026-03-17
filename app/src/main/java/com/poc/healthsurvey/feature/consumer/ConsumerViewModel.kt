package com.poc.healthsurvey.feature.consumer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poc.healthsurvey.core.network.NetworkResult
import com.poc.healthsurvey.domain.model.SurveySubmission
import com.poc.healthsurvey.domain.usecase.GetSurveyTemplateUseCase
import com.poc.healthsurvey.domain.usecase.SubmitSurveyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsumerViewModel @Inject constructor(
    private val getSurveyTemplateUseCase: GetSurveyTemplateUseCase,
    private val submitSurveyUseCase: SubmitSurveyUseCase
) : ViewModel() {

    companion object {
        const val MAX_SCORE = 32
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }

    private val _uiState = MutableStateFlow(ConsumerUiState())
    val uiState: StateFlow<ConsumerUiState> = _uiState.asStateFlow()

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
                            surveyTemplate = result.data,
                            answers = emptyMap()
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

    fun onOptionSelected(questionId: Int, optionIndex: Int) {
        _uiState.update {
            it.copy(answers = it.answers + (questionId to optionIndex))
        }
    }

    fun getAllQuestions() = _uiState.value.surveyTemplate?.questions ?: emptyList()

    fun allQuestionsAnswered(): Boolean {
        val questions = getAllQuestions()
        return questions.isNotEmpty() &&
                questions.all { _uiState.value.answers.containsKey(it.id) }
    }

    fun submitSurvey() {
        val state = _uiState.value
        val template = state.surveyTemplate ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            val questionsWithSelections = template.questions.map { question ->
                val selectedIndex = state.answers[question.id]
                val updatedOptions = question.options.mapIndexed { index, option ->
                    option.copy(isSelected = index == selectedIndex)
                }
                question.copy(options = updatedOptions)
            }

            val rawScore = questionsWithSelections.sumOf { question ->
                question.options.firstOrNull { it.isSelected }?.score ?: 0
            }

            val submission = SurveySubmission(
                email = state.email,
                score = rawScore,
                questions = questionsWithSelections
            )

            when (val result = submitSurveyUseCase(submission)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            submitScore = rawScore
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = result.message ?: "Submission failed"
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