package com.poc.healthsurvey.domain.model

data class SurveyOption(
    val title: String,
    val score: Int,
    val isSelected: Boolean = false
)