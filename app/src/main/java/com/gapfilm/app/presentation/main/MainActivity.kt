package com.gapfilm.app.presentation.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit
import com.gapfilm.app.BuildConfig
import com.gapfilm.app.R
import com.gapfilm.app.presentation.util.*
import com.najva.sdk.NajvaClient

class MainActivity : AppCompatActivity() {
    private val baseUrl = BuildConfig.BASE_URL
    private lateinit var webView: WebView
    private lateinit var settingButton: AppCompatButton
    private lateinit var offlineLayout: FrameLayout
    private val setting by lazy { getSharedPreferences("mainActivitySetting", MODE_PRIVATE) }
    private val connectivityManager by lazy {
        applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private var firstRun: Boolean
        get() = setting.getBoolean("firstRun", true)
        set(value) = setting.edit { putBoolean("firstRun", value) }

    private val requestUrl: String
        get() {
            val url = (
                    if (intent?.action == "android.intent.action.VIEW" && intent?.data != null)
                        intent!!.data!! else Uri.parse(baseUrl)
                    ).buildUpon()

            url.appendQueryParameter("agent", BuildConfig.AGENT)
            url.appendQueryParameter("src", BuildConfig.SOURCE_CHANNEL)

            if (firstRun) url.appendQueryParameter("gpl_firstRun", "true")

            return url.build().toString()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        offlineLayout = findViewById(R.id.offline)
        settingButton = findViewById(R.id.setting)

        registerNetwork(connectivityManager) { if (it) setOnline() else setOffline() }
        initView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        settingButton.setOnClickListener { startActivity(Intent(Settings.ACTION_SETTINGS)) }

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowContentAccess = true
            mediaPlaybackRequiresUserGesture = false
        }

        webView.setBackgroundColor(getRColor(R.color.status))
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webView.webChromeClient = WebChromeClient()

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (firstRun) firstRun = false
            }

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                super.doUpdateVisitedHistory(view, url, isReload)
                var detect = false
                if (url != null) Regex("^([a-z]+)[/]").find(url.replace(baseUrl, ""))?.let {
                    if (it.value == "watch/") {
                        enableWatchMode()
                        detect = true
                    }
                }
                if (!detect) disableWatchMode()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url == null) return false
                val target = Uri.parse(url)
                var detect = false
                when (target.host) {
                    "play.google.com",
                    "instagram.com",
                    "www.instagram.com",
                    "wa.me",
                    "chat.whatsapp.com",
                    "t.me" -> detect = true
                }
                if (target.scheme == "sms") detect = true
                if (detect) try {
                    startActivity(Intent(Intent.ACTION_VIEW, target))
                    return true
                } catch (e: Exception) {
                }
                return false
            }
        }

        webView.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == MotionEvent.ACTION_UP && webView.canGoBack()) {
                webView.goBack()
                return@setOnKeyListener true
            }
            false
        }

        webView.loadUrl(requestUrl)
    }

    private fun setOnline() = runOnUiThread {
        discardBlackUI()
        offlineLayout.visibility = View.GONE
    }

    private fun setOffline() = runOnUiThread {
        setBlackUI()
        offlineLayout.visibility = View.VISIBLE
    }

    private fun enableWatchMode() {
        setLandscape()
        hideSystemUI()
        setBlackUI()
    }

    private fun disableWatchMode() {
        setPortrait()
        showSystemUI()
        discardBlackUI()
    }

    private fun setBlackUI() {
        window.statusBarColor = getRColor(R.color.background)
        window.navigationBarColor = getRColor(R.color.background)
    }

    private fun discardBlackUI() {
        window.statusBarColor = getRColor(R.color.background)
        window.navigationBarColor = getRColor(R.color.navigation)
    }
}