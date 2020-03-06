package com.gangoffive.rig2gig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.twitter.sdk.android.core.models.ImageValue;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VenueActivity extends AppCompatActivity {

    private final String TAG = "@@@@@@@@@@@@@@@@";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;
    EditText description, location, name, venueType;
    Button submit;

    String email, userRef, phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        description = findViewById(R.id.description);
        location = findViewById(R.id.location);
        name = findViewById(R.id.name);
        venueType = findViewById(R.id.type);
        submit = findViewById(R.id.submitBtn);

        userRef = fAuth.getUid();
        email = fAuth.getCurrentUser().getEmail();


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

    public void submitBtnOnClick(View view) {
        String desc = description.getText().toString();
        String loc = location.getText().toString();
        String venueName = name.getText().toString();
        String type = venueType.getText().toString();
        String venueRating = "-1";
        ImageView defImg = new ImageView(this);
        defImg.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);


        fStore.collection("users").document(userRef).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists())
                        {
                            Log.d(TAG, "Document exists!");
                            phoneNumber = document.get("phone").toString();

                            Map<String, Object> venues = new HashMap<>();
                            venues.put("name", venueName);
                            venues.put("location", loc);
                            venues.put("description", desc);
                            venues.put("user-ref", userRef);
                            venues.put("venue-type", type);
                            venues.put("email", email);
                            venues.put("phone-number", phoneNumber);
                            venues.put("rating", venueRating);
                            fStore.collection("venues")
                                    .add(venues)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                            StorageReference sRef = fStorage.getReference()
                                                    .child("/images/venues/" + documentReference.getId() + ".jpg");
                                            UploadTask uploadTask = sRef.putBytes(imageToByteArray(defImg.getDrawable()));
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
    public byte[] imageToByteArray(Drawable image)
    {
        Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}
