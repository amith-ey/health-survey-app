package com.poc.healthsurvey.feature.consumer

import com.poc.healthsurvey.domain.model.SurveyTemplate

data class ConsumerUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val isEmailValid: Boolean = false,
    val surveyTemplate: SurveyTemplate? = null,
    val answers: Map<Int, Int> = emptyMap(),
    val isSubmitting: Boolean = false,
    val submitScore: Int? = null
)