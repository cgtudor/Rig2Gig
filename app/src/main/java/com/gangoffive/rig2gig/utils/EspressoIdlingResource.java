package com.gangoffive.rig2gig.utils;

import androidx.test.espresso.idling.CountingIdlingResource;

public class EspressoIdlingResource {
    private static final String RESOURCE = "GLOBAL";
    private static CountingIdlingResource countingIdlingResource = new CountingIdlingResource(RESOURCE);

    /**
     * Increment countingIdlingResource
     */
    public static void increment()
    {
        countingIdlingResource.increment();
    }

    /**
     * Decrement countingIdlingResource
     */
    public static void decrement()
    {
        if(!countingIdlingResource.isIdleNow())
        {
            countingIdlingResource.decrement();
        }
    }

    /**
     * @return countingIdlingResource
     */
    public static CountingIdlingResource getCountingIdlingResource() {
        return countingIdlingResource;
    }
}
