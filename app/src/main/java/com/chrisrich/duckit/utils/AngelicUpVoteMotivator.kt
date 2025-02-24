package com.chrisrich.duckit.utils

class AngelicUpVoteMotivator {

    fun generateUpVoteMessage(postHeadline: String, currentVotes: Int): String {
        val noVotesMessages = listOf(
            "$postHeadline has zero votes. It's waiting for someone to believe in it. Be the first to make a difference.",
            "$postHeadline is fresh and untouched. Your vote could be the spark that sets it on fire.",
            "No votes yet for $postHeadline, but I believe in you. One click could be the start of something big.",
            "$postHeadline has zero votes, but you have the power to change that. Lift it up and make an impact.",
            "Every great journey starts with one step. Or in this case, one vote. Be the pioneer who gets $postHeadline started."
        )

        val lowVotesMessages = listOf(
            "$postHeadline has $currentVotes votes, but it’s just getting started. Help it reach new heights.",
            "$postHeadline has $currentVotes votes. A little more support and it could become something great.",
            "Your vote has more power than you realize. Help $postHeadline grow by giving it the push it needs.",
            "$postHeadline has $currentVotes votes, but it deserves more. Show it some support.",
            "Think of your upvote as a small act of kindness. Lift $postHeadline up and watch it shine."
        )

        val midVotesMessages = listOf(
            "$postHeadline is gaining momentum with $currentVotes votes. Keep it going and help it reach the next level.",
            "$postHeadline is starting to get noticed with $currentVotes votes. Your vote could be the one that tips the scale.",
            "$postHeadline is picking up steam with $currentVotes votes. Don’t stop now.",
            "$postHeadline is gaining recognition with $currentVotes votes. Be part of something that’s growing.",
            "Every vote counts. Giving $postHeadline an upvote makes sure it gets the attention it deserves."
        )

        val highVotesMessages = listOf(
            "$postHeadline is catching fire with $currentVotes votes. But why stop now?",
            "$postHeadline has $currentVotes votes and it’s not slowing down. Your vote keeps the momentum going.",
            "$postHeadline has $currentVotes votes and it’s turning into something big. Keep it going.",
            "With $currentVotes votes, $postHeadline is already on the rise. Help take it even higher.",
            "$postHeadline has $currentVotes votes. Your vote makes it stronger."
        )

        return when {
            currentVotes == 0 -> noVotesMessages.random()
            currentVotes in 1..2 -> lowVotesMessages.random()
            currentVotes in 3..5 -> midVotesMessages.random()
            currentVotes > 5 -> highVotesMessages.random()
            else -> "Every upvote makes a difference. Support this post and keep the positivity going."
        }
    }
}
