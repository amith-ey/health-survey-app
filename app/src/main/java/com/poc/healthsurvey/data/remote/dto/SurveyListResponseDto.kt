package com.poc.healthsurvey.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SurveyListResponseDto(
    @SerialName("surveyList") val surveyList: List<SurveyListItemDto>
)

@Serializable
data class SurveyListItemDto(
    @SerialName("email") val email: String,
    @SerialName("score") val score: Int,
    @SerialName("surveyId") val surveyId: String
)