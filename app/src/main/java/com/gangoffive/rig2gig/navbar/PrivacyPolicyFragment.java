package com.gangoffive.rig2gig.navbar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gangoffive.rig2gig.R;


public class PrivacyPolicyFragment extends Fragment
{
    private WebView privacyPolicy;
    private final String URL = "https://docs.google.com/document/d/1-_KodnbqHXue-iuDBWvAJdzclOdonEe9QuGLptOV0FE/view";

    /**
     * Upon creation of the PrivacyPolicyFragment, create the fragment_privacy_policy layout. Uses a WebView to view web related content inside the app.
     * @param inflater The inflater is used to read the passed xml file.
     * @param container The views base class.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @return Returns a View of the fragment_about layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_privacy_policy, container, false);

        privacyPolicy = view.findViewById(R.id.webView);
        privacyPolicy.setWebViewClient(new WebViewClient());

        privacyPolicy.loadUrl(URL);

        return view;
    }
}
