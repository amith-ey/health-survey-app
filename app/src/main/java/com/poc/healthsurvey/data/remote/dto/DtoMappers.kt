package com.poc.healthsurvey.data.remote.dto

import com.poc.healthsurvey.domain.model.SurveyDetail
import com.poc.healthsurvey.domain.model.SurveyListItem
import com.poc.healthsurvey.domain.model.SurveyOption
import com.poc.healthsurvey.domain.model.SurveyQuestion
import com.poc.healthsurvey.domain.model.SurveyResult
import com.poc.healthsurvey.domain.model.SurveyTemplate

fun SurveyOptionDto.toDomain() = SurveyOption(
    title = title,
    score = score,
    isSelected = isSelected
)

fun SurveyQuestionDto.toDomain() = SurveyQuestion(
    id = id,
    question = question,
    type = type,
    options = options.map { it.toDomain() }
)

fun SurveyTemplateResponseDto.toDomain() = SurveyTemplate(
    questions = questions.map { it.toDomain() }
)

fun SubmitSurveyResponseDto.toDomain() = SurveyResult(
    email = email,
    score = score
)

fun SurveyListItemDto.toDomain() = SurveyListItem(
    surveyId = surveyId,
    email = email,
    score = score
)

fun SurveyDetailResponseDto.toDomain() = SurveyDetail(
    id = id,
    user = user,
    questions = survey.questions.map { it.toDomain() }
)