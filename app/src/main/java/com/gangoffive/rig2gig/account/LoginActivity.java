package com.gangoffive.rig2gig.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gangoffive.rig2gig.navbar.NavBarActivity;
import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.views.PrivacyPolicyActivity;
import com.gangoffive.rig2gig.views.PrivacyPolicyFragment;
import com.gangoffive.rig2gig.views.TermsOfServiceActivity;
import com.gangoffive.rig2gig.views.TermsOfServiceFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import org.identityconnectors.common.security.GuardedString;

import java.util.HashMap;
import java.util.Map;

/**
 * LoginActivity loads when the activity is called.
 */
public class LoginActivity extends AppCompatActivity{
    private static final String TAG = "TAG";
    private static final int RC_SIGN_IN = 234;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String userId = fAuth.getUid();

    Button signInButton;

    GoogleSignInClient mGoogleSignInClient;
    LoginButton loginButton;
    private CallbackManager mCallbackManager;

    Button loginBtn;
    TextView registerBtn, forgotPasswordBtn, loginTnC;
    EditText emailAddress, password;
    private boolean minimise;

    /**
     * When the onCreate is called previous states from the activity can be restored.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        loginTnC = findViewById(R.id.TandC);

        loginTnC.setGravity(Gravity.CENTER);


        /**
         * onClick for Google Sign In button.
         */
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

