package com.gangoffive.rig2gig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BandListingDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_listing_details);

        final ImageView bandPhoto = findViewById(R.id.bandPhoto);
        final TextView bandName = findViewById(R.id.bandName);
        final TextView rating = findViewById(R.id.rating);
        final TextView genres = findViewById(R.id.genres);
        final TextView position = findViewById(R.id.position);
        final TextView description = findViewById(R.id.description);
        final TextView location = findViewById(R.id.location);

        /*Used to get the id of the listing from the previous activity*/
        /*        String bID = getIntent().getStringExtra("EXTRA_BAND_LISTING_ID");*/

        String bID = "9tLqZo2UCWaq7uyC3crP";

        /*Firestore & Cloud Storage initialization*/
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        /*Finding the listing by its ID in the "band-listings" subfolder*/
        DocumentReference bandListing = db.collection("band-listings").document(bID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        bandListing.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        /*Find the band reference by looking for the band ID in the "bands" subfolder*/
                        DocumentReference band = db.collection("bands").document(document.get("band-ref").toString());

                        band.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());
                                        bandName.setText(document.get("name").toString());
                                        genres.setText("Genres: " + document.get("genres").toString());
                                        rating.setText("Rating: " + Integer.parseInt(document.get("rating").toString()) + "/5");
                                        location.setText(document.get("location").toString());

                                    } else {
                                        Log.d("FIRESTORE", "No such document");
                                    }
                                } else {
                                    Log.d("FIRESTORE", "get failed with ", task.getException());
                                }
                            }
                        });
                        description.setText(document.get("description").toString());
                        position.setText("Looking for: " + document.get("position").toString());
                    } else {
                        Log.d("FIRESTORE", "No such document");
                    }
                } else {
                    Log.d("FIRESTORE", "get failed with ", task.getException());
                }
            }
        });

        //Temp wait for pic to upload
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*Find reference for the photo associated with the listing inside the according subtree*/
        StorageReference bandPic = storage.getReference().child("/images/band-listings/" + bID + ".jpg");

        /*Using Glide to load the picture from the reference directly into the ImageView*/
        GlideApp.with(this /* context */)
                .load(bandPic)
                .into(bandPhoto);
    }
}
