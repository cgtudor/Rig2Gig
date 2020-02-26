package com.gangoffive.rig2gig;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import java.util.Map;

/**
 * Defines the methods for any entity creating an advertisement
 */
public interface CreateAdvertisement
{
    /**
     * Set up references to view items
     */
    public void setViewReferences();

    /**
     * Populate the fields of a view with data
     */
    public void populateInitialFields();

    /**
     * attempt to post advertisement to database
     */
    public void  createAdvertisement();

    /**
     * cancel creation of advertisement
     */
    public void cancelAdvertisement();

    /**
     * map data from view into listing to be posted to database
     */
    public void listingDataMap();

    /**
     * validate the data held in the listing map
     * @return true if valid, false if not
     */
    public boolean validateDataMap();

    /**
     * actions to process if the listing was successfully posted to the database
     * @param data
     */
    public void onSuccessFromDatabase(Map <String, Object> data);

    /**
     * get imageView if another class requires it (such as Listing Manager)
     * @return imageView
     */
    public ImageView getImageView();

    /**
     * handle the success and failure responses from posting to the database
     * @param creationResult defines the result (eg SUCCESS, IMAGE_FAILURE, etc)
     */
    public void handleDatabaseResponse (Enum creationResult);

    public void onSuccessfulImageDownload();


}
