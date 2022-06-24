package dfg.newsapp.data.repository.dataSourceImpl

import dfg.newsapp.data.api.NewsApiService
import dfg.newsapp.data.model.APIResponse
import dfg.newsapp.data.repository.dataSource.NewsRemoteDataSource
import retrofit2.Response
import timber.log.Timber.Forest.e

class NewsRemoteDataSourceImpl(
    private val newsApiService: NewsApiService
) : NewsRemoteDataSource {
    override suspend fun getTopHeadlines(country: String?, category: String?, page: Int): Response<APIResponse> {
        return newsApiService.getTopHeadlines(country, category, page = page)
    }

    override suspend fun getSearchedNews(searchQuery: String?): Response<APIResponse> {
        return newsApiService.getSearchedTopHeadlines(searchQuery = searchQuery)
    }
}