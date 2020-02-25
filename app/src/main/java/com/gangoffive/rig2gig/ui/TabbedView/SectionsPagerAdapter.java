package com.gangoffive.rig2gig.ui.TabbedView;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static int[] TAB_TITLES;
    private final Context mContext;
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

        return PlaceholderFragment.newInstance(position + 1, fragments);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
/*        InputMethodManager input = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
        input.hideSoftInputFromWindow(((Activity)mContext).getWindow().getDecorView().getRootView().getWindowToken() , 0);*/

        return 2;
    }
}
