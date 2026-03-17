package com.poc.healthsurvey.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SurveyPayloadDto(
    @SerialName("questions") val questions: List<SurveyQuestionDto>
)