package com.gangoffive.rig2gig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



public class PrivacyPolicyFragment extends Fragment
{
    private WebView privacyPolicy;
    private final String URL = "https://docs.google.com/document/d/1-_KodnbqHXue-iuDBWvAJdzclOdonEe9QuGLptOV0FE/view";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_privacy_policy, container, false);

        privacyPolicy = view.findViewById(R.id.webView);
        privacyPolicy.setWebViewClient(new WebViewClient());
        //privacyPolicy.getSettings().setJavaScriptEnabled(true);

        privacyPolicy.loadUrl(URL);

        return view;
    }
}
