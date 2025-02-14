package com.chrisrich.duckit.domain.model

import com.google.gson.annotations.SerializedName

data class VoteResponse(@SerializedName("upvotes") val votes: Int)