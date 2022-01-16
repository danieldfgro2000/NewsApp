package dfg.newsapp.data.repository.dataSource

import dfg.newsapp.data.model.APIResponse
import retrofit2.Response

interface NewsRemoteDataSource {

    suspend fun getTopHeadlines(country: String, page: Int) : Response<APIResponse>

}