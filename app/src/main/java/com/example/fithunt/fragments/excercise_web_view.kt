package com.example.fithunt.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.fithunt.R
import com.example.fithunt.databinding.FragmentExcerciseWebViewBinding
import io.ak1.BubbleTabBar


class excercise_web_view : Fragment() {
    private var _binding: FragmentExcerciseWebViewBinding?=null
    private val binding get()=_binding!!
    private val args:excercise_web_viewArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding= FragmentExcerciseWebViewBinding.inflate(inflater,container,false)
        activity?.findViewById<BubbleTabBar>(R.id.bubbleTabBar)?.visibility=View.GONE
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.visibility=View.GONE
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.apply {
            webViewClient= WebViewClient()
            loadUrl(args.webLink)
        }

    }
    inner class WebViewClient : android.webkit.WebViewClient() {

        // Load the URL
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return false
        }

        // ProgressBar will disappear once page is loaded
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            binding.progressBar.visibility = View.GONE
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

}