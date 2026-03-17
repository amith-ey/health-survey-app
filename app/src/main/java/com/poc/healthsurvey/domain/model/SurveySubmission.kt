package com.poc.healthsurvey.domain.model

data class SurveySubmission(
    val email: String,
    val score: Int,
    val questions: List<SurveyQuestion>
)