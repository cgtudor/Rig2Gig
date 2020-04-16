package com.gangoffive.rig2gig.navbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.account.AccountPurposeActivity;
import com.gangoffive.rig2gig.account.ChangePasswordDialog;
import com.gangoffive.rig2gig.account.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class SettingsFragment extends PreferenceFragmentCompat
{
    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final String USERID = fAuth.getUid();
    private final DocumentReference USERDOCUMENT = FSTORE.collection("users").document(USERID);

    /**
     * Upon loading the SettingsFragment, this method is called to setup the page with the required preferences from the specified file.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @param rootKey This is the root of the preference hierarchy.
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setupPreferences();
        setPreferencesFromResource(R.xml.preferences, rootKey);

        if(AccountPurposeActivity.userType.equals("Band"))
        {
            Preference preference = getPreferenceManager().findPreference("MusicianUpgrade");
            preference.setVisible(true);
        }

        Preference button = getPreferenceManager().findPreference("ChangePassword");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                executeDialog();
                return true;
            }
        });
        Preference logout = getPreferenceManager().findPreference("Logout");
        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                fbLogout();
                return true;
            }
        });
    }

    /**
     * This method is used to read from the Firebase Firestore database and pull user account details to display in the account section of the Settings Page.
     */
    private void setupPreferences()
    {
        USERDOCUMENT.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists())
                    {
                        Log.d("FIRESTORE", "DocumentSnapshot data" + document.getData());
                        Preference preference;

                        preference = getPreferenceManager().findPreference("UserName");
                        preference.setSummary(document.get("username").toString());

                        preference = getPreferenceManager().findPreference("FullName");
                        preference.setSummary(document.get("given-name").toString() + " " + document.get("family-name").toString());

                        preference = getPreferenceManager().findPreference("UserEmailAddress");
                        preference.setSummary(document.get("email-address").toString());
                    }
                    else
                    {
                        Log.d("FIRESTORE", "No such document");
                    }
                }
                else
                {
                    Log.d("FIRESTORE", "get failed with: ", task.getException());
                }
            }
        });
    }

    /**
     * This method is used to instantiate and execute the alert dialog created in the ChangePasswordDialog class.
     */
    private void executeDialog()
    {
        ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog();
        changePasswordDialog.show(getFragmentManager(), "Dialog");
    }

    private void fbLogout()
    {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.signOut();
        startActivity(new Intent(getContext(), LoginActivity.class));
    }
}