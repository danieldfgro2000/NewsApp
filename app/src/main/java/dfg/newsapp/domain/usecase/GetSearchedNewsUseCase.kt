package dfg.newsapp.domain.usecase

import dfg.newsapp.data.model.APIResponse
import dfg.newsapp.data.util.Resource
import dfg.newsapp.domain.repository.NewsRepository

class GetSearchedNewsUseCase (private val newsRepository: NewsRepository) {

    suspend fun execute(searchQuery: String): Resource<APIResponse> {
        return newsRepository.getSearchedNews(searchQuery)
    }
}