package com.poc.healthsurvey.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmitSurveyRequestDto(
    @SerialName("email") val email: String,
    @SerialName("score") val score: Int,
    @SerialName("survey") val survey: SurveyPayloadDto
)