package com.longle.ui.aboutme

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
import com.longle.R
import com.longle.databinding.FragmentAboutMeBinding
import com.longle.util.autoCleared
import com.longle.util.hide
import com.longle.util.show

/**
 * A fragment representing about me
 */
class AboutMeFragment : Fragment() {

    private val aboutUrl = "file:///android_asset/about_me.html"
    private var binding by autoCleared<FragmentAboutMeBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_about_me,
            container,
            false
        )
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        loadData()
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

    private fun loadData() {
        binding?.webView?.loadUrl(aboutUrl)
    }

    override fun onDestroyView() {
        binding?.webView?.stopLoading()
        super.onDestroyView()
    }
}
