package com.mroom.cloudapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

public class RegisterActivity extends Activity {
	
	private WebView webview;
	private String registerURL = "https://www.dropbox.com/register";
	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register);
        
        webview = (WebView)findViewById(R.id.webView);
        webview.loadUrl(registerURL);
    }
	
}
