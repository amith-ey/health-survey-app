package com.poc.healthsurvey.feature.consumer

import com.poc.healthsurvey.domain.model.SurveyTemplate

data class ConsumerEmailUiState(
    val email: String = "",
    val isEmailValid: Boolean = false,
    val isLoading: Boolean = false,
    val surveyTemplate: SurveyTemplate? = null,
    val errorMessage: String? = null
)