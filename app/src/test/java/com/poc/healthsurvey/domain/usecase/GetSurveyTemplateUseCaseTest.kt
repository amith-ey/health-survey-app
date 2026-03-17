package com.poc.healthsurvey.domain.usecase

import com.poc.healthsurvey.core.network.NetworkResult
import com.poc.healthsurvey.domain.model.SurveyQuestion
import com.poc.healthsurvey.domain.model.SurveyRepository
import com.poc.healthsurvey.domain.model.SurveyTemplate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetSurveyTemplateUseCaseTest {

    private lateinit var repository: SurveyRepository
    private lateinit var useCase: GetSurveyTemplateUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetSurveyTemplateUseCase(repository)
    }

    @Test
    fun `invoke returns success when repository returns template`() = runTest {
        val template = SurveyTemplate(questions = emptyList())
        coEvery { repository.getSurveyTemplate() } returns NetworkResult.Success(template)

        val result = useCase()

        assertTrue(result is NetworkResult.Success)
        assertEquals(template, (result as NetworkResult.Success).data)
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        coEvery { repository.getSurveyTemplate() } returns
                NetworkResult.Error(message = "Network error")

        val result = useCase()

        assertTrue(result is NetworkResult.Error)
        assertEquals("Network error", (result as NetworkResult.Error).message)
    }

    @Test
    fun `invoke delegates to repository exactly once`() = runTest {
        coEvery { repository.getSurveyTemplate() } returns
                NetworkResult.Success(SurveyTemplate(emptyList()))

        useCase()

        coVerify(exactly = 1) { repository.getSurveyTemplate() }
    }
}