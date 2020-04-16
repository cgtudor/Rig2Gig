package com.gangoffive.rig2gig.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.navbar.NavBarActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotificationService extends Service {

    private final String CHANNEL_ID = "1";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "mainChannel";
            String description = "Channel for main notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference notifDoc = db.collection("communications")
                .document(FirebaseAuth.getInstance().getUid());

        CollectionReference notifColl = db.collection("communications")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("received");

        notifColl.orderBy("posting-date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("FIRESTORE NOTIFICATION", "Listen failed.", e);
                        return;
                    }

                    List<QueryDocumentSnapshot> messages = new ArrayList<>();
                for(DocumentChange dc : queryDocumentSnapshots.getDocumentChanges())
                {
                    switch (dc.getType())
                    {
                        case ADDED:
                            messages.add(dc.getDocument());
                            break;
                        case MODIFIED:
                            messages.add(dc.getDocument());
                            break;
                    }
                }
                if(!messages.isEmpty()) {
                    DocumentSnapshot snapshot = messages.get(0);
                        Log.d("FIRESTORE NOTIFICATION", "document " + " data: " + snapshot.getData());

                        String type = snapshot.get("type").toString();

                        db.collection("users")
                                .document(snapshot.get("sent-from").toString())
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d("FIRESTORE NOTIFICATION", "User found!");
                                    DocumentSnapshot user = task.getResult();
                                    if (user.exists()) {
                                        String notifMessage = "";
                                        String notifTitle = "";
                                        boolean notification = false;

                                        switch (type) {
                                            case "contact-request":
                                                notification = true;
                                                notifTitle = "Contact request received!";
                                                notifMessage = user.get("given-name").toString() + " " +
                                                        user.get("family-name").toString() + " " +
                                                        "has requested your contact details!";
                                                break;
                                            case "contact-accept":
                                                notification = true;
                                                notifTitle = "Contact request accepted!";
                                                notifMessage = user.get("given-name").toString() + " " +
                                                        user.get("family-name").toString() + " " +
                                                        "has accepted your contact request!";
                                                break;
                                            case "contact-decline":
                                                notification = true;
                                                notifTitle = "Contact request declined!";
                                                notifMessage = user.get("given-name").toString() + " " +
                                                        user.get("family-name").toString() + " " +
                                                        "has declined your contact request...";
                                                break;
                                            default:
                                                break;
                                        }

                                        if(notification) {
                                            Intent intent = new Intent(NotificationService.this, NavBarActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            intent.putExtra("CALLED_FROM", "NOTIF");
                                            PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this, CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.ic_email_black)
                                                    .setContentTitle(notifTitle)
                                                    .setContentText(notifMessage)
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                    // Set the intent that will fire when the user taps the notification
                                                    .setContentIntent(pendingIntent)
                                                    .setAutoCancel(true);

                                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(NotificationService.this);

                                            Random r = new Random();

                                            //FOR RELEASE
                                            //notificationManager.notify(r.nextInt(), builder.build());
                                        }
                                    } else {
                                        Log.d("FIRESTORE NOTIFICATION", "User does not exist");
                                    }
                                } else {
                                    Log.d("FIRESTORE NOTIFICATION", "User get failed with", task.getException());
                                }
                            }
                        });
                    }
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
