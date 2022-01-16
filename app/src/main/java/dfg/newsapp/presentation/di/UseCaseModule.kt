package dfg.newsapp.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dfg.newsapp.domain.repository.NewsRepository
import dfg.newsapp.domain.usecase.GetNewsHeadlinesUseCase
import dfg.newsapp.domain.usecase.GetSearchedNewsUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Singleton
    @Provides
    fun provideNewsHeadlinesUseCase ( newsRepository: NewsRepository) : GetNewsHeadlinesUseCase {
        return GetNewsHeadlinesUseCase(newsRepository)
    }

    @Singleton
    @Provides
    fun provideGetSearchedNewsUseCase ( newsRepository: NewsRepository) : GetSearchedNewsUseCase {
        return GetSearchedNewsUseCase(newsRepository)
    }
}