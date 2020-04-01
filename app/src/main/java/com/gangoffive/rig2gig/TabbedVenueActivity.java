package com.gangoffive.rig2gig;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gangoffive.rig2gig.ui.TabbedView.VenuePagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.identityconnectors.common.security.GuardedString;

import java.util.concurrent.atomic.AtomicBoolean;

public class TabbedVenueActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;

    Button confirm, back, submit;

    EditText cFirstName, cLastName, cUsername, cPhoneNumber, rEmailAddress, rConfirmEmail, rPassword, rConfirmPassword, username, venueAddressTextView, Vname, description;
    TextView invis;
    String test2;

    ImageView image;

    private static final String TAG = "======================";

    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_credential,
            R.layout.fragment_create_venue};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        tabTitles = new int[]{R.string.personalInformation, R.string.venueInformation};

        VenuePagerAdapter venuePagerAdapter = new VenuePagerAdapter
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


        CredentialFragment.btn.performClick();
        System.out.println("clicked");

        //toast here for account being created
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
}
