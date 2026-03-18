package com.poc.healthsurvey.feature.consumer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.poc.healthsurvey.core.network.NetworkResult
import com.poc.healthsurvey.domain.model.SurveyQuestion
import com.poc.healthsurvey.domain.model.SurveyTemplate
import com.poc.healthsurvey.domain.usecase.GetSurveyTemplateUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConsumerEmailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getSurveyTemplateUseCase: GetSurveyTemplateUseCase
    private lateinit var viewModel: ConsumerEmailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getSurveyTemplateUseCase = mockk()
        viewModel = ConsumerEmailViewModel(getSurveyTemplateUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun fakeTemplate() = SurveyTemplate(questions = emptyList<SurveyQuestion>())

    @Test
    fun `initial state is correct`() {
        val state = viewModel.uiState.value
        assertEquals("", state.email)
        assertFalse(state.isEmailValid)
        assertFalse(state.isLoading)
        assertNull(state.surveyTemplate)
        assertNull(state.errorMessage)
    }

    @Test
    fun `onEmailChanged updates email and validates invalid email`() = runTest {
        viewModel.onEmailChanged("notanemail")
        assertFalse(viewModel.uiState.value.isEmailValid)
        assertEquals("notanemail", viewModel.uiState.value.email)
    }

    @Test
    fun `onEmailChanged validates correct email`() = runTest {
        viewModel.onEmailChanged("test@example.com")
        assertTrue(viewModel.uiState.value.isEmailValid)
    }

    @Test
    fun `onEmailChanged empty string is invalid`() = runTest {
        viewModel.onEmailChanged("")
        assertFalse(viewModel.uiState.value.isEmailValid)
    }

    @Test
    fun `loadSurveyTemplate sets loading then success`() = runTest {
        val template = fakeTemplate()
        coEvery { getSurveyTemplateUseCase() } returns NetworkResult.Success(template)

        viewModel.loadSurveyTemplate()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.surveyTemplate)
        assertNull(state.errorMessage)
    }

    @Test
    fun `loadSurveyTemplate sets error on failure`() = runTest {
        coEvery { getSurveyTemplateUseCase() } returns
                NetworkResult.Error(message = "Network error")

        viewModel.loadSurveyTemplate()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.surveyTemplate)
        assertEquals("Network error", state.errorMessage)
    }

    @Test
    fun `loadSurveyTemplate delegates to use case exactly once`() = runTest {
        coEvery { getSurveyTemplateUseCase() } returns
                NetworkResult.Success(fakeTemplate())

        viewModel.loadSurveyTemplate()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { getSurveyTemplateUseCase() }
    }

    @Test
    fun `clearError sets errorMessage to null`() = runTest {
        coEvery { getSurveyTemplateUseCase() } returns
                NetworkResult.Error(message = "Some error")

        viewModel.loadSurveyTemplate()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearError()

        assertNull(viewModel.uiState.value.errorMessage)
    }
}