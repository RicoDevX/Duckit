package com.chrisrich.duckit

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DuckitRepo(private val apiService: ApiService) {

    fun getPosts(): Flow<Result<PostListResponse>> = flow {
        try {
            val getPostResponse = apiService.getPosts()
            Log.d("PostRepository", "Fetched posts: ${getPostResponse.posts.size}")
            emit(Result.success(getPostResponse))
        } catch (e: Exception) {
            Log.e("PostRepository", "Error fetching posts", e)
            emit(Result.failure(e))
        }
    }

    fun logIn(email: String, password: String): Flow<Result<AuthResponse>> = flow {
        try {
            val response = apiService.signIn(AuthRequest(email, password))
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun signUp(email: String, password: String): Flow<Result<AuthResponse>> = flow {
        try {
            val response = apiService.signUp(AuthRequest(email, password))
            Log.d("PostRepository", "Sign Up Success : Token ${response.token}")
            emit(Result.success(response))
        } catch (e: Exception) {
            Log.d("PostRepository", "Error Signing up", e)
            emit(Result.failure(e))
        }
    }

    fun upvote(postId: String, token: String): Flow<Result<UpvoteResponse>> = flow {
        try {
            val response = apiService.upvote(postId, token)
            Log.d("PostRepository", "UpVote Success : Number ${response.upvotes}")
            emit(Result.success(response))
        } catch (e: Exception) {
            Log.d("PostRepository", "Error upvoting", e)
            emit(Result.failure(e))
        }
    }

    fun downvote(postId: String, token: String): Flow<Result<UpvoteResponse>> = flow {
        try {
            val response = apiService.downvote(postId, token)
            Log.d("PostRepository", "downvote Success : Number ${response.upvotes}")
            emit(Result.success(response))
        } catch (e: Exception) {
            Log.d("PostRepository", "Error downvoting", e)
            emit(Result.failure(e))
        }
    }

    fun newPost(token: String, headline: String, image: String): Flow<Result<PostResponse>> = flow {
        try {
            val response = apiService.newPost(token, NewPostRequest(headline, image))
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}