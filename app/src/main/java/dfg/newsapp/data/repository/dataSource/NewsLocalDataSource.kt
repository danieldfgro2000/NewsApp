package dfg.newsapp.data.repository.dataSource

import dfg.newsapp.data.model.Article

interface NewsLocalDataSource {
    suspend fun saveArticleToDB(article: Article)
}