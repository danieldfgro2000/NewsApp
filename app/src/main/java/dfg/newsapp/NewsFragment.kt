package dfg.newsapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.SearchView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dfg.newsapp.data.model.Article
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

    private lateinit var viewModel: NewsViewModel
    private lateinit var fragmentNewsBinding: FragmentNewsBinding

    private lateinit var newsAdapter: NewsAdapter

    private var country = "gb"
    private var page = 1
    private var pages = 0
    private var isLastPage = false
    private var isScrolling = false
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentNewsBinding = FragmentNewsBinding.bind(view)
        viewModel = (activity as MainActivity).viewModel
        newsAdapter = (activity as MainActivity).newsAdapter

        val countrySpinner = fragmentNewsBinding.spCountry
        Spinners().setupSpinner(requireContext(), countrySpinner, countryList, viewModel.selectedCountry)
        viewModel.selectedCountry.observe(viewLifecycleOwner){
            viewNewsList()
        }

        val newsTypeSpinner = fragmentNewsBinding.spNewsType
        Spinners().setupSpinner(requireContext(), newsTypeSpinner, newsTypeList, viewModel.selectedNewsType)
        viewModel.selectedNewsType.observe(viewLifecycleOwner){
            viewNewsList()
        }

        initRecyclerView()
        viewNewsList()
        setSearchView()
    }

    private fun viewNewsList() {
        viewModel.getNewsHeadLines( page = page )
        viewModel.newsHeadLines.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressbar()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        if (it.totalResults % 20 == 0) {
                            pages = it.totalResults / 20
                        } else {
                            pages = it.totalResults / 20 + 1
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
                    viewModel.searchNews("us", p0.toString(), page)
                    viewSearched()
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    MainScope().launch {
                        delay(2000)
                        viewModel.searchNews("us", p0.toString(), page)
                        viewSearched()
                    }
                    return false
                }
            })

        fragmentNewsBinding.svNews.setOnCloseListener(
            object : SearchView.OnCloseListener{
                override fun onClose(): Boolean {
                    initRecyclerView()
                    viewNewsList()
                    return false
                }
        } )
    }

    private fun viewSearched() {

        viewModel.searchedNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressbar()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        if (it.totalResults % 20 == 0) {
                            pages = it.totalResults / 20
                        } else {
                            pages = it.totalResults / 20 + 1
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

            val hasReachedToEnd = topPosition + visibleItems >= sizeOfTheCurrentList
            val shouldPaginate = !isLoading && !isLastPage && hasReachedToEnd && isScrolling
            if (shouldPaginate) {
                page ++
                viewModel.getNewsHeadLines(page = page)
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