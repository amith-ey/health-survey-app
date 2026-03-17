package com.poc.healthsurvey.feature.admin

import com.poc.healthsurvey.domain.model.SurveyDetail
import com.poc.healthsurvey.domain.model.SurveyListItem

data class AdminListUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val surveys: List<SurveyListItem> = emptyList()
)

data class AdminDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val surveyDetail: SurveyDetail? = null
)