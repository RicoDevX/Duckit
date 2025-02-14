package com.chrisrich.duckit.domain.usecase.auth

import com.chrisrich.duckit.data.repository.AuthRepository
import com.chrisrich.duckit.domain.model.AuthRequest
import com.chrisrich.duckit.domain.model.AuthResponse
import kotlinx.coroutines.flow.Flow

class LogInUseCase(private val repository: AuthRepository) {
    operator fun invoke(request: AuthRequest): Flow<Result<AuthResponse>> {
        return repository.signIn(request)
    }
}
