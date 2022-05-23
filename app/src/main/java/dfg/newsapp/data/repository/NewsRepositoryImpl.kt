package dfg.newsapp.data.repository

import dfg.newsapp.data.model.APIResponse
import dfg.newsapp.data.model.Article
import dfg.newsapp.data.repository.dataSource.NewsLocalDataSource
import dfg.newsapp.data.repository.dataSource.NewsRemoteDataSource
import dfg.newsapp.data.util.Resource
import dfg.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class NewsRepositoryImpl(
    private val newsRemoteDataSource: NewsRemoteDataSource,
    private val newsLocalDataSource: NewsLocalDataSource
): NewsRepository {
    override suspend fun getNewsHeadlines(country: String?, category: String?, page: Int): Resource<APIResponse> {
        return responseToResource(newsRemoteDataSource.getTopHeadlines(country, category,  page))
    }

    override suspend fun getSearchedNews(
        country: String,
        searchQuery: String,
        page: Int
    ): Resource<APIResponse> {
        return responseToResource(
            newsRemoteDataSource.getSearchedNews(country, searchQuery, page)
        )
    }



    override suspend fun saveNews(article: Article) {
        newsLocalDataSource.saveArticleToDB(article)
    }

    override suspend fun deleteNews(article: Article) {
        newsLocalDataSource.deleteArticleFromDB(article)
    }

    override fun getSavedNews(): Flow<List<Article>> {
         return  newsLocalDataSource.getSavedArticles()
    }

    private fun responseToResource(response: Response<APIResponse>) : Resource<APIResponse> {
        if (response.isSuccessful){
            response.body()?.let {  result ->
                return Resource.Success(result)
            }
        }
        return Resource.Error(response.message())
    }
}