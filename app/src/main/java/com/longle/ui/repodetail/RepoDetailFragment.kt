package com.longle.ui.repodetail

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.longle.R
import com.longle.databinding.FragmentRepoDetailBinding
import com.longle.di.Injectable
import com.longle.di.ViewModelFactory
import com.longle.util.autoCleared
import com.longle.util.hide
import com.longle.util.show
import javax.inject.Inject

/**
 * A fragment representing a single Repo detail screen.
 */
class RepoDetailFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelFactory<RepoDetailViewModel>

    private val viewModel: RepoDetailViewModel by viewModels { factory }
    private var binding by autoCleared<FragmentRepoDetailBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_repo_detail,
            container,
            false
        )
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        subscribeUi()
    }

    private fun setUpView() {
        binding?.webView?.webChromeClient = WebChromeClient()
        binding?.webView?.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding?.progressBar?.show()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding?.progressBar?.hide()
            }
        }
    }

    private fun subscribeUi() {
        viewModel.repo.observe(viewLifecycleOwner, Observer { repo ->
            if (repo != null) {
                binding?.webView?.loadUrl(repo.html_url)
            }
        })
    }

    override fun onDestroyView() {
        binding?.webView?.stopLoading()
        super.onDestroyView()
    }
}
