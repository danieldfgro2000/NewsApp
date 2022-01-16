package dfg.newsapp.domain.usecase

import dfg.newsapp.data.model.Article
import dfg.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow


class GetSavedNewsUseCase (private val newsRepository: NewsRepository) {

    fun execute(): Flow<List<Article>> {
        return newsRepository.getSavedNews()
    }
}