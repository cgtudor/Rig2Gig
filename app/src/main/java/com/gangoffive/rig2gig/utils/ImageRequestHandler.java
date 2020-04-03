package com.gangoffive.rig2gig.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static androidx.core.app.ActivityCompat.requestPermissions;


public class ImageRequestHandler
{
    private static final int REQUEST_GALLERY__PHOTO = 1;
    private static final int REQUEST_PHOTO = 2;

    /**
     * get gallery image for advertisement
     * @param v view of calling activity
     */
    public static void getGalleryImage(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        ((Activity)v.getContext()).startActivityForResult(intent, REQUEST_GALLERY__PHOTO);

    }

    /**
     * get camera image for advertisement
     * @param v view of calling activity
     */
    public static void getCameraImage(View v) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(((Activity)v.getContext()).getPackageManager()) != null) {
            ((Activity)v.getContext()).startActivityForResult(intent, REQUEST_PHOTO);
        }
    }

    /**
     * Handle respose of request and set image of calling activity
     * @param requestCode request code
     * @param resultCode result code
     * @param data data received from response
     * @param image image view to set image to
     * @return ImageView to be returned to activity
     */
    public static ImageView handleResponse(int requestCode, int resultCode, Intent data, ImageView image)
    {
        if (requestCode == REQUEST_GALLERY__PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            try {
                InputStream iStream = image.getContext().getContentResolver().openInputStream(data.getData());
                image.setImageDrawable(Drawable.createFromStream(iStream, data.getData().toString()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(photo);
        }
        return image;
    }
}
