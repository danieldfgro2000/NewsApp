package dfg.newsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dfg.newsapp.databinding.ActivityMainBinding
import dfg.newsapp.domain.repository.NewsRepository
import dfg.newsapp.domain.usecase.GetNewsHeadlinesUseCase
import dfg.newsapp.presentation.adapter.NewsAdapter
import dfg.newsapp.presentation.viewmodel.NewsViewModel
import dfg.newsapp.presentation.viewmodel.NewsViewModelFactory
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: NewsViewModelFactory
    lateinit var viewModel: NewsViewModel
    @Inject
    lateinit var newsAdapter: NewsAdapter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bnvNews.setupWithNavController(navController)

        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]
    }
}