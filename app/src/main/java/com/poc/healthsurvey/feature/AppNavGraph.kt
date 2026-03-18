package com.poc.healthsurvey.feature

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.poc.healthsurvey.core.SurveyConstants
import com.poc.healthsurvey.feature.admin.AdminSurveyDetailScreen
import com.poc.healthsurvey.feature.admin.AdminSurveyListScreen
import com.poc.healthsurvey.feature.consumer.ConsumerEmailScreen
import com.poc.healthsurvey.feature.consumer.SubmitSuccessScreen
import com.poc.healthsurvey.feature.consumer.SurveyQuestionsScreen
import com.poc.healthsurvey.feature.home.HomeScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        composable(Screen.Home.route) {
            HomeScreen(
                onConsumerClick = {
                    navController.navigate(Screen.ConsumerEmail.route)
                },
                onAdminClick = {
                    navController.navigate(Screen.AdminSurveyList.route)
                }
            )
        }

        composable(Screen.ConsumerEmail.route) {
            ConsumerEmailScreen(
                onSurveyLoaded = { email ->
                    navController.navigate(Screen.SurveyQuestions.createRoute(email))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SurveyQuestions.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            SurveyQuestionsScreen(
                email = email,
                onSubmitSuccess = { score ->
                    navController.navigate(Screen.SubmitSuccess.createRoute(score)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SubmitSuccess.route,
            arguments = listOf(
                navArgument("score") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            SubmitSuccessScreen(
                score = score,
                onBackToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AdminSurveyList.route) {
            AdminSurveyListScreen(
                onSurveyClick = { surveyId ->
                    navController.navigate(Screen.AdminSurveyDetail.createRoute(surveyId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AdminSurveyDetail.route,
            arguments = listOf(
                navArgument("surveyId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val surveyId = backStackEntry.arguments?.getString("surveyId") ?: ""
            AdminSurveyDetailScreen(
                surveyId = surveyId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}