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
import com.chrisrich.duckit.domain.usecase.newpost.NewPostUseCase
import com.chrisrich.duckit.domain.usecase.postgallery.DownvotePostUseCase
import com.chrisrich.duckit.domain.usecase.postgallery.GetPostsUseCase
import com.chrisrich.duckit.domain.usecase.postgallery.UpvotePostUseCase
import com.chrisrich.duckit.navigation.NavigationManager
import com.chrisrich.duckit.ui.screens.auth.AuthViewModel
import com.chrisrich.duckit.ui.screens.newpost.NewPostViewModel
import com.chrisrich.duckit.ui.screens.postgallery.PostGalleryViewModel
import com.chrisrich.duckit.utils.DefaultEmailValidator
import com.chrisrich.duckit.utils.EmailValidator
import com.chrisrich.duckit.utils.SessionManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val duckItModule = module {

    //Provide Secure SessionManager
    single { SessionManager(androidContext()) }

    // Provides Navigation Manager
    single { NavigationManager() }

    // Provide EmailValidator
    single<EmailValidator> { DefaultEmailValidator }

    // Provide Retrofit instance
    single { provideHttpClient() }
    single { provideConverterFactory() }
    single { provideRetrofit(get(), get()) }
    single { provideService(get()) }

    // Provide DuckitApi
    single { provideDuckitApi(get()) }

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
    viewModel { PostGalleryViewModel(get(), get(), get(), get(), get()) }
    viewModel { AuthViewModel(get(), get(), get(), get(), get()) }
    viewModel { NewPostViewModel(get(), get(), get()) }
}
