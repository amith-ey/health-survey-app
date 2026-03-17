package com.poc.healthsurvey.domain.model

data class SurveyListItem(
    val surveyId: String,
    val email: String,
    val score: Int
)