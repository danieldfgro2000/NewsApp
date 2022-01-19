package dfg.newsapp.data.repository.dataSourceImpl

import dfg.newsapp.data.db.ArticleDao
import dfg.newsapp.data.model.Article
import dfg.newsapp.data.repository.dataSource.NewsLocalDataSource
import kotlinx.coroutines.flow.Flow

class NewsLocalDataSourceImpl (
    private val articleDao: ArticleDao
) : NewsLocalDataSource {
    override suspend fun saveArticleToDB(article: Article) {
        articleDao.insert(article)
    }

    override fun getSavedArticles(): Flow<List<Article>> {
        return articleDao.getAllArticles()
    }

    override suspend fun deleteArticleFromDB(article: Article) {
        articleDao.deleteArticle(article)
    }
}