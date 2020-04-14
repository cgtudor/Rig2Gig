package com.gangoffive.rig2gig.utils;

/**
 * Interface for activities using ui.TabbedView (override methods when using 3 or more tabs)
 */
public interface TabbedViewReferenceInitialiser {
    /**
     * save data from tabs which may be destroyed
     */
    public void saveTabs();

    /**
     * reinitialise views in tabs which may have been destroyed
     */
    public void reinitialiseTabs();

    /**
     * commence tab preservation process where required
     */
    public void beginTabPreservation();

    /**
     * @param isMapping isMapping to set
     */
    public void setMapping(boolean isMapping);
}
