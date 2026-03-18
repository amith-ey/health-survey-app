package com.poc.healthsurvey.feature

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ConsumerEmail : Screen("consumer_email")
    object SurveyQuestions : Screen("survey_questions/{email}") {
        fun createRoute(email: String) = "survey_questions/$email"
    }
    object SubmitSuccess : Screen("submit_success/{score}") {
        fun createRoute(score: Int) = "submit_success/$score"
    }
    object AdminSurveyList : Screen("admin_survey_list")
    object AdminSurveyDetail : Screen("admin_survey_detail/{surveyId}") {
        fun createRoute(surveyId: String) = "admin_survey_detail/$surveyId"
    }
}