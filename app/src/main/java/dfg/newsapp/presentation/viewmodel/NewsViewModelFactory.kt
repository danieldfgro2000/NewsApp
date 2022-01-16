package dfg.newsapp.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dfg.newsapp.domain.usecase.GetNewsHeadlinesUseCase
import dfg.newsapp.domain.usecase.GetSearchedNewsUseCase
import dfg.newsapp.domain.usecase.SaveNewsUseCase

class NewsViewModelFactory (
    private val app: Application,
    private val getNewsHeadlinesUseCase: GetNewsHeadlinesUseCase,
    private val getSearchedNewsUseCase: GetSearchedNewsUseCase,
    private val saveNewsUseCase: SaveNewsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel (
            app,
            getNewsHeadlinesUseCase,
            getSearchedNewsUseCase,
            saveNewsUseCase
        ) as T
    }
}