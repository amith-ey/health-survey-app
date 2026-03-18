package com.poc.healthsurvey.feature.consumer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.poc.healthsurvey.core.network.NetworkResult
import com.poc.healthsurvey.domain.model.SurveyOption
import com.poc.healthsurvey.domain.model.SurveyQuestion
import com.poc.healthsurvey.domain.model.SurveyResult
import com.poc.healthsurvey.domain.model.SurveyTemplate
import com.poc.healthsurvey.domain.usecase.GetSurveyTemplateUseCase
import com.poc.healthsurvey.domain.usecase.SubmitSurveyUseCase
import io.mockk.coEvery
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
class ConsumerSurveyViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getSurveyTemplateUseCase: GetSurveyTemplateUseCase
    private lateinit var submitSurveyUseCase: SubmitSurveyUseCase
    private lateinit var viewModel: ConsumerSurveyViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getSurveyTemplateUseCase = mockk()
        submitSurveyUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun fakeTemplate() = SurveyTemplate(
        questions = listOf(
            SurveyQuestion(
                id = 1,
                question = "How do you feel?",
                type = "MCQ",
                options = listOf(
                    SurveyOption(title = "Great", score = 3, isSelected = false),
                    SurveyOption(title = "Okay", score = 2, isSelected = false),
                    SurveyOption(title = "Bad", score = 1, isSelected = false)
                )
            )
        )
    )

    private fun setupViewModel() {
        coEvery { getSurveyTemplateUseCase() } returns NetworkResult.Success(fakeTemplate())
        viewModel = ConsumerSurveyViewModel(getSurveyTemplateUseCase, submitSurveyUseCase)
    }

    @Test
    fun `init loads survey template automatically`() = runTest {
        setupViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.surveyTemplate)
        assertFalse(state.isLoading)
    }

    @Test
    fun `init sets error when template load fails`() = runTest {
        coEvery { getSurveyTemplateUseCase() } returns
                NetworkResult.Error(message = "Load failed")
        viewModel = ConsumerSurveyViewModel(getSurveyTemplateUseCase, submitSurveyUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.surveyTemplate)
        assertEquals("Load failed", state.errorMessage)
    }

    @Test
    fun `onOptionSelected stores answer correctly`() = runTest {
        setupViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onOptionSelected(questionId = 1, optionIndex = 2)

        assertEquals(2, viewModel.uiState.value.answers[1])
    }

    @Test
    fun `allQuestionsAnswered returns false when not all answered`() = runTest {
        setupViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.allQuestionsAnswered())
    }

    @Test
    fun `allQuestionsAnswered returns true when all answered`() = runTest {
        setupViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onOptionSelected(questionId = 1, optionIndex = 0)

        assertTrue(viewModel.allQuestionsAnswered())
    }

    @Test
    fun `submitSurvey sets submitScore on success`() = runTest {
        setupViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { submitSurveyUseCase(any()) } returns
                NetworkResult.Success(SurveyResult(email = "test@test.com", score = 3))

        viewModel.onOptionSelected(questionId = 1, optionIndex = 0)
        viewModel.submitSurvey("test@test.com")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.submitScore)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun `submitSurvey sets error on failure`() = runTest {
        setupViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { submitSurveyUseCase(any()) } returns
                NetworkResult.Error(message = "Submission failed")

        viewModel.onOptionSelected(questionId = 1, optionIndex = 0)
        viewModel.submitSurvey("test@test.com")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Submission failed", state.errorMessage)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun `submitSurvey does nothing when template is null`() = runTest {
        coEvery { getSurveyTemplateUseCase() } returns
                NetworkResult.Error(message = "Failed")
        viewModel = ConsumerSurveyViewModel(getSurveyTemplateUseCase, submitSurveyUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.submitSurvey("test@test.com")
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.submitScore)
    }

    @Test
    fun `clearError sets errorMessage to null`() = runTest {
        coEvery { getSurveyTemplateUseCase() } returns
                NetworkResult.Error(message = "Some error")
        viewModel = ConsumerSurveyViewModel(getSurveyTemplateUseCase, submitSurveyUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearError()

        assertNull(viewModel.uiState.value.errorMessage)
    }
}