package com.gangoffive.rig2gig.account;

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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gangoffive.rig2gig.advert.management.GooglePlacesAutoSuggestAdapter;
import com.gangoffive.rig2gig.utils.ImageRequestHandler;
import com.gangoffive.rig2gig.navbar.NavBarActivity;
import com.gangoffive.rig2gig.R;
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

public class CreateMusicianAccountActivity extends AppCompatActivity {

    private final String TAG = "@@@@@@@@@@@@@@@@";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;
    EditText distance, location, name, genre;
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
        setContentView(R.layout.activity_create_musician_account);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        distance = findViewById(R.id.venue_description_final);
        name = findViewById(R.id.venue_name_final);
        genre = findViewById(R.id.genre);

        submit = findViewById(R.id.submitBtn);

        userRef = fAuth.getUid();
        email = fAuth.getCurrentUser().getEmail();

        takePhotoBtn = findViewById(R.id.takePhoto);
        uploadPhotoBtn = findViewById(R.id.uploadBtn);

        autoCompleteTextView = findViewById(R.id.location);
        autoCompleteTextView.setAdapter(new GooglePlacesAutoSuggestAdapter(CreateMusicianAccountActivity.this, android.R.layout.simple_list_item_1));

        image = findViewById(R.id.imageView);

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

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO - Custom Code
    }

    public void submitBtnOnClick(View view)
    {
        String loc = location.getText().toString();
        String musicianName = name.getText().toString();
        String musicianDistance = distance.getText().toString();
        String musicianAddressTextView = autoCompleteTextView.getText().toString();
        Address musicianAddress = getAddress();
        String genres = genre.getText().toString();
        ImageView defImg = new ImageView(this);
        defImg.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);

        if (TextUtils.isEmpty(musicianName)) {
            name.setError("Please Enter A Musician Name!");
            return;
        }
        if (TextUtils.isEmpty(musicianDistance)) {
            distance.setError("Please Set A Distance!");
            return;
        }
        if(TextUtils.isEmpty(musicianAddressTextView))
        {
            autoCompleteTextView.setError("Please Enter Your Venue Address");
            return;
        }
        if(musicianAddress == null)
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

                        Map<String, Object> musicians = new HashMap<>();
                        musicians.put("name", musicianName);
                        musicians.put("index-name", musicianName.toLowerCase());
                        musicians.put("location", checkLocality(musicianAddress));
                        musicians.put("user-ref", userRef);
                        musicians.put("email-address", email);
                        musicians.put("phone-number", phoneNumber);
                        musicians.put("genres", genres);
                        musicians.put("distance", musicianDistance);
                        musicians.put("latitude", musicianAddress.getLatitude());
                        musicians.put("longitude", musicianAddress.getLongitude());
                        musicians.put("musician-rating", "N/A");
                        musicians.put("musician-rating-count", 0);
                        musicians.put("musician-rating-total", 0);
                        musicians.put("performer-rating", "N/A");
                        musicians.put("performer-rating-count", 0);
                        musicians.put("performer-rating-total", 0);

                        fStore.collection("musicians")
                                .add(musicians)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                        StorageReference sRef = fStorage.getReference()
                                                .child("/images/musicians/" + documentReference.getId() + ".jpg");
                                        UploadTask uploadTask = sRef.putBytes(imageToByteArray(image.getDrawable()));
                                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Log.d("STORAGE SUCCEEDED", taskSnapshot.getMetadata().toString());
                                                startActivity(new Intent(getApplicationContext(), NavBarActivity.class));
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

    private String checkLocality(Address musicianAddress)
    {
        if(musicianAddress.getLocality() != null)
        {
            return musicianAddress.getLocality();
        }
        else if(musicianAddress.getSubLocality() != null)
        {
            return musicianAddress.getSubLocality();
        }
        else
        {
            return musicianAddress.getPostalCode();
        }
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
        String musicianName = autoCompleteTextView.getText().toString();
        geocoder = new Geocoder(this, Locale.getDefault());

        try
        {
            List<Address> addressList = geocoder.getFromLocationName(musicianName, 1);

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
