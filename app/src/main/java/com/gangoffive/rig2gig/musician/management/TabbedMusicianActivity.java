package com.gangoffive.rig2gig.musician.management;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.account.CredentialFragment;
import com.gangoffive.rig2gig.account.LoginActivity;
import com.gangoffive.rig2gig.band.management.TabbedBandActivity;
import com.gangoffive.rig2gig.ui.TabbedView.MusicianPagerAdapter;
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

public class TabbedMusicianActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;

    Button test, createListing;

    private static final String TAG = "======================";

    public static Button  faderBtn;

    TextView musicianGenre, fader;
    EditText cFirstName, cLastName, cUsername, cPhoneNumber, rEmailAddress, rConfirmEmail, rPassword, rConfirmPassword, username, musicianName, musicianLocation, musicianDistance, invis;
    ImageView image;

    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_credential,
            R.layout.fragment_create_musician};

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String emailAddress = rEmailAddress.getText().toString().trim();
            String confirmEmail = rConfirmEmail.getText().toString().trim();
            String password = rPassword.getText().toString().trim();
            String confirmPassword = rConfirmPassword.getText().toString().trim();
            String usrname = username.getText().toString().trim();
            String firstName = cFirstName.getText().toString().trim();
            String lastName = cLastName.getText().toString().trim();
            String phoneNo = cPhoneNumber.getText().toString().trim();
            String muscName = musicianName.getText().toString().trim();
            String muscLoc = musicianLocation.getText().toString().trim();
            String muscDist = musicianDistance.getText().toString().trim();
            String muscGenre = musicianGenre.getText().toString().trim();
            image = findViewById(R.id.imageView);

            if (!emailAddress.isEmpty() && !confirmEmail.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()
                    && !usrname.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !phoneNo.isEmpty() && !muscName.isEmpty()
                    && !muscLoc.isEmpty() && !muscDist.isEmpty() && !muscGenre.isEmpty())
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

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Create Musician Account");
        /*Setting the support action bar to the newly created toolbar*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        faderBtn = findViewById(R.id.faderBtn2);
        fader = findViewById(R.id.fader2);

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

    private void initialiseTextViews() {
        rEmailAddress = findViewById(R.id.emailReset);
        rEmailAddress.addTextChangedListener(loginTextWatcher);
        rConfirmEmail = findViewById(R.id.registerConfirmEmail);
        rConfirmEmail.addTextChangedListener(loginTextWatcher);
        rPassword = findViewById(R.id.registerPassword);
        rPassword.addTextChangedListener(loginTextWatcher);
        rConfirmPassword = findViewById(R.id.registerConfirmPassword);
        rConfirmPassword.addTextChangedListener(loginTextWatcher);
        username = findViewById(R.id.venue_description_final);
        username.addTextChangedListener(loginTextWatcher);
        cFirstName = findViewById(R.id.nameFirst);
        cFirstName.addTextChangedListener(loginTextWatcher);
        cLastName = findViewById(R.id.location);
        cLastName.addTextChangedListener(loginTextWatcher);
        cPhoneNumber = findViewById(R.id.cPhoneNumber);
        cPhoneNumber.addTextChangedListener(loginTextWatcher);
        musicianName = findViewById(R.id.firstName);
        musicianName.addTextChangedListener(loginTextWatcher);
        musicianLocation = findViewById(R.id.location2);
        musicianLocation.addTextChangedListener(loginTextWatcher);
        musicianDistance = findViewById(R.id.firstName3);
        musicianDistance.addTextChangedListener(loginTextWatcher);
        musicianGenre = findViewById(R.id.firstName5);
        musicianGenre.addTextChangedListener(loginTextWatcher);
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
        rConfirmPassword = findViewById(R.id.registerConfirmPassword);

        AtomicBoolean validPass = new AtomicBoolean();
        validPass.set(true);
        AtomicBoolean matchingPass = new AtomicBoolean();
        matchingPass.set(true);

        /*Creating an empty char array with the length of the password*/
        char[] passChars = new char[rPassword.getText().length()];

        /*Copying all the characters in the password textbox to the char array*/
        rPassword.getText().getChars(0, rPassword.getText().length(), passChars, 0);

        GuardedString encryptedPassword = new GuardedString(passChars);

        /*Creating an empty char array with the length of the password*/
        char[] confirmChars = new char[rConfirmPassword.getText().length()];

        /*Copying all the characters in the password textbox to the char array*/
        rConfirmPassword.getText().getChars(0, rConfirmPassword.getText().length(), confirmChars, 0);

        GuardedString encryptedConfirm = new GuardedString(confirmChars);

        /*Password validation check*/
        /*Method used to access the encrypted password and get the clear chars*/
        encryptedPassword.access(new GuardedString.Accessor() {
            @Override
            public void access(char[] chars) {
                char[] passChars = chars;
                if(chars.length == 0)
                {
                    validPass.set(false);
                    return;
                }
                else if(chars.length < 8)
                {
                    validPass.set(false);
                    return;
                }
                else
                {
                    boolean number = false, capital = false, lowerCase = false, space = false;
                    for(char c : chars)
                    {
                        if(Character.isDigit(c))
                        {
                            number = true;
                        }
                        if(Character.isLetter(c) && Character.isUpperCase(c))
                        {
                            capital = true;
                        }
                        if(Character.isLetter(c) && Character.isLowerCase(c))
                        {
                            lowerCase = true;
                        }
                        if(c == ' ')
                        {
                            space = true;
                        }
                    }
                    if(!number)
                    {
                        validPass.set(false);
                        return;
                    }
                    if(!capital)
                    {
                        validPass.set(false);
                        return;
                    }
                    if(!lowerCase)
                    {
                        validPass.set(false);
                        return;
                    }
                    if(space)
                    {
                        validPass.set(false);
                        return;
                    }
                }

                /*Confirmation password check*/
                /*Method used to access the encrypted password and get the clear chars*/
                encryptedConfirm.access(new GuardedString.Accessor() {
                    @Override
                    public void access(char[] chars) {
                        if(chars.length == 0)
                        {
                            matchingPass.set(false);
                            return;
                        }
                        else if(chars.length != passChars.length)
                        {
                            matchingPass.set(false);
                            return;
                        }
                        for(int i = 0; i < chars.length; i++)
                        {
                            if(chars[i] != passChars[i])
                            {
                                matchingPass.set(false);
                                return;
                            }
                        }
                    }
                });
            }
        });
        if(!validPass.get())
        {
            rPassword.setError("Minimum password requirements:\n" +
                    "Eight characters\n" +
                    "One digit\n" +
                    "One upper-case letter\n" +
                    "One lower-case letter\n" +
                    "No spaces");
            return;
        }
        if(!matchingPass.get())
        {
            rConfirmPassword.setError("Passwords must be matching!");
            return;
        }
//        rPassword = findViewById(R.id.registerPassword);
//        String password = rPassword.getText().toString();
//        if (TextUtils.isEmpty(password)) {
//            rPassword.setError("Password is required!");
//            return;
//        }
//        rConfirmPassword = findViewById(R.id.registerConfirmPassword);
//        String confirmPassword = rConfirmPassword.getText().toString();
//        if (TextUtils.isEmpty(confirmPassword)) {
//            rConfirmPassword.setError("Confirm password is required!");
//            return;
//        }
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
        if (image.getDrawable() == null)
        {
            Toast.makeText(getApplicationContext(),"Please choose and image!", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    public void faderOnclick(View view) {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(TabbedMusicianActivity.this,R.color.darkerMain));
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