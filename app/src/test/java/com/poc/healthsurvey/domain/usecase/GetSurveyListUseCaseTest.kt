package com.poc.healthsurvey.domain.usecase

import com.poc.healthsurvey.core.network.NetworkResult
import com.poc.healthsurvey.domain.model.SurveyListItem
import com.poc.healthsurvey.domain.model.SurveyRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetSurveyListUseCaseTest {

    private lateinit var repository: SurveyRepository
    private lateinit var useCase: GetSurveyListUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetSurveyListUseCase(repository)
    }

    @Test
    fun `invoke returns list on success`() = runTest {
        val list = listOf(
            SurveyListItem(surveyId = "1", email = "a@b.com", score = 20),
            SurveyListItem(surveyId = "2", email = "c@d.com", score = 15)
        )
        coEvery { repository.getSurveyList() } returns NetworkResult.Success(list)

        val result = useCase()

        assertTrue(result is NetworkResult.Success)
        assertEquals(2, (result as NetworkResult.Success).data.size)
    }

    @Test
    fun `invoke returns empty list`() = runTest {
        coEvery { repository.getSurveyList() } returns NetworkResult.Success(emptyList())

        val result = useCase()

        assertTrue(result is NetworkResult.Success)
        assertEquals(0, (result as NetworkResult.Success).data.size)
    }

    @Test
    fun `invoke returns error on failure`() = runTest {
        coEvery { repository.getSurveyList() } returns
                NetworkResult.Error(message = "Failed")

        val result = useCase()

        assertTrue(result is NetworkResult.Error)
    }
}