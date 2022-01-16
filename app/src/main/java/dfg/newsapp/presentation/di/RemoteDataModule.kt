package dfg.newsapp.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dfg.newsapp.data.api.NewsApiService
import dfg.newsapp.data.repository.dataSource.NewsRemoteDataSource
import dfg.newsapp.data.repository.dataSourceImpl.NewsRemoteDataSourceImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RemoteDataModule {

    @Singleton
    @Provides
    fun provideNewsRemoteDataSource(newsApiService: NewsApiService) : NewsRemoteDataSource {
        return NewsRemoteDataSourceImpl(newsApiService)
    }
}