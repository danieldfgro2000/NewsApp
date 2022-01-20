package dfg.newsapp.domain.repository

import androidx.lifecycle.LiveData
import dfg.newsapp.data.model.APIResponse
import dfg.newsapp.data.model.Article
import dfg.newsapp.data.util.Resource
import kotlinx.coroutines.flow.Flow


interface NewsRepository {

    suspend fun getNewsHeadlines(country: String, category: String,  page: Int): Resource<APIResponse>

    suspend fun getSearchedNews(country: String, searchQuery: String, page: Int) : Resource<APIResponse>

    suspend fun saveNews(article: Article)

    suspend fun deleteNews(article: Article)

    fun getSavedNews() : Flow<List<Article>>
}