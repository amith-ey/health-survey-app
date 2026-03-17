package com.poc.healthsurvey.domain.usecase

import com.poc.healthsurvey.core.network.NetworkResult
import com.poc.healthsurvey.domain.model.SurveyListItem
import com.poc.healthsurvey.domain.model.SurveyRepository
import javax.inject.Inject

class GetSurveyListUseCase @Inject constructor(
    private val repository: SurveyRepository
) {
    suspend operator fun invoke(): NetworkResult<List<SurveyListItem>> =
        repository.getSurveyList()
}