package com.novoprasistoffline;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebPedidos extends Activity {

	public  DataHelper dh;	
	public WebView wv;
	public String sPath;
	
    @SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webpedidos); 
        
        List<String[]> sParametros;
        
        this.dh = new DataHelper(getApplicationContext());
                 
 	    sParametros = dh.RetornaParametros("");
 	
 	    for (String[] name : sParametros) 
 	    { 	
 	    	sPath 	 = name[2].toString();
 	    }
 	    
        wv = (WebView) findViewById(R.id.webView1);
        
        WebSettings ws = wv.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setSupportZoom(false);
        ws.setPluginState(PluginState.ON);                
        ws.setAllowFileAccess(true);
        
        wv.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                    }
            });
                
        wv.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }
        });
        
        wv.loadUrl(sPath);        
    }
}