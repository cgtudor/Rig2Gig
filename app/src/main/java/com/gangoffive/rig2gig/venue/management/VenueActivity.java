package com.gangoffive.rig2gig.venue.management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.advert.management.GooglePlacesAutoSuggestAdapter;
import com.gangoffive.rig2gig.navbar.NavBarActivity;
import com.gangoffive.rig2gig.utils.ImageRequestHandler;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VenueActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private final String TAG = "@@@@@@@@@@@@@@@@";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;
    EditText description, location, name, venueType;
    Button submit, takePhotoBtn, uploadPhotoBtn;

    String email, userRef, phoneNumber, type;

    private ImageView image;
    private Drawable chosenPic;
    //Google Places autocomplete textview
    private AutoCompleteTextView autoCompleteTextView;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        description = findViewById(R.id.venue_description_final);
        name = findViewById(R.id.venue_name_final);
        venueType = findViewById(R.id.type);
        submit = findViewById(R.id.submitBtn);

        userRef = fAuth.getUid();
        email = fAuth.getCurrentUser().getEmail();

        takePhotoBtn = findViewById(R.id.takePhoto);
        uploadPhotoBtn = findViewById(R.id.uploadBtn);


        autoCompleteTextView = findViewById(R.id.location);
        autoCompleteTextView.setAdapter(new GooglePlacesAutoSuggestAdapter(VenueActivity.this, android.R.layout.simple_list_item_1));

        image = findViewById(R.id.imageView);

        String[] venueTypes = { "Function Room", "Pub", "Club"};


        Spinner spin = findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner, venueTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);

        /**
         * description
         * email - from the sign up not database /
         * location /
         * name - this is the venue name /
         * phone-number /
         * user-ref - this is the users uuid /
         * venue-type
         */
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                // Whatever you want to happen when the first item gets selected
                type = "Funtion Room";
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                type = "pub";
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                type = "club";
                break;
        }

        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + type);
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO - Custom Code
    }

    public void submitBtnOnClick(View view) {
        String desc = description.getText().toString();
        String venueName = name.getText().toString();
        String venueRating = "-1";
        String venueAddressTextView = autoCompleteTextView.getText().toString();
        Address venueAddress = getAddress();
        ImageView defImg = new ImageView(this);
        defImg.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);

        if (TextUtils.isEmpty(desc)) {
            description.setError("Please Enter A Venue Description!");
            return;
        }
        if (TextUtils.isEmpty(venueName)) {
            name.setError("Please Enter A Venue Name!");
            return;
        }
        if(TextUtils.isEmpty(venueAddressTextView))
        {
            autoCompleteTextView.setError("Please Enter Your Venue Address");
            return;
        }
        if(venueAddress == null)
        {
            autoCompleteTextView.setError("Please Enter A Valid Address");
            return;
        }



        fStore.collection("users").document(userRef).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists())
                        {
                            Log.d(TAG, "Document exists!");
                            phoneNumber = document.get("phone-number").toString();

                            Map<String, Object> venues = new HashMap<>();
                            venues.put("name", venueName);
                            venues.put("location", venueAddress.getSubAdminArea());
                            venues.put("description", desc);
                            venues.put("user-ref", userRef);
                            venues.put("venue-type", type);
                            venues.put("email-address", email);
                            venues.put("phone-number", phoneNumber);
                            venues.put("rating", venueRating);
                            venues.put("latitude", venueAddress.getLatitude());
                            venues.put("longitude", venueAddress.getLongitude());
                            fStore.collection("venues")
                                    .add(venues)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                            StorageReference sRef = fStorage.getReference()
                                                    .child("/images/venues/" + documentReference.getId() + ".jpg");
                                            UploadTask uploadTask = sRef.putBytes(imageToByteArray(image.getDrawable()));
                                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Log.d("STORAGE SUCCEEDED", taskSnapshot.getMetadata().toString());
                                                    startActivity(new Intent(getApplicationContext(), NavBarActivity.class));
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("STORAGE FAILED", e.toString());
                                                }
                                            });

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });
                        }
                        else
                        {
                            Log.d(TAG, "Document doesn't exists!");
                        }
                    }
                }

        });
    }
    public byte[] imageToByteArray(Drawable image)
    {
        Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public void uploadBtnOnClick(View view) {
        ImageRequestHandler.getGalleryImage(view);

    }

    private Address getAddress()
    {
        String venueName = autoCompleteTextView.getText().toString();
        geocoder = new Geocoder(this, Locale.getDefault());

        try
        {
            List<Address> addressList = geocoder.getFromLocationName(venueName, 1);

            if(addressList.size() > 0)
            {
                Address address = addressList.get(0);
                return address;
            }
            else
            {
                return null;
            }
        }
        catch(IOException io)
        {
            Log.d(TAG, io.toString());
            return null;
        }
    }

    public void takeBtnOnClick(View view) {
        ImageRequestHandler.getCameraImage(view);
    }

    /**
     * handles activity results
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        image = ImageRequestHandler.handleResponse(requestCode, resultCode, data, image);
        chosenPic = image.getDrawable();
    }

    @Override
    public void onBackPressed()
    {
        Toast.makeText(this, "Please fill in your credentials!", Toast.LENGTH_SHORT).show();
    }
}
