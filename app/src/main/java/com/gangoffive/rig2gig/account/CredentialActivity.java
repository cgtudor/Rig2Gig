package com.gangoffive.rig2gig.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gangoffive.rig2gig.navbar.NavBarActivity;
import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.venue.management.VenueActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.identityconnectors.common.security.GuardedString;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * CredentialActivity loads when the activity is called.
 */
public class CredentialActivity extends AppCompatActivity {

    private static final String TAG = "======================";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    EditText cFirstName, cLastName, cUsername, cPhoneNumber, rEmailAddress, rConfirmEmail, rPassword, rConfirmPassword;

    RadioButton fan, accFan, accMusician, accVenue;
    RadioGroup userGroup;
    Button submit, dateOfBirth;

    String gender, userId, dob;
    String userType;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    /**
     * When the onCreate is called previous states from the activity can be restored.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credential);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        cFirstName = findViewById(R.id.venue_name_final);
        cLastName = findViewById(R.id.location);
        cUsername = findViewById(R.id.venue_description_final);
        cPhoneNumber = findViewById(R.id.cPhoneNumber);

        rEmailAddress = findViewById(R.id.emailReset);
        rConfirmEmail = findViewById(R.id.registerConfirmEmail);
        rPassword = findViewById(R.id.registerPassword);
        rConfirmPassword = findViewById(R.id.registerConfirmPassword);

        submit = findViewById(R.id.submitBtn);

    }

    /**
     * This method submits user details inputted to their document within the database.
     * @param view
     */
    public void submitBtnOnClick(View view) {
        String firstName = cFirstName.getText().toString();
        String lastName = cLastName.getText().toString();
        String username = cUsername.getText().toString();
        String phoneNumber = cPhoneNumber.getText().toString();

        final String email = rEmailAddress.getText().toString().trim();
        String confirmEmail = rConfirmEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            rEmailAddress.setError("Email is required!");
            return;
        }
        if (TextUtils.isEmpty(confirmEmail)) {
            rConfirmEmail.setError("Confirm email is required!");
            return;
        }
        if (!confirmEmail.matches(email)) {
            rConfirmEmail.setError("Email doesn't match!");
            return;
        }
        if (TextUtils.isEmpty(firstName)) {
            cFirstName.setError("Please enter a first name");
            return;
        }
        if (TextUtils.isEmpty(lastName)) {
            cLastName.setError("Please enter a last name");
            return;
        }
        if (TextUtils.isEmpty(username)) {
            cUsername.setError("Please enter a username name");
            return;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            cPhoneNumber.setError("Please enter a phone number");
            return;
        }

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

        /*Method used to access the encrypted password and get the clear chars*/
        encryptedPassword.access(new GuardedString.Accessor() {
            @Override
            public void access(char[] chars) {
                /**
                 * Creating an account with Firebase from the information that the user has inputted.
                 */
                String pass = new String(chars);
                fAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userId = fAuth.getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userId);
                            Map<String, Object> user = new HashMap<>();
                            user.put("email-address", email);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(CredentialActivity.this, "Account has been created!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onSuccess: user Profile is created for " + userId);
                                    fAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                //startActivity(new Intent(getApplicationContext(),CredentialActivity.class));
                                                userId = fAuth.getUid();
                                                DocumentReference documentReference = fStore.collection("users").document(userId);
                                                Map<String, Object> user = new HashMap<>();
                                                user.put("given-name", firstName);
                                                user.put("family-name", lastName);
                                                user.put("username", username);
                                                user.put("phone-number", phoneNumber);
                                                user.put("user-type", AccountPurposeActivity.userType);
                                                documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(CredentialActivity.this, "Information Added", Toast.LENGTH_SHORT).show();
                                                        if (AccountPurposeActivity.userType.equals("Venue"))
                                                        {
                                                            startActivity(new Intent(getApplicationContext(), VenueActivity.class));
                                                        }
                                                        else if (AccountPurposeActivity.userType.equals("Musician")){
                                                            startActivity(new Intent(getApplicationContext(), CreateMusicianAccountActivity.class));
                                                        }
                                                        else
                                                        {
                                                            startActivity(new Intent(getApplicationContext(), NavBarActivity.class));
                                                            finish();
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(CredentialActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                                        String uuid = fAuth.getUid();
                                                        System.out.println("=========================" + uuid);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CredentialActivity.this, "Error creating account", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });

                        } else {
                            Toast.makeText(CredentialActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        /*Disposes of the encrypted password from the memory*/
        encryptedConfirm.dispose();
        encryptedPassword.dispose();
    }

    /**
     * This method is used to handle the back button.
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}