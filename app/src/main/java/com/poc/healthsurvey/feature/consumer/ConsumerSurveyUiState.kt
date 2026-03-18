package com.poc.healthsurvey.feature.consumer

import com.poc.healthsurvey.domain.model.SurveyTemplate

data class ConsumerSurveyUiState(
    val isLoading: Boolean = false,
    val surveyTemplate: SurveyTemplate? = null,
    val answers: Map<Int, Int> = emptyMap(),
    val isSubmitting: Boolean = false,
    val submitScore: Int? = null,
    val errorMessage: String? = null
)