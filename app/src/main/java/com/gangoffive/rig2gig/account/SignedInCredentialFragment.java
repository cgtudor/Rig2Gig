package com.gangoffive.rig2gig.account;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.advert.index.VenueAdapter;
import com.gangoffive.rig2gig.advert.index.VenueListing;
import com.gangoffive.rig2gig.advert.index.ViewVenuesFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.identityconnectors.common.security.GuardedString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SignedInCredentialFragment extends Fragment implements View.OnClickListener
{
    private String TAG = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";

    SwipeRefreshLayout swipeLayout;

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference colRef;
    private List<DocumentSnapshot> documentSnapshots;

    private RecyclerView recyclerView;
    private VenueAdapter adapter;

    private ArrayList<VenueListing> venueListings;

    public static Button btn;
    String userId;

    EditText cFirstName, cLastName, cUsername, cPhoneNumber, rEmailAddress, rConfirmEmail, rPassword, rConfirmPassword, invis;
    /**
     * Upon creation of the ViewVenuesFragment, create the fragment_view_venues layout.
     * @param inflater The inflater is used to read the passed xml file.
     * @param container The views base class.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @return Returns a View of the fragment_upgrade_to_musicians layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewVenuesFragment()).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        final View v = inflater.inflate(R.layout.fragment_signedincredential, container, false);
        btn = v.findViewById(R.id.submitBtn);
        btn.setOnClickListener(this);

        btn.setVisibility(View.INVISIBLE);
        cFirstName = v.findViewById(R.id.nameFirst);
        cLastName = v.findViewById(R.id.location);
        cUsername = v.findViewById(R.id.venue_description_final);
        cPhoneNumber = v.findViewById(R.id.cPhoneNumber);

        rEmailAddress = v.findViewById(R.id.emailReset);
        rConfirmEmail = v.findViewById(R.id.registerConfirmEmail);
        rPassword = v.findViewById(R.id.registerPassword);
        rConfirmPassword = v.findViewById(R.id.registerConfirmPassword);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Creating the initial authentication account for the user with the base information provided.
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitBtn:
                String firstName = cFirstName.getText().toString();
                String lastName = cLastName.getText().toString();
                String username = cUsername.getText().toString();
                String phoneNumber = cPhoneNumber.getText().toString();


                CollectionReference emailRef = fStore.collection("users");
                Query emailQuery = emailRef.whereEqualTo("indexUsername", username.toLowerCase());
                emailQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty())
                            {
                                Toast.makeText(getActivity(), "Account is being created please sit tight!", Toast.LENGTH_SHORT).show();
                                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@2Made it here");
                                //startActivity(new Intent(getApplicationContext(),CredentialActivity.class));
                                userId = fAuth.getUid();
                                DocumentReference documentReference = fStore.collection("users").document(userId);
                                Map<String, Object> user = new HashMap<>();
                                user.put("given-name", firstName);
                                user.put("family-name", lastName);
                                user.put("username", username);
                                user.put("indexUsername", username.toLowerCase());
                                user.put("phone-number", phoneNumber);
                                user.put("user-type", AccountPurposeActivity.userType);
                                documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Toast.makeText(CredentialActivity.this, "Information Added", Toast.LENGTH_SHORT).show();
                                        System.out.println("Added User@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                                        if (AccountPurposeActivity.userType == "Venue") {
                                            CreateVenueFragment.venueBtn.performClick();
                                        } else {
                                            CreateMusicianFragment.btn.performClick();
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Toast.makeText(CredentialActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                        String uuid = fAuth.getUid();
                                        System.out.println("=========================" + uuid);
                                    }
                                });
                            }
                            else
                            {
                                for (DocumentSnapshot document : task.getResult()) {
                                    if (document.exists()) {
                                        String userName = document.getString("username");
                                        Log.d(TAG, "username already exists");
                                        cUsername.setError(username + " Already Exists!");
                                        return;
                                    }
                                }
                            }
                        }
                    }
                });
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    public boolean onBackPressed() {
        return true;
    }

    /**
     * Checking the SDK level of the device.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "get successful with data123213213");
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        if (Build.VERSION.SDK_INT >= 26) {
//            ft.setReorderingAllowed(false);
//        }
//        ft.detach(SignedInCredentialFragment.this).attach(SignedInCredentialFragment.this).commit();
    }
}
