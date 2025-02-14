package com.chrisrich.duckit.di

import com.chrisrich.duckit.data.remote.provideConverterFactory
import com.chrisrich.duckit.data.remote.provideDuckitApi
import com.chrisrich.duckit.data.remote.provideHttpClient
import com.chrisrich.duckit.data.remote.provideRetrofit
import com.chrisrich.duckit.data.remote.provideService
import com.chrisrich.duckit.data.repository.AuthRepository
import com.chrisrich.duckit.data.repository.PostRepository
import com.chrisrich.duckit.domain.usecase.auth.LogInUseCase
import com.chrisrich.duckit.domain.usecase.auth.SignUpUseCase
import com.chrisrich.duckit.domain.usecase.post.NewPostUseCase
import com.chrisrich.duckit.domain.usecase.postlist.DownvotePostUseCase
import com.chrisrich.duckit.domain.usecase.postlist.GetPostsUseCase
import com.chrisrich.duckit.domain.usecase.postlist.UpvotePostUseCase
import com.chrisrich.duckit.navigation.NavigationManager
import com.chrisrich.duckit.ui.screens.auth.AuthViewModel
import com.chrisrich.duckit.ui.screens.postlist.PostListViewModel
import com.chrisrich.duckit.utils.SessionManager
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val duckItModule = module {
    // Provides Navigation Manager
    single { NavigationManager() }

    // Provide Retrofit instance
    single { provideHttpClient() }
    single { provideConverterFactory() }
    single { provideRetrofit(get(), get()) }
    single { provideService(get()) }

    // Provide DuckitApi
    single { provideDuckitApi(get()) } // Pass the Retrofit instance

    // Provide SessionManager
    single { SessionManager(get()) }

    // Provide Repositories
    single { PostRepository(get()) }
    single { AuthRepository(get()) }

    // Auth UseCases
    single { LogInUseCase(get()) }
    single { SignUpUseCase(get()) }

    // Post UseCases
    single { GetPostsUseCase(get()) }
    single { UpvotePostUseCase(get()) }
    single { DownvotePostUseCase(get()) }
    single { NewPostUseCase(get()) }

    // Provide ViewModels
    viewModel { PostListViewModel(get(), get(),get(), get()) }
    viewModel { AuthViewModel(get(), get(), get()) }
}
