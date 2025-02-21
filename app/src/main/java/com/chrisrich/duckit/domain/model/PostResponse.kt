package com.chrisrich.duckit.domain.model

import com.google.gson.annotations.SerializedName

data class PostResponse(@SerializedName("Posts") val posts: List<Post>)