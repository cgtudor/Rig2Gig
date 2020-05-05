package com.gangoffive.rig2gig.account;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.ui.TabbedView.SignedInVenuePagerAdapter;
import com.gangoffive.rig2gig.ui.TabbedView.VenuePagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.identityconnectors.common.security.GuardedString;

import java.util.concurrent.atomic.AtomicBoolean;

public class SignedInTabbedVenueActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;

    Button confirm, back, submit, createListing;

    EditText cFirstName, cLastName, cPhoneNumber, username, venueAddressTextView, Vname, description;
    TextView invis;
    String test2;

    ImageView image;

    private static final String TAG = "======================";

    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_signedincredential,
            R.layout.fragment_create_venue};

    /**
     * Text watched used to check for input or not from the user this is used to disable the confirm button
     * if no information has been entered.
     */
    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String usrname = username.getText().toString().trim();
            String firstName = cFirstName.getText().toString().trim();
            String lastName = cLastName.getText().toString().trim();
            String phoneNo = cPhoneNumber.getText().toString().trim();
            String venueName = Vname.getText().toString().trim();
            String venueAdd = venueAddressTextView.getText().toString().trim();
            String venueDesc = description.getText().toString().trim();
            image = findViewById(R.id.imageView);

            if (!usrname.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !phoneNo.isEmpty() && !venueName.isEmpty()
                    && !venueAdd.isEmpty() && !venueDesc.isEmpty())
            {
                //createListing.setBackgroundColor(Color.parseColor("#12c2e9"));
                createListing.setTextColor(Color.parseColor("#FFFFFF"));
            }
            else
            {
                //createListing.setTextColor(Color.parseColor("#a6a6a6"));
                createListing.setTextColor(Color.parseColor("#800000"));
                //createListing.setBackgroundColor(Color.parseColor("#a6a6a6"));

            }
        }

        //String usernameInput = editTextUsername.getText().toString().trim();
        //            String passwordInput = editTextPassword.getText().toString().trim();
        //
        //            buttonConfirm.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty());

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * Gets an instance of Firebase, and calles the adapters for the tabbed views to load the needed
     * Fragments.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        tabTitles = new int[]{R.string.personalInformation, R.string.venueInformation};

        SignedInVenuePagerAdapter venuePagerAdapter = new SignedInVenuePagerAdapter
                (this, getSupportFragmentManager(), tabTitles, fragments);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(venuePagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        submit = findViewById(R.id.submitBtn);
        submit = findViewById(R.id.createListing);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Create Venue Account");
        /*Setting the support action bar to the newly created toolbar*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        createListing = findViewById(R.id.createListing);
        /**
         * Has no use other than to waste time an let the findViewById's to load to enable the text watcher
         */
        String UUID = "9bN1BWYfCJQ2Pmcqzhnr87IIbj13";
        DocumentReference doc = fStore.collection("users").document(UUID);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    initialiseTextViews();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                initialiseTextViews();
            }
        });

    }

    /**
     * Initialising text views
     */
    private void initialiseTextViews() {
        username = findViewById(R.id.venue_description_final);
        username.addTextChangedListener(loginTextWatcher);
        cFirstName = findViewById(R.id.nameFirst);
        cFirstName.addTextChangedListener(loginTextWatcher);
        cLastName = findViewById(R.id.location);
        cLastName.addTextChangedListener(loginTextWatcher);
        cPhoneNumber = findViewById(R.id.cPhoneNumber);
        cPhoneNumber.addTextChangedListener(loginTextWatcher);
        Vname = findViewById(R.id.venue_name);
        Vname.addTextChangedListener(loginTextWatcher);
        venueAddressTextView = findViewById(R.id.venue_location);
        venueAddressTextView.addTextChangedListener(loginTextWatcher);
        description = findViewById(R.id.venue_description);
        description.addTextChangedListener(loginTextWatcher);
    }

    /**
     * Checking that all inputs are not null if so providing an error to the user so that they need
     * filling in before proceeding.
     * @param view
     */
    public void confirmOnClick(View view)
    {
        username = findViewById(R.id.venue_description_final);
        String usrname = username.getText().toString();
        if (TextUtils.isEmpty(usrname)) {
            username.setError("Username is required!");
            return;
        }
        cFirstName = findViewById(R.id.nameFirst);
        String firstName = cFirstName.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            cFirstName.setError("First name is required!");
            return;
        }
        cLastName = findViewById(R.id.location);
        String lastName = cLastName.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            cLastName.setError("Last name is required!");
            return;
        }
        cPhoneNumber = findViewById(R.id.cPhoneNumber);
        String phoneNumber = cPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            cPhoneNumber.setError("Phone number is required!");
            return;
        }

        Vname = findViewById(R.id.venue_name);
        String venueName = Vname.getText().toString();
        if (TextUtils.isEmpty(venueName)) {
            Vname.setError("Please set venue name!");
            return;
        }
        venueAddressTextView = findViewById(R.id.venue_location);
        String venuaAddress = venueAddressTextView.getText().toString();
        if (TextUtils.isEmpty(venuaAddress)) {
            venueAddressTextView.setError("Please set a location!");
            return;
        }
        description = findViewById(R.id.venue_description);
        String desc = description.getText().toString();
        if (TextUtils.isEmpty(desc)) {
            description.setError("Please Enter A Venue Description!");
            return;
        }

        image = findViewById(R.id.imageViewVenue);
        if (image.getDrawable() == null)
        {
            Toast.makeText(getApplicationContext(),"Please choose and image!", Toast.LENGTH_SHORT).show();
            return;
        }


        SignedInCredentialFragment.btn.performClick();
        System.out.println("clicked");

        //toast here for account being created
    }

    /**
     * Going back when cancel is pressed.
     * @param view
     */
    public void cancelOnClick(View view)
    {
        onBackPressed();
    }

    /**
     * Going to the LoginActivity when back is pressed.
     */
    public void onBackPressed() {
        Intent backToMain = new Intent(this,
                LoginActivity.class);
        startActivity(backToMain);
    }

    /**
     * Finishing the activity when home is selected.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
