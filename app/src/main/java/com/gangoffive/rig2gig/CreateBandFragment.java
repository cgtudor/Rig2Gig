package com.gangoffive.rig2gig;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CreateBandFragment extends Fragment implements View.OnClickListener {
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseStorage fStorage = FirebaseStorage.getInstance();

    public static Button btn;

    EditText cBandName, cBandLocation, cBandDistance, cBandGenres, cBandEmail, cBandPhoneNumber;

    String musicianRef;
    public static String bandRef;
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

        final View v = inflater.inflate(R.layout.fragment_create_band, container, false);

        btn = v.findViewById(R.id.createBandBtn);
        btn.setOnClickListener(this);
        btn.setVisibility(View.INVISIBLE);

        cBandName = v.findViewById(R.id.BandName);
        cBandLocation = v.findViewById(R.id.bandLocation);
        cBandDistance = v.findViewById(R.id.bandDistance);
        cBandGenres = v.findViewById(R.id.bandGenres);
        cBandEmail = v.findViewById(R.id.bandEmail);
        cBandPhoneNumber = v.findViewById(R.id.bandPhoneNumber);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.createBandBtn:
                final String bandName = cBandName.getText().toString();
                final String bandLocation = cBandLocation.getText().toString().trim();
                final String bandDistance = cBandDistance.getText().toString().trim();
                final String bandGenres = cBandGenres.getText().toString().trim();
                final String bandEmail = cBandEmail.getText().toString().trim();
                final String bandPhoneNumber = cBandPhoneNumber.getText().toString().trim();

                if (TextUtils.isEmpty(bandName)){
                    cBandName.setError("Band Name Is Required!");
                }
                if (TextUtils.isEmpty(bandLocation)){
                    cBandLocation.setError("Band Location Is Required!");
                }
                if (TextUtils.isEmpty(bandDistance)){
                    cBandDistance.setError("Distance Is Required!");
                }
                if (TextUtils.isEmpty(bandGenres)){
                    cBandGenres.setError("Genres Is Required!");
                }
                if (TextUtils.isEmpty(bandEmail)){
                    cBandEmail.setError("Band Email Is Required!");
                }
                if (TextUtils.isEmpty(bandPhoneNumber)){
                    cBandPhoneNumber.setError("Band Phone Number Is Required!");
                }

                DocumentReference docRef = fStore.collection("users").document(fAuth.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                musicianRef = document.getString("musicianRef");
                                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " + musicianRef);
                                Map<String, Object> band = new HashMap<>();
                                band.put("name", bandName);
                                band.put("location", bandLocation);
                                band.put("distance", bandDistance);
                                band.put("genres", bandGenres);
                                band.put("email", bandEmail);
                                band.put("phone-number", bandPhoneNumber);
                                band.put("musicianRef", musicianRef);

                                fStore.collection("bands")
                                        .add(band)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ going to band image");
                                                bandRef = documentReference.toString();
                                                BandImageFragment.submitBtn.performClick();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
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
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }
}
