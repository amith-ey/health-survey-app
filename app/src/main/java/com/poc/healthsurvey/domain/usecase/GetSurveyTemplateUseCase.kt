package com.poc.healthsurvey.domain.usecase

import com.poc.healthsurvey.core.network.NetworkResult
import com.poc.healthsurvey.domain.model.SurveyRepository
import com.poc.healthsurvey.domain.model.SurveyTemplate
import javax.inject.Inject

class GetSurveyTemplateUseCase @Inject constructor(
    private val repository: SurveyRepository
) {
    suspend operator fun invoke(): NetworkResult<SurveyTemplate> =
        repository.getSurveyTemplate()
}