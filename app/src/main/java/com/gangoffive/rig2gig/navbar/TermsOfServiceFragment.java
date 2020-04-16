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


public class TermsOfServiceFragment extends Fragment
{
    private WebView termsOfService;
    private final String URL = "https://app.termly.io/document/terms-and-conditions/d4d4d728-74cc-4247-9fae-47c1763946ab";

    /**
     * Upon creation of the TermsOfServiceFragment, create the fragment_privacy_policy layout.
     * @param inflater The inflater is used to read the passed xml file.
     * @param container The views base class.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @return Returns a WebView of the fragment_privacy_policy layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_privacy_policy, container, false);

        termsOfService = view.findViewById(R.id.webView);
        termsOfService.setWebViewClient(new WebViewClient());
        termsOfService.getSettings().setJavaScriptEnabled(true);

        termsOfService.loadUrl(URL);

        return view;
    }
}
