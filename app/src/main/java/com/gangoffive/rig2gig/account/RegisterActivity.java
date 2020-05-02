package com.gangoffive.rig2gig.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gangoffive.rig2gig.R;
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
 * RegisterActivity loads when the activity is called.
 */
public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "TAG";

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    EditText rEmailAddress, rConfirmEmail, rPassword, rConfirmPassword;
    Button rRegisterBtn;

    String userId;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rEmailAddress = findViewById(R.id.emailReset);
        rConfirmEmail = findViewById(R.id.registerConfirmEmail);
        rPassword = findViewById(R.id.registerPassword);
        rConfirmPassword = findViewById(R.id.registerConfirmPassword);
        rRegisterBtn = findViewById(R.id.resetPasswordBtn);
    }

    public void registerBtnOnClick(View view) {
        AtomicBoolean validPass = new AtomicBoolean();
        validPass.set(true);
        AtomicBoolean matchingPass = new AtomicBoolean();
        matchingPass.set(true);
        final String email = rEmailAddress.getText().toString().trim();
        String confirmEmail = rConfirmEmail.getText().toString().trim();

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

        if (TextUtils.isEmpty(email)){
            rEmailAddress.setError("Email is required!");
            return;
        }
        if (TextUtils.isEmpty(confirmEmail)){
            rConfirmEmail.setError("Confirm email is required!");
            return;
        }
        if(!confirmEmail.matches(email)){
            rConfirmEmail.setError("Email doesn't match!");
            return;
        }

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
                fAuth.createUserWithEmailAndPassword(email, new String(chars)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            userId = fAuth.getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userId);
                            Map<String, Object> user = new HashMap<>();
                            user.put("email", email);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterActivity.this, "Account has been created!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userId);
                                    fAuth.signInWithEmailAndPassword(email, new String(passChars)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()){
                                                Log.d(TAG, "Opening credential activity");
                                                startActivity(new Intent(getApplicationContext(),CredentialActivity.class));
                                            }
                                            else
                                            {
                                                Log.d(TAG, "Sign in failed: " + task.getException());
                                            }
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this, "Error creating account", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });

                        }else {
                            Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
     * onClick the registerLoginBtn will launch the LoginActivity.
     * @param view
     */
    public void registerLoginBtn(View view) {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
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