        /**
         * Initialisation of Facebook login button.
         */

        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.fb_loginBtn);
        loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
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

    /**
     * Login in with email and password. This method gets the inputted email address and password
     * by the user.
     * @param view
     */
    public void LoginBtnOnClick(View view) {

        final String getEmail = emailAddress.getText().toString().trim();

        if(getEmail == null || getEmail.equals(""))
        {
            Toast.makeText(this, "You must provide an email", Toast.LENGTH_SHORT).show();
            return;
        }



        /*Creating an empty char array with the length of the password*/
        char[] passChars = new char[password.getText().length()];

        /*Copying all the characters in the password textbox to the char array*/
        password.getText().getChars(0, password.getText().length(), passChars, 0);
        GuardedString encryptedPassword = new GuardedString(passChars);

        /*Method used to access the encrypted password and get the clear chars*/
        encryptedPassword.access(new GuardedString.Accessor() {
            @Override
            public void access(char[] clearChars) {

                if(clearChars.length == 0)
                {
                    Toast.makeText(LoginActivity.this, "You must provide a password", Toast.LENGTH_SHORT).show();
                    return;
                }

                fAuth.signInWithEmailAndPassword(getEmail, new String(clearChars)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this, "User Logged In", Toast.LENGTH_LONG).show();
                                // This is an existing user, show them a welcome back screen.
                            final String getUserId = fAuth.getUid();
                            DocumentReference docIdRef = fStore.collection("users").document(getUserId);
                            docIdRef.update("token", FirebaseInstanceId.getInstance().getToken());
                            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists())
                                        {
                                            Log.d(TAG, "Document exists!");
                                            AccountPurposeActivity.userType = document.get("user-type").toString();
                                            /*Intent serviceIntent = new Intent(LoginActivity.this, NotificationService.class);
                                            startService(serviceIntent);*/
                                            startActivity(new Intent(getApplicationContext(), NavBarActivity.class));
                                            finish();
                                        }
                                        else
                                        {
                                            Log.d(TAG, "Document doesn't exists!");
                                        }
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Email Or Password Is Incorrect! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        /*Disposes of the encrypted password from the memory*/
        encryptedPassword.dispose();
    }

    /**
     * If the user is signing in with Google then the authentication method with google will be called
     * with the users account information. The same will happen if the user is signing in with Facebook.
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
                // Google Sign In failed
                Log.w(TAG, "Google sign in failed", e);
            }
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * This method gets the information passed by Google, if the user hasn't log in before with Google
     * then their email will be added to the database under a document of their userId. Then the user will
     * get redirected to the CredentialActivity. If the user has already logged in with Facebook they will be redirected
     * to the NavBarActivity.
     * @param acct
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = fAuth.getCurrentUser();
                            final String userEmail = user.getEmail();
                            final String getUserId = fAuth.getUid();
                            DocumentReference docIdRef = fStore.collection("users").document(getUserId);
                            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d(TAG, "Document exists!");
                                            AccountPurposeActivity.userType = document.get("User Type").toString();
                                            /*Intent serviceIntent = new Intent(LoginActivity.this, NotificationService.class);
                                            startService(serviceIntent);*/
                                            startActivity(new Intent(getApplicationContext(), NavBarActivity.class));
                                            finish();
                                        } else {
                                            Log.d(TAG, "Document does not exist!");
                                            System.out.println("================================== Doc dont exist");
                                            DocumentReference documentReference = fStore.collection("users").document(getUserId);
                                            System.out.println("================================== " + userId);
                                            System.out.println("================================== " + userEmail);
                                            Map<String, Object> test = new HashMap<>();
                                            test.put("Email Address", userEmail);
                                            documentReference.set(test).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(LoginActivity.this, "Account has been created!", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userId);
                                                    /*Intent serviceIntent = new Intent(LoginActivity.this, NotificationService.class);
                                                    startService(serviceIntent);*/
                                                    startActivity(new Intent(getApplicationContext(),CredentialActivity.class));
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    /**
     * This method gets the information passed by Facebook, if the user hasn't log in before with Facebook
     * then their email will be added to the database under a document of their userId. Then the user will
     * get redirected to the CredentialActivity. If the user has already logged in with Facebook they will be redirected
     * to the NavBarActivity.
     * @param token
     */
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithFacebook:success");
                            FirebaseUser user = fAuth.getCurrentUser();
                            final String userEmail = user.getEmail();
                            System.out.println("================================= " + userEmail);

                            DocumentReference docIdRef = fStore.collection("users").document(userId);
                            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d(TAG, "Document exists!");
                                            AccountPurposeActivity.userType = document.get("User Type").toString();
                                            /*Intent serviceIntent = new Intent(LoginActivity.this, NotificationService.class);
                                            startService(serviceIntent);*/
                                            startActivity(new Intent(getApplicationContext(), NavBarActivity.class));
                                            finish();
                                        } else {
                                            Log.d(TAG, "Document does not exist!");
                                            System.out.println("================================== Doc dont exist");
                                            DocumentReference documentReference = fStore.collection("users").document(userId);
                                            System.out.println("================================== " + userId);
                                            System.out.println("================================== " + userEmail);
                                            Map<String, Object> test = new HashMap<>();
                                            test.put("Email Address", userEmail);
                                            documentReference.set(test).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(LoginActivity.this, "Account has been created!", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userId);
                                                    /*Intent serviceIntent = new Intent(LoginActivity.this, NotificationService.class);
                                                    startService(serviceIntent);*/
                                                    startActivity(new Intent(getApplicationContext(),CredentialActivity.class));
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    /**
     * Starts the intent for Google Sign In.
     */
    private void googleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * onClick the loginRegisterBtn will launch the Register Activity
     * @param view
     */
    public void loginRegisterBtn(View view) {
        startActivity(new Intent(getApplicationContext(), AccountPurposeActivity.class));
    }

    /**
     * onClick the forgotPasswordBtn will launch the ForgotPasswordActivity
     * @param view
     */
    public void forgotPasswordBtn(View view) {
        startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
    }

    /**
     * This method is used to handle the back button.
     */
    @Override
    public void onBackPressed()
    {
        if(minimise)
        {
            this.moveTaskToBack(true);
        }
        else
        {
            Toast.makeText(LoginActivity.this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
            minimise = true;
        }
    }

    /**
     * This method is used to handle resuming an activity.
     */
    @Override
    public void onResume()
    {
        minimise = false;
        super.onResume();
    }

    public void privacyOnClick(View view) {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ Privacy Clicked!");
        startActivity(new Intent(getApplicationContext(), PrivacyPolicyActivity.class));
    }

    public void termsofserviceOnClick(View view) {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ Terms Clicked!");
        startActivity(new Intent(getApplicationContext(), TermsOfServiceActivity.class));
    }
}




