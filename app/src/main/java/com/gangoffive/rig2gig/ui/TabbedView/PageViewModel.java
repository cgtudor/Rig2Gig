package com.gangoffive.rig2gig.ui.TabbedView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();

    /**
     * ser index of tab
     * @param index tab index
     */
    public void setIndex(int index) {
        mIndex.setValue(index);
    }
    
}