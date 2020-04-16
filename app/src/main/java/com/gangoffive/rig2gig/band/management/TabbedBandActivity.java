package com.gangoffive.rig2gig.band.management;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.ui.TabbedView.BandPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class TabbedBandActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;

    public static Button  faderBtn;
    Button test, back;

    TextView genre, fader;
    EditText name, location, distance, email, number, invis;
    ImageView image;
    String bandName;

    Button createListing;

    private static final String TAG = "======================";
    static String musicianID;

    private int[] tabTitles;
    private int[] fragments = {R.layout.activity_create_band,
            R.layout.fragment_band_image};

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String nameInput = name.getText().toString().trim();
            String locationInput = location.getText().toString().trim();
            String distanceInput = distance.getText().toString().trim();
            String genreInput = genre.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String numberInput = number.getText().toString().trim();
            image = findViewById(R.id.imageView);

            if (!nameInput.isEmpty() && !locationInput.isEmpty() && !distanceInput.isEmpty() && !genreInput.isEmpty() && !emailInput.isEmpty() && !numberInput.isEmpty())
            {
                createListing.setBackgroundColor(Color.parseColor("#12c2e9"));
            }
            else
            {
                createListing.setBackgroundColor(Color.parseColor("#a6a6a6"));

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_layout);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        musicianID = getIntent().getStringExtra("EXTRA_MUSICIAN_ID");

        tabTitles = new int[]{R.string.bandInformation, R.string.bandImage};

        faderBtn = findViewById(R.id.fakeFaderBtn);
        faderBtn.setVisibility(View.INVISIBLE);
        fader = findViewById(R.id.fader1);
        test = findViewById(R.id.submitBtn);
        back = findViewById(R.id.cancel);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                TabbedBandActivity.super.onBackPressed();
            }
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Create A Band");
        /*Setting the support action bar to the newly created toolbar*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BandPagerAdapter bandPagerAdapter = new BandPagerAdapter
                (this, getSupportFragmentManager(), tabTitles, fragments);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(bandPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        createListing = findViewById(R.id.createListing);
        /**
         * Has no use other than to waste time an let the findViewById's to load to enable the text watcher
         */
        DocumentReference doc = fStore.collection("users").document(fAuth.getUid());
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

    private void initialiseTextViews() {
        name = findViewById(R.id.BandName);
        name.addTextChangedListener(loginTextWatcher);
        location = findViewById(R.id.location3);
        location.addTextChangedListener(loginTextWatcher);
        distance = findViewById(R.id.bandDistance);
        distance.addTextChangedListener(loginTextWatcher);
        genre = findViewById(R.id.bandGenres);
        genre.addTextChangedListener(loginTextWatcher);
        email = findViewById(R.id.bandEmail);
        email.addTextChangedListener(loginTextWatcher);
        number = findViewById(R.id.bandPhoneNumber);
        number.addTextChangedListener(loginTextWatcher);
    }

    public void confirmOnClick(View view)
    {
        name = findViewById(R.id.BandName);
        String bandName = name.getText().toString();
        if (TextUtils.isEmpty(bandName)) {
            name.setError("Band name is required!");
            return;
        }
        location = findViewById(R.id.location3);
        String bandLocation = location.getText().toString();
        if (TextUtils.isEmpty(bandLocation)) {
            location.setError("Band location is required!");
            return;
        }
        distance = findViewById(R.id.bandDistance);
        String bandDistance = distance.getText().toString();
        if (TextUtils.isEmpty(bandDistance)) {
            distance.setError("Distance to travel is required!");
            return;
        }
        genre = findViewById(R.id.bandGenres);
        String bandGenre = genre.getText().toString();
        if (TextUtils.isEmpty(bandGenre)) {
            genre.setError("Distance to travel is required!");
            return;
        }
        email = findViewById(R.id.bandEmail);
        String bandEmail = email.getText().toString();
        if (TextUtils.isEmpty(bandEmail)) {
            email.setError("Band email is required!");
            return;
        }
        number = findViewById(R.id.bandPhoneNumber);
        String bandPhonenumber = number.getText().toString();
        if (TextUtils.isEmpty(bandPhonenumber)) {
            number.setError("Band email is required!");
            return;
        }

        image = findViewById(R.id.imageView);
        if (image.getDrawable() == null)
        {
            Toast.makeText(getApplicationContext(),"Please choose and image!", Toast.LENGTH_SHORT).show();
            return;
        }

        CreateBandFragment.btn.performClick();
        System.out.println("clicked");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void fakeFaderOnClick(View view) {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(TabbedBandActivity.this,R.color.darkerMain));
        fader.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        fader.setVisibility(View.GONE);
    }

}