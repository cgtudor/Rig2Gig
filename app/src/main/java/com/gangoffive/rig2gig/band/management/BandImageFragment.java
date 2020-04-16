package com.gangoffive.rig2gig.band.management;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.band.management.CreateBandFragment;
import com.gangoffive.rig2gig.navbar.NavBarActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BandImageFragment extends Fragment implements View.OnClickListener {
    FirebaseStorage fStorage = FirebaseStorage.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    Button uploadBtn, takeBtn;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    private static final int REQUEST_GALLERY__PHOTO = 1;

    private ImageView image;
    private Drawable chosenPic;

    public static Button submitBtn;
    String musicianRef;

    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.SYSTEM_ALERT_WINDOW","android.permission.CAMERA"};

    /**
     * Upon creation of the CreateBandFragment, create the fragment_create_band layout.
     * @param inflater The inflater is used to read the passed xml file.
     * @param container The views base class.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @return Returns a View of the fragment_about layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        final View v = inflater.inflate(R.layout.fragment_band_image, container, false);

        submitBtn = v.findViewById(R.id.submitBtn);
        uploadBtn = v.findViewById(R.id.uploadBtn);
        takeBtn = v.findViewById(R.id.takeBtn);
        uploadBtn.setOnClickListener(this);
        takeBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        submitBtn.setVisibility(View.INVISIBLE);

        image = v.findViewById(R.id.imageView);

        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.submitBtn:
                ImageView defImg = new ImageView(getActivity());
                defImg.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);

                DocumentReference docRef = fStore.collection("bands").document(CreateBandFragment.bandRef);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                DocumentReference documentReference = fStore.collection("bands").document(CreateBandFragment.bandRef);
                                StorageReference sRef = fStorage.getReference()
                                        .child("/images/bands/" + CreateBandFragment.bandRef + ".jpg");
                                UploadTask uploadTask = sRef.putBytes(imageToByteArray(image.getDrawable()));
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                                {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                    {
                                        Log.d("STORAGE SUCCEEDED", taskSnapshot.getMetadata().toString());
                                        Intent intent = new Intent(getActivity(), NavBarActivity.class);
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener()
                                {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        Log.d("STORAGE FAILED", e.toString());
                                    }
                                });

                            } else {
                                Log.d("LOGGER", "No such document");
                            }
                        } else {
                            Log.d("LOGGER", "get failed with ", task.getException());
                        }
                    }
                });

                break;
            case R.id.takeBtn:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,
                        CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.uploadBtn:
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, REQUEST_GALLERY__PHOTO);
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@2 " + REQUEST_GALLERY__PHOTO);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    public byte[] imageToByteArray(Drawable image) {
        Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap

                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);

                image.setImageBitmap(bitmap);
            }
        }
        else
        {
            if (requestCode == REQUEST_GALLERY__PHOTO)
            {
                Uri returnUri = data.getData();
                Bitmap bitmapImage = null;
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image.setImageBitmap(bitmapImage);
            }
        }
    }
}
