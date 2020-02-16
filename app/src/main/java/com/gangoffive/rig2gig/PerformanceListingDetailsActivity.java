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

public class PerformanceListingDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_listing_details);
        final ImageView bandPhoto = findViewById(R.id.bandPhoto);
        final TextView bandName = findViewById(R.id.bandName);
        final TextView genre = findViewById(R.id.genre);
        final TextView rating = findViewById(R.id.rating);
        final TextView price = findViewById(R.id.price);
        final TextView location = findViewById(R.id.location);
        final TextView distance = findViewById(R.id.distance);

        /*Used to get the id of the listing from the previous activity*/
        String pID = getIntent().getStringExtra("EXTRA_PERFORMANCE_LISTING_ID");

        /*temp for testing*/
        pID = "w0Bnuzff4aGEV7AEhjHQ";

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        DocumentReference performanceListing = db.collection("performer-listings").document(pID);
        performanceListing.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());
                        DocumentReference band = db.collection("bands").document(document.get("bandRef").toString());
                        band.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());
                                        bandName.setText(document.get("name").toString());
                                        rating.setText("Rating: " + document.get("rating").toString() + "/5");
                                        location.setText(document.get("location").toString());

                                    } else {
                                        Log.d("FIRESTORE", "No such document");
                                    }
                                } else {
                                    Log.d("FIRESTORE", "get failed with ", task.getException());
                                }
                            }
                        });
                        genre.setText(document.get("genres").toString());
                        distance.setText("Distance willing to travel: " + document.get("distance").toString() + " miles");
                        price.setText("£" + document.get("chargePerHour").toString() + " per hour");
                    } else {
                        Log.d("FIRESTORE", "No such document");
                    }
                } else {
                    Log.d("FIRESTORE", "get failed with ", task.getException());
                }
            }
        });
        StorageReference bandPic = storage.getReference().child("/images/performance-listings/" + pID + ".jpg");
        GlideApp.with(this /* context */)
                .load(bandPic)
                .into(bandPhoto);
    }
}
