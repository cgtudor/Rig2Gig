package com.gangoffive.rig2gig;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

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

        if(CredentialActivity.userType.equals("Band"))
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
                        preference.setSummary(document.get("Username").toString());

                        preference = getPreferenceManager().findPreference("FullName");
                        preference.setSummary(document.get("Full Name").toString());

                        preference = getPreferenceManager().findPreference("UserEmailAddress");
                        preference.setSummary(document.get("Email Address").toString());
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

    private void executeDialog()
    {
        ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog();
        //changePasswordDialog.setTargetFragment(SettingsFragment.this, 1);
        changePasswordDialog.show(getFragmentManager(), "Dialog");
    }
}