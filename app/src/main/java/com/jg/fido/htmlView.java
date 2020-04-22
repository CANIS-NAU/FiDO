package com.jg.fido;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

public class htmlView extends AppCompatActivity {

    private  WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_view);

        // Get file name of HTML
        Intent intent = getIntent();
        final String fileName = intent.getStringExtra("fileName");
        //final String fileName = "www.google.com.html";

        webView = (WebView)findViewById(R.id.web_view);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        //webView.loadUrl("http://bblearn.nau.edu");

        //webView.loadUrl("file:///android_asset/Login.html");

        webView.loadUrl("file:"+getFilesDir()+File.separator+fileName);


        // fit screen
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);

        //hand operation --make it bigger or smaller
        webView.getSettings().setSupportZoom(true);
    }

    public boolean onKeyDown(int keyCode , KeyEvent keyEvent){
        if(keyCode==keyEvent.KEYCODE_BACK){//监听返回键，如果可以后退就后退
            if(webView.canGoBack()){
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, keyEvent);
    }

}
