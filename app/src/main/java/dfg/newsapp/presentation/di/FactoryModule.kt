package dfg.newsapp.presentation.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dfg.newsapp.domain.usecase.GetNewsHeadlinesUseCase
import dfg.newsapp.domain.usecase.GetSearchedNewsUseCase
import dfg.newsapp.domain.usecase.SaveNewsUseCase
import dfg.newsapp.presentation.viewmodel.NewsViewModelFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FactoryModule {

    @Singleton
    @Provides
    fun provideNewsViewModelFactory (
        application: Application,
        getNewsHeadlinesUseCase: GetNewsHeadlinesUseCase,
        getSearchedNewsUseCase: GetSearchedNewsUseCase,
        saveNewsUseCase: SaveNewsUseCase
    ) : NewsViewModelFactory {
        return  NewsViewModelFactory(
            application,
            getNewsHeadlinesUseCase,
            getSearchedNewsUseCase,
            saveNewsUseCase
        )
    }
}