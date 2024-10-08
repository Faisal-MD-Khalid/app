package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final String HTML_FILE_PATH ="file:///android_asset/animation.html";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        // Load HTML file containing GSAP animation
        webView.loadUrl(HTML_FILE_PATH);

        // Show the WebView
        webView.setVisibility(View.VISIBLE);

        // Hide the TextView
        //TextView textView = findViewById(R.id.textView);
        //textView.setVisibility(View.GONE);

        // Optional: You can set a delay before transitioning to the next activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the next activity
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, calculateAnimationDuration());
    }

    // Calculate the duration of the animation, adjust this according to your animation duration
    private long calculateAnimationDuration() {
        // Adjust this value according to your animation duration
        return 5000; // Example: 5 seconds
    }
}
