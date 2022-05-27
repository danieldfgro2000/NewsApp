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
import timber.log.Timber.Forest.e


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
        setupSpinners()
        setSearchView()
        observeTopHeadlines()
        observeSearchedQuery()
        observeSearchedNews()
    }

    private fun initRecyclerView() {
        newsAdapter = NewsAdapter()

        fragmentNewsBinding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@NewsFragment.onScrollListener)
        }

        newsAdapter.setOnItemClickListener { article ->

            article?.let {
                e("article = $article")
                val bundle = Bundle().apply {
                    putSerializable("selected_article", article)
                }
                findNavController().navigate(R.id.action_newsFragment_to_infoFragment, bundle)
            }

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

    private fun setupSpinners() {
        with(newsViewModel) {
            countrySpinner = fragmentNewsBinding.spCountry
            Spinners().setupSpinner(
                requireContext(),
                countrySpinner,
                countryList,
                selectedCountry
            )
            newsCategorySpinner = fragmentNewsBinding.spNewsType
            Spinners().setupSpinner(
                requireContext(),
                newsCategorySpinner,
                newsTypeList,
                selectedCategory
            )

            selectedCountry.observe(viewLifecycleOwner) { country ->

                if (previousCountry.value.isNullOrEmpty()) {
                    previousCountry.value = country
                }
                if (country != previousCountry.value && !country.isNullOrEmpty()) {
                    selectedCategory.value?.let { category ->
                        getNewsHeadLines(
                            page = page,
                            country = country,
                            category = category
                        )
                        previousCountry.value = country
                    }
                }
            }

            selectedCategory.observe(viewLifecycleOwner) { category ->

                if (previousCategory.value.isNullOrEmpty()) {
                    previousCategory.value = category
                    selectedCountry.value?.let { country ->
                        getNewsHeadLines(
                            page = page,
                            country = country,
                            category = category
                        )
                    }
                }
                if (category != previousCategory.value && !category.isNullOrEmpty()) {
                    selectedCountry.value?.let { country ->
                        getNewsHeadLines(
                            page = page,
                            country = country,
                            category = category
                        )
                        previousCategory.value = category
                    }
                }
            }
        }
    }

    private fun observeTopHeadlines() {
        newsViewModel.newsHeadLines.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressbar()
                    response.data?.let {
                        pages = 1
                        page = 0
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
                    if (!p0.isNullOrEmpty()) {
                        newsViewModel.searchedQuery.value = p0
                    }
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    if (!p0.isNullOrEmpty() && p0.length >= 5) {
                        newsViewModel.searchedQuery.value = p0
                    }
                    return false
                }
            })

        fragmentNewsBinding.svNews.setOnCloseListener {
            Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun observeSearchedQuery() {

        with(newsViewModel) {
            searchedQuery.observe(viewLifecycleOwner) { searchedQuery ->

                if (previousSearchedQuery.value.isNullOrEmpty()) {
                    previousSearchedQuery.value = searchedQuery
                    searchNews()
                }

                if (searchedQuery != previousSearchedQuery.value) {
                    counterSetup()
                    observeTimer()
                }
            }
        }
    }

    private fun observeTimer() {
        with(newsViewModel){
            notTyping.observe(viewLifecycleOwner){
                e("typing = $it")
//            if(it) newsViewModel.searchNews()
            }
        }
    }

    private fun observeSearchedNews() {

        with(newsViewModel) {
            searchedNews.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressbar()
                        response.data?.let {
                            pages = 1
                            page = 0
                            if (previousSearchedNews.value == null) {
                                previousSearchedNews.value = response
                                newsAdapter.differ.submitList(it.articles.toList())
                            }

                            if (response.data.articles.size != previousSearchedNews.value?.data?.articles?.size) {
                                e("Searched news changed")
                                e("Searched news changed response = ${response.data.articles.size}")
                                e("Searched news changed previous = ${previousSearchedNews.value?.data?.articles?.size}")
                                newsAdapter.differ.submitList(it.articles.toList())
                                previousSearchedNews.value = response
                            }

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
                            Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
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