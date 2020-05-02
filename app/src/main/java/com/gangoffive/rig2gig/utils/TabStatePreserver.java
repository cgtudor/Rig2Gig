package com.gangoffive.rig2gig.utils;

import android.app.Activity;
import android.view.WindowManager;

import com.gangoffive.rig2gig.utils.TabbedViewReferenceInitialiser;

public class TabStatePreserver {

    private boolean editingText;
    private boolean savedOnFocus;
    private int saveLoopCount;
    private boolean breakout;
    private TabbedViewReferenceInitialiser tabView;

    /**
     * Constructor for TabStatePreserver
     * @param tabbedView interface creating object
     */
    public TabStatePreserver(TabbedViewReferenceInitialiser tabbedView)
    {
        editingText = false;
        savedOnFocus = false;
        saveLoopCount = 0;
        breakout = false;
        tabView = tabbedView;
    }

    /**
     * Determine if saving tabs is required
     */
    public void preserveTabState()
    {
        if (!editingText && !breakout)
        {
            if (saveLoopCount == 0)
            {
                tabView.saveTabs();
            }
            ((Activity)tabView).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            breakOut();
        }
        else if (editingText && !savedOnFocus)
        {
            tabView.saveTabs();
            ((Activity)tabView).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            savedOnFocus = true;
        }
        else if (breakout)
        {
            breakout = false;
        }
    }

    /**
     * Determine if sufficient loops have occurred to ensure tabs are preserved
     */
    public void breakOut() {
        if (saveLoopCount == 15)
        {
            tabView.setMapping(true);
        }
        saveLoopCount++;
        if (saveLoopCount > 15)
        {
            breakout = true;
            saveLoopCount = 0;
            tabView.setMapping(false);
        }
    }

    /**
     * Ensure tracing variables are correct on focus change
     * @param hasFocus true if has focus
     */
    public void onFocusChange(boolean hasFocus)
    {
        if (hasFocus)
        {
            editingText = true;
            savedOnFocus = false;
        }
        else
        {
            editingText = false;
            savedOnFocus = false;
        }
    }
}
