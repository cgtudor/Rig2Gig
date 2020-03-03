package com.gangoffive.rig2gig;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;

public class TabStatePreserver {

    private boolean editingText;
    private boolean savedOnFocus;
    private int saveLoopCount;
    private boolean breakout;
    private TabbedViewReferenceInitialiser tabView;

    public TabStatePreserver(TabbedViewReferenceInitialiser tabbedView)
    {
        editingText = false;
        savedOnFocus = false;
        saveLoopCount = 0;
        breakout = false;
        tabView = tabbedView;
    }

    public void preserveTabState()
    {
        if (!editingText && !breakout)
        {
            tabView.saveTabs();
            ((Activity)tabView).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            breakOut();
        }
        else if (editingText && !savedOnFocus)
        {
            tabView.saveTabs();
            ((Activity)tabView).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            savedOnFocus = true;
        }
        else if (breakout)
        {
            breakout = false;
        }
    }

    public void breakOut() {
        saveLoopCount++;
        if (saveLoopCount > 15)
        {
            breakout = true;
            saveLoopCount = 0;
        }
    }

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
