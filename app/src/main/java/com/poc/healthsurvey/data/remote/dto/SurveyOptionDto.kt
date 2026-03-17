package com.poc.healthsurvey.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SurveyOptionDto(
    @SerialName("title") val title: String,
    @SerialName("score") val score: Int,
    @SerialName("isSelected") val isSelected: Boolean = false
)