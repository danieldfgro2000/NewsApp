package dfg.newsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import dfg.newsapp.BuildConfig
import dfg.newsapp.R
import dfg.newsapp.databinding.ActivityMainBinding
import dfg.newsapp.presentation.adapter.NewsAdapter
import dfg.newsapp.presentation.viewmodel.NewsViewModel
import dfg.newsapp.presentation.viewmodel.NewsViewModelFactory
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: NewsViewModelFactory
    val newsViewModel: NewsViewModel by viewModels()

    @Inject
    lateinit var newsAdapter: NewsAdapter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (BuildConfig.DEBUG) { Timber.plant(Timber.DebugTree()) }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bnvNews.setupWithNavController(navController)
    }
}