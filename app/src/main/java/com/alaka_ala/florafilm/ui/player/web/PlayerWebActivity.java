package com.alaka_ala.florafilm.ui.player.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alaka_ala.florafilm.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlayerWebActivity extends AppCompatActivity {
    private WebView webView;
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;
    private Activity activity;
    private String url;
    private String poster;
    private JSONArray files;

    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player_web);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        url = getIntent().getStringExtra("url");
        poster = getIntent().getStringExtra("poster");
        String json = getIntent().getStringExtra("files");
        if (url.isEmpty()) {
            try {
                if (json != null) {
                    files = new JSONArray(json);
                }
            }
            catch (JSONException e) {
                throw new RuntimeException(e);
            }
            if (files == null) return;
        }

        webView = findViewById(R.id.webViewWebPlayer);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        webView.setWebChromeClient(new WebChromeClient() {
            private View customView;
            private CustomViewCallback customViewCallback;

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                if (customView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                //activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                customView = view;
                customViewCallback = callback;
                webView.setVisibility(View.GONE);
                FrameLayout decor = (FrameLayout) activity.getWindow().getDecorView();
                decor.addView(customView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                if (customView == null) {
                    return;
                }
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                FrameLayout decor = (FrameLayout) activity.getWindow().getDecorView();
                decor.removeView(customView);
                customView = null;
                activity.finish();
                customViewCallback.onCustomViewHidden();
                webView.setVisibility(View.VISIBLE);
            }


        });


        String urlRequest;
        if (files != null) {
            urlRequest = "file:///android_asset/player_web.html?play=0&file=" + files;
        } else {
            urlRequest = "file:///android_asset/player_web.html?play=0&file=" + url;
        }

        if (!poster.isEmpty()) {
            urlRequest += "&poster=" + poster;
        }


        webView.loadUrl(urlRequest);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}