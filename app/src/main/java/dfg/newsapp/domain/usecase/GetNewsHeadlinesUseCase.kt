package dfg.newsapp.domain.usecase

import dfg.newsapp.data.model.APIResponse
import dfg.newsapp.data.util.Resource
import dfg.newsapp.domain.repository.NewsRepository

class GetNewsHeadlinesUseCase(private val newsRepository: NewsRepository) {

    suspend fun execute(): Resource<APIResponse> {
        return newsRepository.getNewsHeadlines()
    }
}