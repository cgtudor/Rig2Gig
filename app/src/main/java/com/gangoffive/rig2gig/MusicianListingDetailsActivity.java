package com.gangoffive.rig2gig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MusicianListingDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musician_listing_details);

        /*Setting the support action bar to the newly created toolbar*/
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView musicianPhoto = findViewById(R.id.musicianPhoto);
        final TextView musicianName = findViewById(R.id.musicianName);
        final TextView position = findViewById(R.id.position);
        final TextView description = findViewById(R.id.description);
        final TextView location = findViewById(R.id.position);
        final TextView distance = findViewById(R.id.distance);

        /*Used to get the id of the listing from the previous activity*/
        String mID = getIntent().getStringExtra("EXTRA_MUSICIAN_LISTING_ID");

        /*Firestore & Cloud Storage initialization*/
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        /*Finding the listing by its ID in the "musician-listings" subfolder*/
        DocumentReference musicianListing = db.collection("musician-listings").document(mID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        musicianListing.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        /*Find the musician reference by looking for the musician ID in the "musicians" subfolder*/
                        DocumentReference musician = db.collection("musicians").document(document.get("musician-ref").toString());

                        musician.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());
                                        musicianName.setText(document.get("name").toString());
                                        distance.setText("Distance willing to travel: " + document.get("distance").toString() + " miles");
                                        location.setText(document.get("location").toString());

                                        getSupportActionBar().setTitle(musicianName.getText().toString());

                                    } else {
                                        Log.d("FIRESTORE", "No such document");
                                    }
                                } else {
                                    Log.d("FIRESTORE", "get failed with ", task.getException());
                                }
                            }
                        });
                        description.setText(document.get("description").toString());

                        /*Getting the positions in a single string and then eliminating the array brackets with substring*/
                        String positions = document.get("position").toString();
                        position.setText("Preferred positions: " + positions.substring(1, positions.length() - 1));
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
        StorageReference musicianPic = storage.getReference().child("/images/musician-listings/" + mID + ".jpg");

        /*Using Glide to load the picture from the reference directly into the ImageView*/
        GlideApp.with(this /* context */)
                .load(musicianPic)
                .into(musicianPhoto);
    }
    /**
     * Overriding the up navigation to call onBackPressed
     * @return true
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Checks the user-type. Redirects to console if it is a musician or to the previous activity/fragment if not.
     */
    @Override
    public void onBackPressed() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(FirebaseAuth.getInstance().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot document = task.getResult();
                            if(document.exists())
                            {
                                String accType = document.get("user-type").toString();
                                if(accType.equals("Musician"))
                                {
                                    Intent intent = new Intent(MusicianListingDetailsActivity.this, NavBarActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    MusicianListingDetailsActivity.this.onBackPressed();
                                    finish();
                                }
                            }
                            else
                            {
                                Log.d("FIRESTORE", "No such document");
                            }
                        }
                        else
                        {
                            Log.d("FIRESTORE", "get failed with ", task.getException());
                        }
                    }
                });
    }
}
