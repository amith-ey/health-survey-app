package com.poc.healthsurvey.domain.model

data class SurveyDetail(
    val id: String,
    val user: String,
    val questions: List<SurveyQuestion>
)