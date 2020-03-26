package com.gangoffive.rig2gig;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.gangoffive.rig2gig.ui.TabbedView.MusicianPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class TabbedMusicianActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;

    Button test, back;

    private static final String TAG = "======================";

    EditText cFirstName, cLastName, cUsername, cPhoneNumber, rEmailAddress, rConfirmEmail, rPassword, rConfirmPassword, username, musicianName, musicianLocation, musicianDistance, musicianGenre, invis;
    ImageView image;

    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_credential,
            R.layout.fragment_create_musician};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        tabTitles = new int[]{R.string.personalInformation, R.string.musicianInformation};

        MusicianPagerAdapter musicianPagerAdapter = new MusicianPagerAdapter
                (this, getSupportFragmentManager(), tabTitles, fragments);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(musicianPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        test = findViewById(R.id.submitBtn);
    }

    public void confirmOnClick(View view)
    {
        rEmailAddress = findViewById(R.id.emailReset);
        String email = rEmailAddress.getText().toString();
        if (TextUtils.isEmpty(email)) {
            rEmailAddress.setError("Email is required!");
            return;
        }
        rConfirmEmail = findViewById(R.id.registerConfirmEmail);
        String confirmEmail = rConfirmEmail.getText().toString();
        if (TextUtils.isEmpty(confirmEmail)) {
            rConfirmEmail.setError("Confirm email is required!");
            return;
        }
        if (!confirmEmail.matches(email)) {
            rConfirmEmail.setError("Email doesn't match!");
            return;
        }
        rPassword = findViewById(R.id.registerPassword);
        String password = rPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            rPassword.setError("Password is required!");
            return;
        }
        rConfirmPassword = findViewById(R.id.registerConfirmPassword);
        String confirmPassword = rConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(confirmPassword)) {
            rConfirmPassword.setError("Confirm password is required!");
            return;
        }
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

        musicianName = findViewById(R.id.firstName);
        String name = musicianName.getText().toString();
        if (TextUtils.isEmpty(name)){
            musicianName.setError("Musician name is required!");
            return;
        }
        musicianLocation = findViewById(R.id.location2);
        String loc = musicianLocation.getText().toString();
        if (TextUtils.isEmpty(loc)) {
            musicianLocation.setError("Please set a location!");
            return;
        }
        musicianDistance = findViewById(R.id.firstName3);
        String dist = musicianDistance.getText().toString();
        if (TextUtils.isEmpty(dist)){
            musicianDistance.setError("Distance is required!");
            return;
        }
        musicianGenre = findViewById(R.id.firstName5);
        String genre = musicianGenre.getText().toString();
        if (TextUtils.isEmpty(genre)) {
            musicianGenre.setError("Genre is required!");
            return;
        }

        image = findViewById(R.id.imageView);
        invis = findViewById(R.id.invis);
        if (image.getDrawable() == null)
        {
            invis.setError("Please select an image");
            return;
        }

        CredentialFragment.btn.performClick();
        System.out.println("clicked");
    }

    public void cancelOnClick(View view)
    {
        onBackPressed();
    }

    public void onBackPressed() {
        Intent backToMain = new Intent(this,
                LoginActivity.class);
        startActivity(backToMain);
    }
}