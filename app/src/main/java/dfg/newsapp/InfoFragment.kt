package dfg.newsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dfg.newsapp.databinding.FragmentInfoBinding
import dfg.newsapp.presentation.viewmodel.NewsViewModel


class InfoFragment : Fragment() {

    private lateinit var fragmentInfoBinding: FragmentInfoBinding
    private lateinit var viewModel: NewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentInfoBinding = FragmentInfoBinding.bind(view)
        val args: InfoFragmentArgs by navArgs()
        val article = args.selectedArticle

        viewModel = (activity as MainActivity).newsViewModel

        fragmentInfoBinding.wvInfo.apply {
            webViewClient = WebViewClient()

            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false

            if (article.url != null) {
                loadUrl(article.url)
            }
        }

        fragmentInfoBinding.floatingActionButton.setOnClickListener {
            viewModel.saveNewsToLocalDB(article)
            Snackbar.make(view, "Saved Successfully", Snackbar.LENGTH_LONG).show()
        }
    }
}