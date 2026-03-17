package com.poc.healthsurvey.feature.consumer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
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
class ConsumerViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getSurveyTemplateUseCase: GetSurveyTemplateUseCase
    private lateinit var submitSurveyUseCase: SubmitSurveyUseCase
    private lateinit var viewModel: ConsumerViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getSurveyTemplateUseCase = mockk()
        submitSurveyUseCase = mockk()
        viewModel = ConsumerViewModel(getSurveyTemplateUseCase, submitSurveyUseCase)
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

    @Test
    fun `initial state is correct`() = runTest {
        val state = viewModel.uiState.value
        assertEquals("", state.email)
        assertFalse(state.isEmailValid)
        assertNull(state.surveyTemplate)
        assertFalse(state.isLoading)
    }

    @Test
    fun `onEmailChanged updates email and validates correctly`() = runTest {
        viewModel.onEmailChanged("invalid")
        assertFalse(viewModel.uiState.value.isEmailValid)

        viewModel.onEmailChanged("valid@email.com")
        assertTrue(viewModel.uiState.value.isEmailValid)
    }

    @Test
    fun `loadSurveyTemplate updates state with template on success`() = runTest {
        val template = fakeTemplate()
        coEvery { getSurveyTemplateUseCase() } returns NetworkResult.Success(template)

        viewModel.loadSurveyTemplate()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.surveyTemplate)
        assertFalse(state.isLoading)
        assertEquals(1, state.surveyTemplate?.questions?.size)
    }

    @Test
    fun `loadSurveyTemplate sets error message on failure`() = runTest {
        coEvery { getSurveyTemplateUseCase() } returns
                NetworkResult.Error(message = "Network error")

        viewModel.loadSurveyTemplate()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Network error", state.errorMessage)
    }

    @Test
    fun `onOptionSelected stores answer correctly`() = runTest {
        viewModel.onOptionSelected(questionId = 1, optionIndex = 2)
        assertEquals(2, viewModel.uiState.value.answers[1])
    }

    @Test
    fun `allQuestionsAnswered returns false when not all answered`() = runTest {
        val template = fakeTemplate()
        coEvery { getSurveyTemplateUseCase() } returns NetworkResult.Success(template)

        viewModel.loadSurveyTemplate()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.allQuestionsAnswered())
    }

    @Test
    fun `allQuestionsAnswered returns true when all answered`() = runTest {
        val template = fakeTemplate()
        coEvery { getSurveyTemplateUseCase() } returns NetworkResult.Success(template)

        viewModel.loadSurveyTemplate()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onOptionSelected(questionId = 1, optionIndex = 0)

        assertTrue(viewModel.allQuestionsAnswered())
    }

    @Test
    fun `submitSurvey sets submitScore on success`() = runTest {
        val template = fakeTemplate()
        coEvery { getSurveyTemplateUseCase() } returns NetworkResult.Success(template)
        coEvery { submitSurveyUseCase(any()) } returns
                NetworkResult.Success(SurveyResult(email = "test@test.com", score = 3))

        viewModel.onEmailChanged("test@test.com")
        viewModel.loadSurveyTemplate()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onOptionSelected(questionId = 1, optionIndex = 0)
        viewModel.submitSurvey()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.submitScore)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun `submitSurvey sets error on failure`() = runTest {
        val template = fakeTemplate()
        coEvery { getSurveyTemplateUseCase() } returns NetworkResult.Success(template)
        coEvery { submitSurveyUseCase(any()) } returns
                NetworkResult.Error(message = "Submission failed")

        viewModel.onEmailChanged("test@test.com")
        viewModel.loadSurveyTemplate()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onOptionSelected(questionId = 1, optionIndex = 0)
        viewModel.submitSurvey()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Submission failed", state.errorMessage)
        assertFalse(state.isSubmitting)
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

    @Test
    fun `uiState emits loading then success via turbine`() = runTest {
        val template = fakeTemplate()
        coEvery { getSurveyTemplateUseCase() } returns NetworkResult.Success(template)

        viewModel.uiState.test {
            val initial = awaitItem()
            assertFalse(initial.isLoading)

            viewModel.loadSurveyTemplate()

            val loading = awaitItem()
            assertTrue(loading.isLoading)

            testDispatcher.scheduler.advanceUntilIdle()

            val success = awaitItem()
            assertFalse(success.isLoading)
            assertNotNull(success.surveyTemplate)

            cancelAndIgnoreRemainingEvents()
        }
    }
}