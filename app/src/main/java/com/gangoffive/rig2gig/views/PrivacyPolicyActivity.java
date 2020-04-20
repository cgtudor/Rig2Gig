package com.gangoffive.rig2gig.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.gangoffive.rig2gig.R;

public class PrivacyPolicyActivity extends AppCompatActivity {
    WebView mWebView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.termsandconditions);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Rig2Gig Privacy Policy");
        /*Setting the support action bar to the newly created toolbar*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebView = findViewById(R.id.activitywebview);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.loadUrl("file:///android_asset/privacy.html");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
