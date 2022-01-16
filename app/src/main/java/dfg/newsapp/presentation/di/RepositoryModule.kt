package dfg.newsapp.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dfg.newsapp.data.repository.NewsRepositoryImpl
import dfg.newsapp.data.repository.dataSource.NewsRemoteDataSource
import dfg.newsapp.domain.repository.NewsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideNewsRepository (
        newsRemoteDataSource: NewsRemoteDataSource
    ) : NewsRepository {
        return NewsRepositoryImpl(newsRemoteDataSource)
    }
}