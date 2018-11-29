package com.r3pi.task.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.r3pi.task.R;
import com.r3pi.task.utils.Constants;

/**
 * Created by margarita on 11/24/18.
 */

public class BookGoogleInfoWebViewActivity extends AppCompatActivity {
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_google_info_webview);
        // /book url to load
        String infoUrl = getIntent().getStringExtra(Constants.BOOK_URL);
        WebView mBookWebView = findViewById(R.id.bookWebView);
        mBookWebView.setWebViewClient(new BookWebViewClient());
        WebSettings settings = mBookWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mBookWebView.loadUrl(infoUrl);
    }
    public class BookWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }
}
