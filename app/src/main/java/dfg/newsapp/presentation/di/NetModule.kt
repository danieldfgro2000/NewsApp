package dfg.newsapp.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dfg.newsapp.BuildConfig
import dfg.newsapp.data.api.NewsApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber.Forest.e

@Module
@InstallIn(SingletonComponent::class)
class NetModule {

    private fun provideClientBuilder() : OkHttpClient {
        val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        clientBuilder.addInterceptor(Interceptor { chain ->
            val request = chain.request()
//            e(request.toString())
            chain.proceed(request)
        })
        return clientBuilder.build()
    }


    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.BASE_URL)
            .client(provideClientBuilder())
            .build()
    }

    @Provides
    fun provideNewsAPIService(retrofit: Retrofit) : NewsApiService {
        return retrofit.create(NewsApiService::class.java)
    }
}