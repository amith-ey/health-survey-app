package com.poc.healthsurvey.data.repository

import com.poc.healthsurvey.core.network.NetworkResult
import com.poc.healthsurvey.data.remote.dto.SubmitSurveyRequestDto
import com.poc.healthsurvey.data.remote.dto.SurveyPayloadDto
import com.poc.healthsurvey.data.remote.dto.SurveyQuestionDto
import com.poc.healthsurvey.data.remote.dto.SurveyOptionDto
import com.poc.healthsurvey.data.remote.dto.toDomain
import com.poc.healthsurvey.data.remote.safeApiCall
import com.poc.healthsurvey.data.remote.service.SurveyApiService
import com.poc.healthsurvey.domain.model.SurveyDetail
import com.poc.healthsurvey.domain.model.SurveyListItem
import com.poc.healthsurvey.domain.model.SurveyRepository
import com.poc.healthsurvey.domain.model.SurveyResult
import com.poc.healthsurvey.domain.model.SurveySubmission
import com.poc.healthsurvey.domain.model.SurveyTemplate
import javax.inject.Inject

class SurveyRepositoryImpl @Inject constructor(
    private val apiService: SurveyApiService
) : SurveyRepository {

    override suspend fun getSurveyTemplate(): NetworkResult<SurveyTemplate> =
        safeApiCall {
            val response = apiService.getSurveyTemplate()
            response.body()?.toDomain()
                ?: throw IllegalStateException("Empty response body")
        }

    override suspend fun submitSurvey(
        submission: SurveySubmission
    ): NetworkResult<SurveyResult> =
        safeApiCall {
            val request = SubmitSurveyRequestDto(
                email = submission.email,
                score = submission.score,
                survey = SurveyPayloadDto(
                    questions = submission.questions.map { q ->
                        SurveyQuestionDto(
                            id = q.id,
                            question = q.question,
                            type = q.type,
                            options = q.options.map { o ->
                                SurveyOptionDto(
                                    title = o.title,
                                    score = o.score,
                                    isSelected = o.isSelected
                                )
                            }
                        )
                    }
                )
            )
            val response = apiService.submitSurvey(request)
            response.body()?.toDomain()
                ?: throw IllegalStateException("Empty response body")
        }

    override suspend fun getSurveyList(): NetworkResult<List<SurveyListItem>> =
        safeApiCall {
            val response = apiService.getSurveyList()
            response.body()?.surveyList?.map { it.toDomain() }
                ?: throw IllegalStateException("Empty response body")
        }

    override suspend fun getSurveyDetail(id: String): NetworkResult<SurveyDetail> =
        safeApiCall {
            val response = apiService.getSurveyDetail(id)
            response.body()?.toDomain()
                ?: throw IllegalStateException("Empty response body")
        }
}