package com.poc.healthsurvey.domain.model

import com.poc.healthsurvey.core.network.NetworkResult

interface SurveyRepository {
    suspend fun getSurveyTemplate(): NetworkResult<SurveyTemplate>
    suspend fun submitSurvey(submission: SurveySubmission): NetworkResult<SurveyResult>
    suspend fun getSurveyList(): NetworkResult<List<SurveyListItem>>
    suspend fun getSurveyDetail(id: String): NetworkResult<SurveyDetail>
}