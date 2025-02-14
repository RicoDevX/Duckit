package com.chrisrich.duckit.domain.usecase.postlist

import com.chrisrich.duckit.data.repository.PostRepository
import com.chrisrich.duckit.domain.model.VoteResponse
import kotlinx.coroutines.flow.Flow

class DownvotePostUseCase(private val repository: PostRepository) {
    operator fun invoke(postId: String, token: String): Flow<Result<VoteResponse>> {
        return repository.downvote(postId, token)
    }
}
