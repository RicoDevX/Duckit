package com.chrisrich.duckit.data.repository

import com.chrisrich.duckit.data.remote.DuckitApi
import com.chrisrich.duckit.domain.model.NewPostRequest
import com.chrisrich.duckit.domain.model.PostListResponse
import com.chrisrich.duckit.domain.model.PostResponse
import com.chrisrich.duckit.domain.model.VoteResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * Repository class responsible for handling post-related operations.
 *
 * This class interacts with the API service to fetch posts, upvote/downvote posts,
 * and create new posts. It leverages Kotlin's Flow API for asynchronous data handling
 * and ensures proper error handling using `.catch()`.
 *
 * @property apiService The API service responsible for network requests.
 */
class PostRepository(private val apiService: DuckitApi) {

    /**
     * Fetches a list of posts from the API.
     *
     * - Removes duplicate posts based on the `image` field.
     * - Returns the result wrapped in a [Flow] of [Result] for proper error handling.
     *
     * @param token The authentication token for API access.
     * @return A [Flow] emitting either a successful [PostListResponse] or an error.
     */
    fun getPosts(token: String?): Flow<Result<PostListResponse>> = flow {
        val response = apiService.getPosts(token)
        val uniquePosts = response.posts.distinctBy { it.image }
        emit(Result.success(PostListResponse(uniquePosts)))
    }.catch { e -> emit(Result.failure(e)) }

    /**
     * Sends an upvote request for a specific post.
     *
     * - Calls the API to upvote a post.
     * - Returns the result wrapped in a [Flow] of [Result] for proper error handling.
     *
     * @param postId The ID of the post to be upvoted.
     * @param token The authentication token for API access.
     * @return A [Flow] emitting either a successful [VoteResponse] or an error.
     */
    fun upvote(postId: String, token: String): Flow<Result<VoteResponse>> = flow {
        emit(Result.success(apiService.upvote(postId, token)))
    }.catch { e -> emit(Result.failure(e)) }

    /**
     * Sends a downvote request for a specific post.
     *
     * - Calls the API to downvote a post.
     * - Returns the result wrapped in a [Flow] of [Result] for proper error handling.
     *
     * @param postId The ID of the post to be downvoted.
     * @param token The authentication token for API access.
     * @return A [Flow] emitting either a successful [VoteResponse] or an error.
     */
    fun downvote(postId: String, token: String): Flow<Result<VoteResponse>> = flow {
        emit(Result.success(apiService.downvote(postId, token)))
    }.catch { e -> emit(Result.failure(e)) }

    /**
     * Submits a new post to the API.
     *
     * - Calls the API to create a new post.
     * - Returns the result wrapped in a [Flow] of [Result] for proper error handling.
     *
     * @param token The authentication token for API access.
     * @param request The request object containing new post details.
     * @return A [Flow] emitting either a successful [PostResponse] or an error.
     */
    fun newPost(token: String, request: NewPostRequest): Flow<Result<PostResponse>> = flow {
        emit(Result.success(apiService.newPost(token, request)))
    }.catch { e -> emit(Result.failure(e)) }
}
