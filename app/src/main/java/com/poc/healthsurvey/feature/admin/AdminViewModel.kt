package com.poc.healthsurvey.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poc.healthsurvey.core.network.NetworkResult
import com.poc.healthsurvey.domain.usecase.GetSurveyDetailUseCase
import com.poc.healthsurvey.domain.usecase.GetSurveyListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val getSurveyListUseCase: GetSurveyListUseCase,
    private val getSurveyDetailUseCase: GetSurveyDetailUseCase
) : ViewModel() {

    private val _listUiState = MutableStateFlow(AdminListUiState())
    val listUiState: StateFlow<AdminListUiState> = _listUiState.asStateFlow()

    private val _detailUiState = MutableStateFlow(AdminDetailUiState())
    val detailUiState: StateFlow<AdminDetailUiState> = _detailUiState.asStateFlow()

    fun loadSurveyList() {
        viewModelScope.launch {
            _listUiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = getSurveyListUseCase()) {
                is NetworkResult.Success -> {
                    _listUiState.update {
                        it.copy(
                            isLoading = false,
                            surveys = result.data
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _listUiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Failed to load surveys"
                        )
                    }
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun loadSurveyDetail(id: String) {
        viewModelScope.launch {
            _detailUiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = getSurveyDetailUseCase(id)) {
                is NetworkResult.Success -> {
                    _detailUiState.update {
                        it.copy(
                            isLoading = false,
                            surveyDetail = result.data
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _detailUiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Failed to load survey detail"
                        )
                    }
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun clearListError() {
        _listUiState.update { it.copy(errorMessage = null) }
    }

    fun clearDetailError() {
        _detailUiState.update { it.copy(errorMessage = null) }
    }
}