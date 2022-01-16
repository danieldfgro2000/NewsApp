package dfg.newsapp.data.repository.dataSourceImpl

import dfg.newsapp.data.api.NewsApiService
import dfg.newsapp.data.model.APIResponse
import dfg.newsapp.data.repository.dataSource.NewsRemoteDataSource
import retrofit2.Response

class NewsRemoteDataSourceImpl(
    private val newsApiService: NewsApiService
) : NewsRemoteDataSource {
    override suspend fun getTopHeadlines(country: String, page: Int): Response<APIResponse> {
        return newsApiService.getTopHeadlines(country, page)
    }

    override suspend fun getSearchedNews(
        country: String,
        searchQuery: String,
        page: Int
    ): Response<APIResponse> {
        return newsApiService.getSearchedTopHeadlines(country, searchQuery, page)
    }
}