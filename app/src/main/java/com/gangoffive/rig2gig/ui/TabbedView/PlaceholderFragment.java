package com.gangoffive.rig2gig.ui.TabbedView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static int [] fragments;

    private PageViewModel pageViewModel;

    /**
     * create new PlaceholderFragment for use in tabbed view
     * @param index index of tab
     * @param fragmentArray array of fragments to populate the tabs
     * @return newly created fragment
     */
    public static PlaceholderFragment newInstance(int index, int [] fragmentArray) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        fragments = fragmentArray;
        return fragment;
    }

    /**
     * Overridden onCreate method
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 0;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    /**
     * Overridden onCreateView method to inflate each fragment as required
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = null;
        int page = getArguments().getInt(ARG_SECTION_NUMBER);
        root = inflater.inflate(fragments[page], container, false);
        return root;
    }
}