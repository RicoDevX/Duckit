package com.chrisrich.duckit.data.repository

import com.chrisrich.duckit.data.remote.DuckitApi
import com.chrisrich.duckit.domain.model.NewPostRequest
import com.chrisrich.duckit.domain.model.PostListResponse
import com.chrisrich.duckit.domain.model.PostResponse
import com.chrisrich.duckit.domain.model.VoteResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class PostRepository(private val apiService: DuckitApi) {

    fun getPosts(token: String?): Flow<Result<PostListResponse>> = flow {
        emit(Result.success(apiService.getPosts(token)))
    }.catch { e -> emit(Result.failure(e)) }

    fun upvote(postId: String, token: String): Flow<Result<VoteResponse>> = flow {
        emit(Result.success(apiService.upvote(postId, token)))
    }.catch { e -> emit(Result.failure(e)) }

    fun downvote(postId: String, token: String): Flow<Result<VoteResponse>> = flow {
        emit(Result.success(apiService.downvote(postId, token)))
    }.catch { e -> emit(Result.failure(e)) }

    fun newPost(token: String, request: NewPostRequest): Flow<Result<PostResponse>> = flow {
        emit(Result.success(apiService.newPost(token, request)))
    }.catch { e -> emit(Result.failure(e)) }
}
