package com.poc.healthsurvey.data.repository

import com.poc.healthsurvey.data.remote.service.SurveyApiService
import com.poc.healthsurvey.domain.model.SurveyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideSurveyApiService(retrofit: Retrofit): SurveyApiService =
        retrofit.create(SurveyApiService::class.java)

    @Provides
    @Singleton
    fun provideSurveyRepository(
        impl: SurveyRepositoryImpl
    ): SurveyRepository = impl
}