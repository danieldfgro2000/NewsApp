package dfg.newsapp.presentation.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dfg.newsapp.data.db.ArticleDao
import dfg.newsapp.data.db.ArticleDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideNewsDatabase(app: Application) : ArticleDatabase {
        return Room.databaseBuilder(app, ArticleDatabase::class.java, "news_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideNewsDao(articleDatabase: ArticleDatabase) : ArticleDao {
        return articleDatabase.getArticleDao()
    }
}