package dfg.newsapp.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dfg.newsapp.presentation.adapter.NewsAdapter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AdapterModule {

    @Provides
    fun provideNewsAdapter(): NewsAdapter {
        return NewsAdapter()
    }
}