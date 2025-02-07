package com.chrisrich.duckit

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiService {
    @POST("signin")
    suspend fun signIn(@Body request: AuthRequest): AuthResponse

    @POST("signup")
    suspend fun signUp(@Body request: AuthRequest): AuthResponse

    @GET("posts")
    suspend fun getPosts(@Header("Authorization") token: String? = null): PostListResponse

    @POST("posts/{id}/upvote")
    suspend fun upvote(@Path("id") postId: String, @Header("Authorization") token: String): UpvoteResponse

    @POST("posts/{id}/downvote")
    suspend fun downvote(@Path("id") postId: String, @Header("Authorization") token: String): UpvoteResponse

    @POST("posts")
    suspend fun newPost(@Header("Authorization") token: String, @Body request: NewPostRequest): PostResponse
}