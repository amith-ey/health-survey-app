package com.poc.healthsurvey.feature.consumer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poc.healthsurvey.core.SurveyConstants
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
class ConsumerSurveyViewModel @Inject constructor(
    private val getSurveyTemplateUseCase: GetSurveyTemplateUseCase,
    private val submitSurveyUseCase: SubmitSurveyUseCase
) : ViewModel() {

    companion object {
        const val MAX_SCORE = SurveyConstants.MAX_SCORE
    }

    private val _uiState = MutableStateFlow(ConsumerSurveyUiState())
    val uiState: StateFlow<ConsumerSurveyUiState> = _uiState.asStateFlow()

    init {
        loadTemplate()
    }

    private fun loadTemplate() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getSurveyTemplateUseCase()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, surveyTemplate = result.data)
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

    fun allQuestionsAnswered(): Boolean {
        val questions = _uiState.value.surveyTemplate?.questions ?: return false
        return questions.isNotEmpty() &&
                questions.all { _uiState.value.answers.containsKey(it.id) }
    }

    fun submitSurvey(email: String) {
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
                email = email,
                score = rawScore,
                questions = questionsWithSelections
            )

            when (val result = submitSurveyUseCase(submission)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(isSubmitting = false, submitScore = rawScore)
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