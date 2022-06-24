package dfg.newsapp.data.api

import dfg.newsapp.BuildConfig
import dfg.newsapp.data.model.APIResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country")
        country: String?,
        @Query("category")
        category: String?,
        @Query("pageSize")
        pageSize: Int = 100,
        @Query("page")
        page: Int,
        @Query("apiKey")
        apiKey: String = BuildConfig.API_KEY
    ) : Response<APIResponse>

    @GET("v2/everything")
    suspend fun getSearchedTopHeadlines(
//        @Query("country")
//        country: String,
        @Query("q")
        searchQuery: String?,
        @Query("apiKey")
        apiKey: String = BuildConfig.API_KEY
    ) : Response<APIResponse>
}