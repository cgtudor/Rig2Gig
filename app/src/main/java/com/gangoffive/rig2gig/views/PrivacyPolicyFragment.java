package com.gangoffive.rig2gig.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;

import com.gangoffive.rig2gig.R;

public class PrivacyPolicyFragment extends Fragment {

    WebView mWebView;

    public PrivacyPolicyFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.termsandconditions, container, false);


        mWebView = v.findViewById(R.id.tandcs);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.loadUrl("file:///android_asset/privacy.html");
        return v;
    }
}
