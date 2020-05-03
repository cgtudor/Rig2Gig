package com.gangoffive.rig2gig;

import com.gangoffive.rig2gig.band.management.MusiciansBands;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.assertEquals;

/**
 * This test class is used to test and maintain the integrity of the MusiciansBands class.
 * @author Ben souch
 * @version #0.3b
 * @since #0.3b
 */
public class MusiciansBandsTest
{
    /**
     * Rule created to be used in several incorrect value inputs in the following tests.
     * @since #0.3b
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * getBandReferenceTest() tests the getReference() method when creating a MusiciansBands object with the correct values.
     * @since #0.3b
     */
    @Test
    public void getBandReferenceTest()
    {
        MusiciansBands musiciansBands = new MusiciansBands("ABC", "GunsNRoses");

        String expResult = "ABC";
        String actualResult = musiciansBands.getReference();

        assertEquals(expResult, actualResult);
    }

    /**
     * getBandNameTest() tests the getBandName() method when creating a MusiciansBands object with the correct values.
     * @since #0.3b
     */
    @Test
    public void getBandNameTest()
    {
        MusiciansBands musiciansBands = new MusiciansBands("ABC", "GunsNRoses");

        String expResult = "GunsNRoses";
        String actualResult = musiciansBands.getBandName();

        assertEquals(expResult, actualResult);
    }

    /**
     * getBandReferenceNullTest() tests creating an object of MusiciansBands with a null reference entry.
     * @since #1.0
     * @exception  IllegalArgumentException
     * @see IllegalArgumentException
     */
    @Test
    public void getBandReferenceNullTest()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Please ensure reference and/or band name is not empty or null");
        MusiciansBands musiciansBands = new MusiciansBands(null, "GunsNRoses");
    }

    /**
     * getBandReferenceEmpty() tests creating an object of MusiciansBands with an empty reference entry.
     * @since #1.0
     * @exception  IllegalArgumentException
     * @see IllegalArgumentException
     */
    @Test
    public void getBandReferenceEmpty()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Please ensure reference and/or band name is not empty or null");
        MusiciansBands musiciansBands = new MusiciansBands("", "GunsNRoses");
    }

    /**
     * getBandNameNull() tests creating an object of MusiciansBands with a null band name entry.
     * @since #1.0
     * @exception  IllegalArgumentException
     * @see IllegalArgumentException
     */
    @Test
    public void getBandNameNull()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Please ensure reference and/or band name is not empty or null");
        MusiciansBands musiciansBands = new MusiciansBands("ABC", null);
    }

    /**
     * getBandNameEmpty() tests creating an object of MusiciansBands with an empty band name entry.
     * @since #1.0
     * @exception  IllegalArgumentException
     * @see IllegalArgumentException
     */
    @Test
    public void getBandNameEmpty()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Please ensure reference and/or band name is not empty or null");
        MusiciansBands musiciansBands = new MusiciansBands("ABC", "");
    }
}