package com.poc.healthsurvey.domain.usecase

import com.poc.healthsurvey.core.network.NetworkResult
import com.poc.healthsurvey.domain.model.SurveyRepository
import com.poc.healthsurvey.domain.model.SurveyResult
import com.poc.healthsurvey.domain.model.SurveySubmission
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SubmitSurveyUseCaseTest {

    private lateinit var repository: SurveyRepository
    private lateinit var useCase: SubmitSurveyUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = SubmitSurveyUseCase(repository)
    }

    private fun fakeSubmission() = SurveySubmission(
        email = "test@example.com",
        score = 20,
        questions = emptyList()
    )

    @Test
    fun `invoke returns success with correct score`() = runTest {
        val submission = fakeSubmission()
        val result = SurveyResult(email = submission.email, score = 20)
        coEvery { repository.submitSurvey(submission) } returns NetworkResult.Success(result)

        val response = useCase(submission)

        assertTrue(response is NetworkResult.Success)
        assertEquals(20, (response as NetworkResult.Success).data.score)
    }

    @Test
    fun `invoke returns error on failure`() = runTest {
        val submission = fakeSubmission()
        coEvery { repository.submitSurvey(submission) } returns
                NetworkResult.Error(message = "Submit failed")

        val response = useCase(submission)

        assertTrue(response is NetworkResult.Error)
        assertEquals("Submit failed", (response as NetworkResult.Error).message)
    }

    @Test
    fun `invoke delegates to repository with correct submission`() = runTest {
        val submission = fakeSubmission()
        coEvery { repository.submitSurvey(submission) } returns
                NetworkResult.Success(SurveyResult(email = "test@example.com", score = 20))

        useCase(submission)

        coVerify(exactly = 1) { repository.submitSurvey(submission) }
    }
}