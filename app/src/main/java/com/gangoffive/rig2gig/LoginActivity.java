package com.gangoffive.rig2gig;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.GoogleAuthProvider;

import org.identityconnectors.common.security.GuardedString;

public class LoginActivity extends AppCompatActivity{
    private static final String TAG = "TAG";
    private static final int RC_SIGN_IN = 234;

    FirebaseAuth fAuth;
    SignInButton signInButton;

    GoogleSignInClient mGoogleSignInClient;
    LoginButton loginButton;
    private CallbackManager mCallbackManager;

    Button loginBtn;
    TextView registerBtn, forgotPasswordBtn;
    EditText emailAddress, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        emailAddress = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.registerLoginBtn);
        registerBtn = findViewById(R.id.loginRegisterBtn);
        forgotPasswordBtn = findViewById(R.id.forgotPasswordBtn);
        signInButton = findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        googleSignIn();
                        break;
                }
            }
        });

        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.fb_loginBtn);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }
            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    public void LoginBtnOnClick(View view) {
        final String getEmail = emailAddress.getText().toString().trim();

        /*Creating an empty char array with the length of the password*/
        char[] passChars = new char[password.getText().length()];

        /*Copying all the characters in the password textbox to the char array*/
        password.getText().getChars(0, password.getText().length(), passChars, 0);
        GuardedString encryptedPassword = new GuardedString(passChars);
        /*Method used to access the encrypted password and get the clear chars*/
        encryptedPassword.access(new GuardedString.Accessor() {
            @Override
            public void access(char[] clearChars) {
                fAuth.signInWithEmailAndPassword(getEmail, new String(clearChars)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "User Logged In", Toast.LENGTH_LONG).show();
                            /**
                             * Checking if this is users first time logging in or existing user
                             */
                            FirebaseUserMetadata metadata = fAuth.getCurrentUser().getMetadata();
                            if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                                // The user is new, show them a fancy intro screen!
                                startActivity(new Intent(getApplicationContext(), CredentialActivity.class));
                            } else {
                                // This is an existing user, show them a welcome back screen.
                                startActivity(new Intent(getApplicationContext(), NavBarActivity.class));
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Email Or Password Is Incorrect! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        /*Disposes of the encrypted password from the memory*/
        encryptedPassword.dispose();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        System.out.println("-------------------------GoogleAuth");
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = fAuth.getCurrentUser();
                            /**
                             * Checking if this is users first time logging in or existing user
                             */
                            FirebaseUserMetadata metadata = fAuth.getCurrentUser().getMetadata();
                            if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                                // The user is new, show them a fancy intro screen!
                                startActivity(new Intent(getApplicationContext(), CredentialActivity.class));
                            } else {
                                // This is an existing user, show them a welcome back screen.
                                startActivity(new Intent(getApplicationContext(), NavBarActivity.class));
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = fAuth.getCurrentUser();
                            /**
                             * Checking if this is users first time logging in or existing user
                             */
                            FirebaseUserMetadata metadata = fAuth.getCurrentUser().getMetadata();
                            if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                                // The user is new, show them a fancy intro screen!
                                startActivity(new Intent(getApplicationContext(), CredentialActivity.class));
                            } else {
                                // This is an existing user, show them a welcome back screen.
                                startActivity(new Intent(getApplicationContext(), NavBarActivity.class));
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void googleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        System.out.println("-------------------------------Here");
    }

    public void loginRegisterBtn(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }

    public void forgotPasswordBtn(View view) {
        startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
    }
}




