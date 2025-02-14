package com.chrisrich.duckit.data.repository

import com.chrisrich.duckit.data.remote.DuckitApi
import com.chrisrich.duckit.domain.model.AuthRequest
import com.chrisrich.duckit.domain.model.AuthResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AuthRepository(private val apiService: DuckitApi) {

    fun signIn(request: AuthRequest): Flow<Result<AuthResponse>> = flow {
        emit(Result.success(apiService.signIn(request)))
    }.catch { e -> emit(Result.failure(e)) }

    fun signUp(request: AuthRequest): Flow<Result<AuthResponse>> = flow {
        emit(Result.success(apiService.signUp(request)))
    }.catch { e -> emit(Result.failure(e)) }
}
