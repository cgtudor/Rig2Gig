package com.gangoffive.rig2gig.account;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.gangoffive.rig2gig.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePasswordDialog extends DialogFragment
{
    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final String USERID = fAuth.getUid();
    private final DocumentReference USERDOCUMENT = FSTORE.collection("users").document(USERID);

    /**
     * This method details the alert dialog.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @return returns the created dialog.
     */
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.change_password_dialog_message)
                .setTitle(R.string.change_password_dialog_title);

        builder.setPositiveButton(R.string.change_password_confirm, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                sendEmail(dialog);
            }
        });

        builder.setNegativeButton(R.string.change_password_cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();

        return dialog;
    }

    /**
     * This method is used to grab the logged in user's email form the database and send a reset password email to them.
     * @param dialog This parameter represents the context.
     */
    private void sendEmail(DialogInterface dialog)
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
                        String email = document.get("email-address").toString().trim();
                        fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(((Dialog) dialog).getContext(), "Email Sent!", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(((Dialog) dialog).getContext(), "Email Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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
}
