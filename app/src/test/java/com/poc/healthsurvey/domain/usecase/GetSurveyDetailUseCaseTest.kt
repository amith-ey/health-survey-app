package com.poc.healthsurvey.domain.usecase

import com.poc.healthsurvey.core.network.NetworkResult
import com.poc.healthsurvey.domain.model.SurveyDetail
import com.poc.healthsurvey.domain.model.SurveyRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetSurveyDetailUseCaseTest {

    private lateinit var repository: SurveyRepository
    private lateinit var useCase: GetSurveyDetailUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetSurveyDetailUseCase(repository)
    }

    @Test
    fun `invoke returns detail on success`() = runTest {
        val detail = SurveyDetail(id = "1", user = "test@test.com", questions = emptyList())
        coEvery { repository.getSurveyDetail("1") } returns NetworkResult.Success(detail)

        val result = useCase("1")

        assertTrue(result is NetworkResult.Success)
        assertEquals("1", (result as NetworkResult.Success).data.id)
    }

    @Test
    fun `invoke passes correct id to repository`() = runTest {
        val detail = SurveyDetail(id = "abc", user = "x@y.com", questions = emptyList())
        coEvery { repository.getSurveyDetail("abc") } returns NetworkResult.Success(detail)

        useCase("abc")

        coVerify(exactly = 1) { repository.getSurveyDetail("abc") }
    }

    @Test
    fun `invoke returns error on failure`() = runTest {
        coEvery { repository.getSurveyDetail(any()) } returns
                NetworkResult.Error(message = "Not found")

        val result = useCase("999")

        assertTrue(result is NetworkResult.Error)
        assertEquals("Not found", (result as NetworkResult.Error).message)
    }
}