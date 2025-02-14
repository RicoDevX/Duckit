package com.chrisrich.duckit.domain.usecase.post

import com.chrisrich.duckit.data.repository.PostRepository
import com.chrisrich.duckit.domain.model.NewPostRequest
import com.chrisrich.duckit.domain.model.PostResponse
import kotlinx.coroutines.flow.Flow

class NewPostUseCase(private val repository: PostRepository) {
    operator fun invoke(token: String, request: NewPostRequest): Flow<Result<PostResponse>> {
        return repository.newPost(token, request)
    }
}
