package com.poc.healthsurvey.domain.usecase

import com.poc.healthsurvey.core.network.NetworkResult
import com.poc.healthsurvey.domain.model.SurveyDetail
import com.poc.healthsurvey.domain.model.SurveyRepository
import javax.inject.Inject

class GetSurveyDetailUseCase @Inject constructor(
    private val repository: SurveyRepository
) {
    suspend operator fun invoke(id: String): NetworkResult<SurveyDetail> =
        repository.getSurveyDetail(id)
}