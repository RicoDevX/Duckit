package com.chrisrich.duckit.data.remote

import com.chrisrich.duckit.domain.model.AuthRequest
import com.chrisrich.duckit.domain.model.AuthResponse
import com.chrisrich.duckit.domain.model.NewPostRequest
import com.chrisrich.duckit.domain.model.PostListResponse
import com.chrisrich.duckit.domain.model.PostResponse
import com.chrisrich.duckit.domain.model.VoteResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface DuckitApi {
    @POST("signin")
    suspend fun signIn(@Body request: AuthRequest): AuthResponse

    @POST("signup")
    suspend fun signUp(@Body request: AuthRequest): AuthResponse

    @GET("posts")
    suspend fun getPosts(@Header("Authorization") token: String? = null): PostListResponse

    @POST("posts/{id}/upvote")
    suspend fun upvote(@Path("id") postId: String, @Header("Authorization") token: String): VoteResponse

    @POST("posts/{id}/downvote")
    suspend fun downvote(@Path("id") postId: String, @Header("Authorization") token: String): VoteResponse

    @POST("posts")
    suspend fun newPost(@Header("Authorization") token: String, @Body request: NewPostRequest): PostResponse
}