package com.poc.healthsurvey.domain.usecase

import com.poc.healthsurvey.core.network.NetworkResult
import com.poc.healthsurvey.domain.model.SurveyRepository
import com.poc.healthsurvey.domain.model.SurveyResult
import com.poc.healthsurvey.domain.model.SurveySubmission
import javax.inject.Inject

class SubmitSurveyUseCase @Inject constructor(
    private val repository: SurveyRepository
) {
    suspend operator fun invoke(submission: SurveySubmission): NetworkResult<SurveyResult> =
        repository.submitSurvey(submission)
}