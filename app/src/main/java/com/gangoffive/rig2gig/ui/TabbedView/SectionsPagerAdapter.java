package com.gangoffive.rig2gig.ui.TabbedView;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.gangoffive.rig2gig.TabbedViewReferenceInitialiser;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static int[] TAB_TITLES;
    private static Context mContext = null;
    private static int[] fragments;

    public SectionsPagerAdapter(Context context, FragmentManager fm, int[] tabTitles, int[] fragmentArray) {
        super(fm);
        mContext = context;
        TAB_TITLES = tabTitles;
        fragments = fragmentArray;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Fragment fragment = PlaceholderFragment.newInstance(position, fragments);
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        ((TabbedViewReferenceInitialiser) mContext).saveTabs();
        return TAB_TITLES.length;
    }

    public static Context getmContext()
    {
        return mContext;
    }
}
