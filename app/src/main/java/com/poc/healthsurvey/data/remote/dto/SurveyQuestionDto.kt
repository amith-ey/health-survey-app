package com.poc.healthsurvey.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SurveyQuestionDto(
    @SerialName("id") val id: Int,
    @SerialName("question") val question: String,
    @SerialName("type") val type: String,
    @SerialName("options") val options: List<SurveyOptionDto>
)