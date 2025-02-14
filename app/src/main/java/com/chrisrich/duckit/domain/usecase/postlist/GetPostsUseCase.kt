package com.chrisrich.duckit.domain.usecase.postlist

import com.chrisrich.duckit.data.repository.PostRepository
import com.chrisrich.duckit.domain.model.PostListResponse
import kotlinx.coroutines.flow.Flow

class GetPostsUseCase(private val repository: PostRepository) {
    operator fun invoke(token: String?): Flow<Result<PostListResponse>> {
        return repository.getPosts(token)
    }
}
