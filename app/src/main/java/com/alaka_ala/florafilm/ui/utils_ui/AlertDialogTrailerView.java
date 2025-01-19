package com.alaka_ala.florafilm.ui.utils_ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.ui.player.web.PlayerWebActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AlertDialogTrailerView extends MaterialAlertDialogBuilder {
    private final Context context;
    private final View view;
    private WebView webView;
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;
    private AlertDialog alertDialog;
    private String poster = "";

    @SuppressLint("SetJavaScriptEnabled")
    public AlertDialogTrailerView(Activity activity, @NonNull Context context, String url, String poster, CallbackFullScreen callbackFullScreen) {
        super(context);
        this.context = context;
        this.callbackFullScreen = callbackFullScreen;
        view = View.inflate(context, R.layout.show_trailer_dialog_alert, null);
        setView(view);
        webView = view.findViewById(R.id.webViewPlayers);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

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
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                callbackFullScreen.inFullscreen();
                alertDialog.dismiss();
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                if (customView == null) {
                    return;
                }
                //activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                FrameLayout decor = (FrameLayout) activity.getWindow().getDecorView();
                decor.removeView(customView);
                customView = null;
                customViewCallback.onCustomViewHidden();
                webView.setVisibility(View.VISIBLE);
                callbackFullScreen.exitFullscreen();
            }
        });


        String urlRequest;
        if (poster.isEmpty()){
            urlRequest = "file:///android_asset/player_web.html?file=" + url;
        } else {
            urlRequest = "file:///android_asset/player_web.html?file=" + url + "&poster=" + poster;
        }


        webView.loadUrl(urlRequest);


    }

    public void showAlert() {
        alertDialog = super.show();
    }


    public interface CallbackFullScreen {
        void inFullscreen();
        void exitFullscreen();
    }

    private CallbackFullScreen callbackFullScreen;

    public void setCallbackFullScreen(CallbackFullScreen callbackFullScreen) {
        this.callbackFullScreen = callbackFullScreen;
    }


}
