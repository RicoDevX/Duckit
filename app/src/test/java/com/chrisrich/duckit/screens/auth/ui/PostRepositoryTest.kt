package com.chrisrich.duckit.screens.auth.ui

import app.cash.turbine.test
import com.chrisrich.duckit.data.remote.DuckitApi
import com.chrisrich.duckit.data.repository.PostRepository
import com.chrisrich.duckit.domain.model.NewPostRequest
import com.chrisrich.duckit.domain.model.Post
import com.chrisrich.duckit.domain.model.PostListResponse
import com.chrisrich.duckit.domain.model.PostResponse
import com.chrisrich.duckit.domain.model.VoteResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class PostRepositoryTest {

    private lateinit var repository: PostRepository
    private val mockApiService: DuckitApi = mock(DuckitApi::class.java)

    @Before
    fun setup() {
        repository = PostRepository(mockApiService)
    }

    @Test
    fun `getPosts should return unique posts based on image`() = runTest {
        // Mock API response with duplicate images
        val post1 = Post("1", "Headline 1", "image1.png", 10, "Author1")
        val post2 = Post("2", "Headline 2", "image2.png", 5, "Author2")
        val post3 = Post("3", "Headline 3", "image1.png", 8, "Author3") // Duplicate image

        val apiResponse = PostListResponse(listOf(post1, post2, post3))
        whenever(mockApiService.getPosts(null)).thenReturn(apiResponse)

        // Test Flow emission
        repository.getPosts(null).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)

            val uniquePosts = result.getOrThrow().posts
            assertEquals(2, uniquePosts.size) // Should only contain 2 unique posts
            assertEquals(listOf(post1, post2), uniquePosts) // Ensure correct unique posts

            awaitComplete()
        }
    }

    @Test
    fun `getPosts should return failure on API exception`() = runTest {
        val exception = RuntimeException("Network error")
        whenever(mockApiService.getPosts(null)).thenThrow(exception)

        repository.getPosts(null).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())

            awaitComplete()
        }
    }

    @Test
    fun `upvote should return success response`() = runTest {
        val postId = "1"
        val token = "token123"
        val voteResponse = VoteResponse(12)
        whenever(mockApiService.upvote(postId, token)).thenReturn(voteResponse)

        repository.upvote(postId, token).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(voteResponse, result.getOrThrow())

            awaitComplete()
        }
    }

    @Test
    fun `upvote should return failure on API exception`() = runTest {
        val postId = "1"
        val token = "token123"
        val exception = RuntimeException("Upvote failed")
        whenever(mockApiService.upvote(postId, token)).thenThrow(exception)

        repository.upvote(postId, token).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())

            awaitComplete()
        }
    }

    @Test
    fun `downvote should return success response`() = runTest {
        val postId = "1"
        val token = "token123"
        val voteResponse = VoteResponse(12)
        whenever(mockApiService.downvote(postId, token)).thenReturn(voteResponse)

        repository.downvote(postId, token).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(voteResponse, result.getOrThrow())

            awaitComplete()
        }
    }

    @Test
    fun `downvote should return failure on API exception`() = runTest {
        val postId = "1"
        val token = "token123"
        val exception = RuntimeException("Downvote failed")
        whenever(mockApiService.downvote(postId, token)).thenThrow(exception)

        repository.downvote(postId, token).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())

            awaitComplete()
        }
    }

    @Test
    fun `newPost should return success response`() = runTest {
        val token = "token123"
        val request = NewPostRequest("Title", "image.png")
        val postResponse = PostResponse(listOf(Post("1", "Title", "image.png", 0, "Author1")))
        whenever(mockApiService.newPost(token, request)).thenReturn(postResponse)

        repository.newPost(token, request).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(postResponse, result.getOrThrow())

            awaitComplete()
        }
    }

    @Test
    fun `newPost should return failure on API exception`() = runTest {
        val token = "token123"
        val request = NewPostRequest("Title", "image.png")
        val exception = RuntimeException("Post creation failed")
        whenever(mockApiService.newPost(token, request)).thenThrow(exception)

        repository.newPost(token, request).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())

            awaitComplete()
        }
    }
}
