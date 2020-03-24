package com.gangoffive.rig2gig;

import androidx.test.espresso.idling.CountingIdlingResource;

public class EspressoIdlingResource {
    private static final String RESOURCE = "GLOBAL";

    private static CountingIdlingResource countingIdlingResource = new CountingIdlingResource(RESOURCE);

    public static void increment()
    {
        countingIdlingResource.increment();
    }

    public static void decrement()
    {
        if(!countingIdlingResource.isIdleNow())
        {
            countingIdlingResource.decrement();
        }
    }

    public static CountingIdlingResource getCountingIdlingResource() {
        return countingIdlingResource;
    }
}
