package com.poc.healthsurvey.domain.model

data class SurveyQuestion(
    val id: Int,
    val question: String,
    val type: String,
    val options: List<SurveyOption>
)