package com.coalbell.farklescrore

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebViewClient

import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        setSupportActionBar(toolbarFeedback)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<WebView>(R.id.feedbackPage).apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl("https://docs.google.com/forms/d/e/1FAIpQLSe6EFtVn06cnFVRMISAvNm5pSCxFZjO4vrZ11vbsiyyFib0aw/viewform?usp=sf_link")
        }
    }
}
