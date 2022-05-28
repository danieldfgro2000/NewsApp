package dfg.newsapp.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dfg.newsapp.data.model.APIResponse
import dfg.newsapp.data.model.Article
import dfg.newsapp.data.util.Resource
import dfg.newsapp.domain.usecase.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber.Forest.e
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val app: Application,
    private val getNewsHeadlinesUseCase: GetNewsHeadlinesUseCase,
    private val getSearchedNewsUseCase: GetSearchedNewsUseCase,
    private val saveNewsUseCase: SaveNewsUseCase,
    private val getSavedNewsUseCase: GetSavedNewsUseCase,
    private val deleteSavedNewsUseCase: DeleteSavedNewsUseCase
) : AndroidViewModel(app) {

    val previousCountry = MutableLiveData<String>()
    val selectedCountry = MutableLiveData<String>()
    val previousCategory = MutableLiveData<String>()
    val selectedCategory = MutableLiveData<String>()

    val newsHeadLines: MutableLiveData<Resource<APIResponse>> = MutableLiveData()
    fun getNewsHeadLines(
        country: String?,
        category: String?,
        page: Int
    ) = viewModelScope.launch(IO) {
        e("getting headlines, page = $page")
        newsHeadLines.postValue(Resource.Loading())
        try {
            if (isNetworkAvailable(app)) {
                val apiResult = getNewsHeadlinesUseCase.execute(country, category, page)
                newsHeadLines.postValue(apiResult)
            } else {
                newsHeadLines.postValue(Resource.Error("Internet is not available"))
            }
        } catch (e: Exception) {
            newsHeadLines.postValue(Resource.Error(e.message.toString()))
        }
    }

    val searchedQuery = MutableLiveData<String>()
    val previousSearchedQuery = MutableLiveData<String>()
    val searchedNews: MutableLiveData<Resource<APIResponse>> = MutableLiveData()
    val previousSearchedNews: MutableLiveData<Resource<APIResponse>> = MutableLiveData()


    var notTyping = MutableLiveData<Boolean>()
    fun counterSetup() {
        var counter = 0
        val coolDown = 10
        notTyping.value = false
        viewModelScope.launch(IO) {
            while (counter < coolDown) {
                delay(200)
                counter++
                e("counter = ${counter}")
            }
            e("out while")
            viewModelScope.launch(Main) {
                notTyping.value = true
            }

        }
        e("typing = ${notTyping.value}")
    }


    fun searchNews() = viewModelScope.launch(IO) {
        searchedNews.postValue(Resource.Loading())
        try {
            if (isNetworkAvailable(app)) {
                if (searchedQuery.value!!.length >= 5){
                    e("Getting searched news")
                    val response = getSearchedNewsUseCase.execute(searchedQuery.value)
                    searchedNews.postValue(response)
                }
            } else {
                searchedNews.postValue(Resource.Error("No iternet connection"))
            }
        } catch (e: java.lang.Exception) {
            searchedNews.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun saveNewsToLocalDB(article: Article) = viewModelScope.launch {
        saveNewsUseCase.execute(article)
    }

    fun getSavedNews() = liveData {
        getSavedNewsUseCase.execute().collect {
            emit(it)
        }
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        deleteSavedNewsUseCase.execute(article)
    }

    private fun isNetworkAvailable(context: Context?): Boolean {
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
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }
}