package dfg.newsapp.domain.usecase

import dfg.newsapp.data.model.Article
import dfg.newsapp.domain.repository.NewsRepository

class DeleteSavedNewsUseCase(private val newsRepository: NewsRepository) {
    suspend fun execute(article: Article) = newsRepository.deleteNews(article)
}