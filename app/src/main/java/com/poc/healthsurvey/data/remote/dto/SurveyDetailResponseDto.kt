package com.poc.healthsurvey.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SurveyDetailResponseDto(
    @SerialName("id") val id: String,
    @SerialName("user") val user: String,
    @SerialName("survey") val survey: SurveyPayloadDto
)