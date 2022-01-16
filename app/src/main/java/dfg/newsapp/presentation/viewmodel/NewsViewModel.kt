package dfg.newsapp.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dfg.newsapp.data.model.APIResponse
import dfg.newsapp.data.model.Article
import dfg.newsapp.data.util.Resource
import dfg.newsapp.domain.usecase.GetNewsHeadlinesUseCase
import dfg.newsapp.domain.usecase.GetSearchedNewsUseCase
import dfg.newsapp.domain.usecase.SaveNewsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.http.Query

class NewsViewModel (
    private val app: Application,
    private val getNewsHeadlinesUseCase: GetNewsHeadlinesUseCase,
    private val getSearchedNewsUseCase: GetSearchedNewsUseCase,
    private val saveNewsUseCase: SaveNewsUseCase
): AndroidViewModel(app) {
    val newsHeadLines: MutableLiveData<Resource<APIResponse>> = MutableLiveData()

    fun getNewsHeadLines(country: String, page: Int) = viewModelScope.launch(Dispatchers.IO) {
        newsHeadLines.postValue(Resource.Loading())

        try {
            if (isNetworkAvailable(app)) {
                val apiResult = getNewsHeadlinesUseCase.execute(country, page)
                newsHeadLines.postValue(apiResult)
            } else {
                newsHeadLines.postValue(Resource.Error("Internet is not available"))
            }
        } catch (e: Exception) {
            newsHeadLines.postValue(Resource.Error(e.message.toString()))
        }
    }

    private fun isNetworkAvailable(context: Context?) : Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null)
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            } else {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                if(activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                    return true
                }
            }
        return false
        }

    val searchedNews: MutableLiveData<Resource<APIResponse>> = MutableLiveData()

    fun searchNews (country: String, searchQuery: String, page: Int) = viewModelScope.launch {
        searchedNews.postValue(Resource.Loading())

        try {
            if (isNetworkAvailable(app)) {
                val response = getSearchedNewsUseCase.execute(
                    country,
                    searchQuery,
                    page
                )
                searchedNews.postValue(response)
            } else {
                searchedNews.postValue(Resource.Error("No iternet connection"))
            }
        } catch (e: java.lang.Exception) {
            searchedNews.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun saveArticleToLocalDB(article: Article) = viewModelScope.launch {
        saveNewsUseCase.execute(article)
    }
}