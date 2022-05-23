package dfg.newsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dfg.newsapp.data.util.Resource
import dfg.newsapp.databinding.FragmentNewsBinding
import dfg.newsapp.presentation.adapter.NewsAdapter
import dfg.newsapp.presentation.viewmodel.NewsViewModel
import dfg.newsapp.util.Spinners
import dfg.newsapp.util.countryList
import dfg.newsapp.util.newsTypeList
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class NewsFragment : Fragment() {

    private val newsViewModel: NewsViewModel by activityViewModels()
    private lateinit var fragmentNewsBinding: FragmentNewsBinding

    private lateinit var newsAdapter: NewsAdapter

    private var page = 1
    private var pages = 0
    private var isLastPage = false
    private var isScrolling = false
    private var isLoading = false
    private lateinit var countrySpinner: Spinner
    private lateinit var newsCategorySpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentNewsBinding = FragmentNewsBinding.bind(view)

        newsAdapter = (activity as MainActivity).newsAdapter

        initRecyclerView()
        setSearchView()
        viewNewsList()

        countrySpinner = fragmentNewsBinding.spCountry
        Spinners().setupSpinner(
            requireContext(),
            countrySpinner,
            countryList,
            newsViewModel.selectedCountry
        )
        newsCategorySpinner = fragmentNewsBinding.spNewsType
        Spinners().setupSpinner(
            requireContext(),
            newsCategorySpinner,
            newsTypeList,
            newsViewModel.selectedCategory
        )

        newsViewModel.selectedCountry.observe(viewLifecycleOwner) {
            pages = 1
            page = 0
            if (it != newsViewModel.previousCountry.value) {
                newsViewModel.getNewsHeadLines(
                    page = page,
                    country = it,
                    category = newsViewModel.selectedCategory.value
                )
                newsViewModel.previousCountry.value = it
            }
        }

        newsViewModel.selectedCategory.observe(viewLifecycleOwner) {

            pages = 1
            page = 0
            if (it != newsViewModel.previousCategory.value) {
                newsViewModel.getNewsHeadLines(
                    page = page,
                    country = newsViewModel.selectedCountry.value,
                    category = it
                )
                newsViewModel.previousCategory.value = it
            }
        }
    }

    private fun viewNewsList() {
        newsViewModel.newsHeadLines.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressbar()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        pages = if (it.totalResults % 20 == 0) {
                            it.totalResults / 20
                        } else {
                            it.totalResults / 20 + 1
                        }
                        isLastPage = page == pages
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Error -> {
                    hideProgressbar()
                    response.message?.let {
                        Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setSearchView() {
        fragmentNewsBinding.svNews.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    newsViewModel.searchNews(
                        newsViewModel.selectedCountry.value ?: "us",
                        p0.toString(),
                        page
                    )
                    viewSearched()
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    MainScope().launch {
                        delay(2000)
                        newsViewModel.searchNews(
                            newsViewModel.selectedCountry.value ?: "us",
                            p0.toString(),
                            page
                        )
                        viewSearched()
                    }
                    return false
                }
            })

        fragmentNewsBinding.svNews.setOnCloseListener {
            initRecyclerView()
            newsViewModel.getNewsHeadLines(
                country = newsViewModel.selectedCountry.value,
                category = newsViewModel.selectedCategory.value,
                page
            )
            false
        }
    }

    private fun viewSearched() {

        newsViewModel.searchedNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressbar()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        pages = if (it.totalResults % 20 == 0) {
                            it.totalResults / 20
                        } else {
                            it.totalResults / 20 + 1
                        }
                        isLastPage = page == pages
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Error -> {
                    hideProgressbar()
                    response.message?.let {
                        Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        newsAdapter = NewsAdapter()

        fragmentNewsBinding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@NewsFragment.onScrollListener)
        }

        newsAdapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("selected_article", article)
            }
            findNavController().navigate(R.id.action_newsFragment_to_infoFragment, bundle)
        }
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = fragmentNewsBinding.rvNews.layoutManager as LinearLayoutManager
            val sizeOfTheCurrentList = layoutManager.itemCount
            val visibleItems = layoutManager.childCount
            val topPosition = layoutManager.findFirstVisibleItemPosition()

            val hasReachedToEnd = topPosition + visibleItems - 2 >= sizeOfTheCurrentList
            val shouldPaginate = !isLoading && !isLastPage && hasReachedToEnd && isScrolling
            if (shouldPaginate) {
                page++
                newsViewModel.getNewsHeadLines(
                    page = page,
                    country = newsViewModel.selectedCountry.value,
                    category = newsViewModel.selectedCategory.value
                )
                isScrolling = false
            }
        }
    }

    private fun showProgressBar() {
        isLoading = true
        fragmentNewsBinding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressbar() {
        isLoading = false
        fragmentNewsBinding.progressBar.visibility = View.GONE
    }
}