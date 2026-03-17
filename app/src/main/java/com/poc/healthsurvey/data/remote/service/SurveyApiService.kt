package com.poc.healthsurvey.data.remote.service

import com.poc.healthsurvey.data.remote.dto.SubmitSurveyRequestDto
import com.poc.healthsurvey.data.remote.dto.SubmitSurveyResponseDto
import com.poc.healthsurvey.data.remote.dto.SurveyDetailResponseDto
import com.poc.healthsurvey.data.remote.dto.SurveyListResponseDto
import com.poc.healthsurvey.data.remote.dto.SurveyTemplateResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SurveyApiService {

    @GET("surveyTemplate")
    suspend fun getSurveyTemplate(): Response<SurveyTemplateResponseDto>

    @POST("survey")
    suspend fun submitSurvey(
        @Body request: SubmitSurveyRequestDto
    ): Response<SubmitSurveyResponseDto>

    @GET("surveyList")
    suspend fun getSurveyList(): Response<SurveyListResponseDto>

    @GET("survey")
    suspend fun getSurveyDetail(
        @Query("id") id: String
    ): Response<SurveyDetailResponseDto>
}