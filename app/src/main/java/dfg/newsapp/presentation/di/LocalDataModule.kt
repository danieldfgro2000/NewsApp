package dfg.newsapp.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dfg.newsapp.data.db.ArticleDao
import dfg.newsapp.data.repository.dataSource.NewsLocalDataSource
import dfg.newsapp.data.repository.dataSourceImpl.NewsLocalDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalDataModule {

    @Singleton
    @Provides
    fun prodLocalDataSource( articleDao: ArticleDao) : NewsLocalDataSource {
        return NewsLocalDataSourceImpl(articleDao)
    }
}