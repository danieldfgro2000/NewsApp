package dfg.newsapp.domain.usecase

import dfg.newsapp.data.model.Article
import dfg.newsapp.domain.repository.NewsRepository

class SaveNewsUseCase(private val newsRepository: NewsRepository) {

    suspend fun execute(article: Article) = newsRepository.saveNews(article)
}