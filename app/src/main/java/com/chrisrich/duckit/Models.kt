package com.chrisrich.duckit

import com.google.gson.annotations.SerializedName

data class AuthRequest(val email: String, val password: String)
data class AuthResponse(val token: String)
data class PostListResponse(@SerializedName("Posts") val posts: List<Post>)
data class UpvoteResponse(val upvotes: Int)
data class NewPostRequest(val headline: String, val image: String)
data class PostResponse(val Posts: List<Post>)
data class Post(val id: String, val headline: String, val image: String, val upvotes: Int, val author: String)