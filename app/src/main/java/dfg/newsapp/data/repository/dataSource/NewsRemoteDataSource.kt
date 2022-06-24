package dfg.newsapp.data.repository.dataSource

import dfg.newsapp.data.model.APIResponse
import retrofit2.Response

interface NewsRemoteDataSource {

    suspend fun getTopHeadlines(country: String?, category: String?, page: Int) : Response<APIResponse>

    suspend fun getSearchedNews(searchQuery: String?) : Response<APIResponse>

}