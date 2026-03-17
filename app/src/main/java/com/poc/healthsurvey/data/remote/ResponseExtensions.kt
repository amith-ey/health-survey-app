package com.poc.healthsurvey.data.remote

import com.poc.healthsurvey.core.network.NetworkResult

suspend fun <T> safeApiCall(call: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(call())
    } catch (e: Exception) {
        NetworkResult.Error(message = e.message ?: "Unknown error occurred")
    }
}