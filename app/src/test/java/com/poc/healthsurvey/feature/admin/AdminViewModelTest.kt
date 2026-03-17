package com.poc.healthsurvey.feature.admin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.poc.healthsurvey.core.network.NetworkResult
import com.poc.healthsurvey.domain.model.SurveyDetail
import com.poc.healthsurvey.domain.model.SurveyListItem
import com.poc.healthsurvey.domain.usecase.GetSurveyDetailUseCase
import com.poc.healthsurvey.domain.usecase.GetSurveyListUseCase
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
class AdminViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getSurveyListUseCase: GetSurveyListUseCase
    private lateinit var getSurveyDetailUseCase: GetSurveyDetailUseCase
    private lateinit var viewModel: AdminViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getSurveyListUseCase = mockk()
        getSurveyDetailUseCase = mockk()
        viewModel = AdminViewModel(getSurveyListUseCase, getSurveyDetailUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial list state is correct`() {
        val state = viewModel.listUiState.value
        assertFalse(state.isLoading)
        assertTrue(state.surveys.isEmpty())
        assertNull(state.errorMessage)
    }

    @Test
    fun `loadSurveyList updates surveys on success`() = runTest {
        val surveys = listOf(
            SurveyListItem(surveyId = "1", email = "a@b.com", score = 20),
            SurveyListItem(surveyId = "2", email = "c@d.com", score = 15)
        )
        coEvery { getSurveyListUseCase() } returns NetworkResult.Success(surveys)

        viewModel.loadSurveyList()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.listUiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.surveys.size)
        assertEquals("1", state.surveys[0].surveyId)
    }

    @Test
    fun `loadSurveyList sets error on failure`() = runTest {
        coEvery { getSurveyListUseCase() } returns
                NetworkResult.Error(message = "Failed to load")

        viewModel.loadSurveyList()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.listUiState.value
        assertFalse(state.isLoading)
        assertEquals("Failed to load", state.errorMessage)
        assertTrue(state.surveys.isEmpty())
    }

    @Test
    fun `loadSurveyList shows empty list correctly`() = runTest {
        coEvery { getSurveyListUseCase() } returns NetworkResult.Success(emptyList())

        viewModel.loadSurveyList()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.listUiState.value
        assertTrue(state.surveys.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun `loadSurveyDetail updates detail on success`() = runTest {
        val detail = SurveyDetail(
            id = "abc",
            user = "test@test.com",
            questions = emptyList()
        )
        coEvery { getSurveyDetailUseCase("abc") } returns NetworkResult.Success(detail)

        viewModel.loadSurveyDetail("abc")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.detailUiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.surveyDetail)
        assertEquals("abc", state.surveyDetail?.id)
        assertEquals("test@test.com", state.surveyDetail?.user)
    }

    @Test
    fun `loadSurveyDetail sets error on failure`() = runTest {
        coEvery { getSurveyDetailUseCase(any()) } returns
                NetworkResult.Error(message = "Not found")

        viewModel.loadSurveyDetail("999")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.detailUiState.value
        assertFalse(state.isLoading)
        assertEquals("Not found", state.errorMessage)
        assertNull(state.surveyDetail)
    }

    @Test
    fun `clearListError sets list errorMessage to null`() = runTest {
        coEvery { getSurveyListUseCase() } returns
                NetworkResult.Error(message = "Error")

        viewModel.loadSurveyList()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearListError()

        assertNull(viewModel.listUiState.value.errorMessage)
    }

    @Test
    fun `clearDetailError sets detail errorMessage to null`() = runTest {
        coEvery { getSurveyDetailUseCase(any()) } returns
                NetworkResult.Error(message = "Error")

        viewModel.loadSurveyDetail("1")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearDetailError()

        assertNull(viewModel.detailUiState.value.errorMessage)
    }
}