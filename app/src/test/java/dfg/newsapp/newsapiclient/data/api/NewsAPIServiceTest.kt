package dfg.newsapp.newsapiclient.data.api

import com.google.common.truth.Truth.assertThat
import dfg.newsapp.data.api.NewsApiService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.Charset

class NewsAPIServiceTest {
    private lateinit var service: NewsApiService
    private lateinit var server: MockWebServer

   @Before
    fun setUp(){
        server = MockWebServer()
        service = Retrofit.Builder()
           .baseUrl(server.url(""))
           .addConverterFactory(GsonConverterFactory.create())
           .build()
           .create(NewsApiService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    private fun enqueueMockResponse(
        fileName: String
    ) {
        val inputStream = javaClass.classLoader!!.getResourceAsStream(fileName)
        val source = inputStream.source().buffer()
        val mockResponse = MockResponse()
        mockResponse.setBody(source.readString(Charsets.UTF_8))
        server.enqueue(mockResponse)
    }

    @Test
    fun getTopHeadlines_sendRequest_receivedExpected() {
        runBlocking {
            enqueueMockResponse("newsresponse.json")
            val responseBody = service.getTopHeadlines(country = "us", 1).body()
            val request = server.takeRequest()
            assertThat(responseBody).isNotNull()
            assertThat(request.path).isEqualTo("/v2/top-headlines?country=us&page=1&apiKey=a61b4edaa29f488bbd940433f76df38f")
        }
    }

    @Test
    fun getTopHeadlines_receivedResponse_correctPageSize() {
        runBlocking {
            enqueueMockResponse("newsresponse.json")
            val responseBody = service.getTopHeadlines(country = "us", 1).body()
            val articleList = responseBody!!.articles
            assertThat(articleList.size).isEqualTo(20)
        }
    }

    @Test
    fun getTopHeadlines_receivedResponse_correctContent() {
        runBlocking {
            enqueueMockResponse("newsresponse.json")
            val responseBody = service.getTopHeadlines(country = "us", 1).body()
            val articleList = responseBody!!.articles
            val article = articleList[0]
            assertThat(article.author).isEqualTo("Ken Belson")
            assertThat(article.url).isEqualTo("https://www.nytimes.com/2022/01/15/sports/football/nfl-scores.html")
            assertThat(article.publishedAt).isEqualTo("2022-01-16T06:29:00Z")
        }
    }
}