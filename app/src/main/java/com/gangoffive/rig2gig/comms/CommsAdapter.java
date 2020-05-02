package com.gangoffive.rig2gig.comms;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gangoffive.rig2gig.firebase.GlideApp;
import com.gangoffive.rig2gig.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CommsAdapter extends RecyclerView.Adapter<CommsAdapter.ViewHolder> {

    private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private DocumentReference commDocRef;

    private ArrayList<Communication> communications;
    private Context context;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        /*void onItemClick(int position);*/
        void onPhotoClick(int position);
        void onNameClick(int position);
        void onTopButtonClick(int position);
        void onBotButtonClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public String commRef;
        public ImageView imageViewPhoto;
        public TextView textViewName;
        public TextView textViewType;
        public ImageView imageViewIcon;
        public TextView textViewDate;
        public ImageView imageViewTopButton;
        public ImageView imageViewBotButton;

        public ViewHolder(@NonNull View itemView, CommsAdapter.OnItemClickListener listener) {
            super(itemView);

            imageViewPhoto = (ImageView) itemView.findViewById(R.id.imageViewPhoto);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewType = (TextView) itemView.findViewById(R.id.textViewType);
            imageViewIcon = (ImageView) itemView.findViewById(R.id.imageViewIcon);
            textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            imageViewTopButton = (ImageView) itemView.findViewById(R.id.imageViewTopButton);
            imageViewBotButton = (ImageView) itemView.findViewById(R.id.imageViewBotButton);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });*/

            imageViewPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onPhotoClick(position);
                        }
                    }
                }
            });

            textViewName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onNameClick(position);
                        }
                    }
                }
            });

            imageViewTopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onTopButtonClick(position);
                        }
                    }
                }
            });

            imageViewBotButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onBotButtonClick(position);
                        }
                    }
                }
            });
        }
    }

    public CommsAdapter(ArrayList<Communication> communications, Context context) {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        this.communications = communications;
        this.context = context;
    }

    @NonNull
    @Override
    public CommsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*switch(viewType) {
            case VIEW_TYPE_EMPTY_LIST_PLACEHOLDER:
                //fill in
                break;
            case VIEW_TYPE_OBJECT_VIEW:
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comms_listing, parent, false);
                return new CommsAdapter.ViewHolder(v, listener);
                break;
        }*/

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comms_listing, parent, false);
        return new CommsAdapter.ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CommsAdapter.ViewHolder holder, int position) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        Communication communication = communications.get(position);

        holder.commRef = communication.getCommRef();

        commDocRef = db.collection("communications")
                .document((FirebaseAuth.getInstance().getUid()))
                .collection("received")
                .document(holder.commRef);

        commDocRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot commDoc = task.getResult();
                    if (commDoc.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + commDoc.getData());

                        StorageReference venuePic = storage.getReference().child("/images/" + communication.getSentFromType() + "/" + communication.getSentFromRef() + ".jpg");
                        GlideApp.with(holder.imageViewPhoto.getContext())
                                .load(venuePic)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .skipMemoryCache(false)
                                .into(holder.imageViewPhoto);

                        Timestamp postingDate = (Timestamp) commDoc.get("posting-date");
                        Date pDate = postingDate.toDate();
                        String pattern = "dd/MM/yyyy HH:mm";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                        holder.textViewDate.setText(simpleDateFormat.format(pDate));

                        switch(commDoc.get("type").toString()) {
                            case "contact-request":
                                holder.textViewType.setText("wants to contact you.");
                                holder.imageViewTopButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_circle_green));
                                holder.imageViewBotButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cancel_red));
                                break;
                            case "contact-accept":
                                holder.textViewType.setText("has accepted your contact request.");
                                holder.imageViewTopButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_phone_black));
                                holder.imageViewBotButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_email_black));
                                break;
                            case "contact-send":
                                holder.textViewType.setText("has shared their contact details");
                                holder.imageViewTopButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_phone_black));
                                holder.imageViewBotButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_email_black));
                                break;
                            case "contact-decline":
                                holder.textViewType.setText("has declined your contact request.");
                                holder.imageViewTopButton.setVisibility(View.GONE);
                                holder.imageViewBotButton.setVisibility(View.GONE);
                                break;
                            case "contact-retain":
                                holder.textViewType.setText("did not recieve your contact details");
                                holder.imageViewTopButton.setVisibility(View.GONE);
                                holder.imageViewBotButton.setVisibility(View.GONE);
                                break;
                            case "left-band":
                                holder.textViewType.setText("You are no longer a band member.");
                                holder.imageViewTopButton.setVisibility(View.GONE);
                                holder.imageViewBotButton.setVisibility(View.GONE);
                                break;
                            case "join-request":
                                DocumentReference bandDocRef = db.collection("bands").document(commDoc.get("sent-from-ref").toString());
                                bandDocRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    /**
                                     * on successful database query, return data and image to calling activity
                                     */
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                    {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                holder.textViewType.setText("has invited you to join their band ");// +
                                                        //document.getData().get("name").toString() + ".");
                                                holder.imageViewTopButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_circle_green));
                                                holder.imageViewBotButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cancel_red));
                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                            } else {
                                                Log.d(TAG, "No such document");
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });
                                break;
                            default:
                                //
                        }

                        DocumentReference sentFromDocRef = db.collection(communication.getSentFromType()).document(communication.getSentFromRef());

                        sentFromDocRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d("FIRESTORE", "DocumentSnapshot data:111 " + commDoc.getData());
                                    DocumentSnapshot sentFromDoc = task.getResult();
                                    if (sentFromDoc.exists()) {
                                        Log.d("FIRESTORE", "DocumentSnapshot data:222 " + commDoc.getData());

                                        holder.textViewName.setText(sentFromDoc.get("name").toString());
                                    }
                                } else {
                                    Log.d("FIRESTORE", "DocumentSnapshot data:333 " + commDoc.getData());
                                }
                            }
                        });

                        /*String userRef = commDoc.get("sent-from").toString();
                        DocumentReference userDocRef = db.collection("users").document(userRef);

                        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d("FIRESTORE", "DocumentSnapshot data:111 " + commDoc.getData());
                                    DocumentSnapshot userDoc = task.getResult();
                                    if (userDoc.exists()) {
                                        Log.d("FIRESTORE", "DocumentSnapshot data:222 " + commDoc.getData());

                                        String userType = userDoc.get("user-type").toString().equals("Musician") ? "musicians" : "venues";
                                        db.collection(userType).whereEqualTo("user-ref", userRef)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            Log.d("FIRESTORE", "DocumentSnapshot data:444 " + commDoc.getData());
                                                            QuerySnapshot snap = task.getResult();
                                                            if(snap.isEmpty())
                                                            {
                                                                Log.d("FIRESTORE", "No results for user ref " + userRef);
                                                            } else {
                                                                Log.d("FIRESTORE", "DocumentSnapshot data:333 " + commDoc.getData());

                                                                DocumentSnapshot refDoc = snap.getDocuments().get(0);

                                                                holder.textViewName.setText(refDoc.get("name").toString());

                                                                if (commDoc.get("type").toString().equals("join-request"))
                                                                {
                                                                    String photoID = commDoc.get("band-ref").toString();
                                                                    StorageReference bandPic = storage.getReference().child("/images/bands/" + photoID + ".jpg");
                                                                    GlideApp.with(holder.imageViewPhoto.getContext()).load(bandPic).into(holder.imageViewPhoto);
                                                                }
                                                                else
                                                                {
                                                                    String photoID = refDoc.getId();

                                                                    StorageReference venuePic = storage.getReference().child("/images/" + userType + "/" + photoID + ".jpg");
                                                                    GlideApp.with(holder.imageViewPhoto.getContext()).load(venuePic).into(holder.imageViewPhoto);
                                                                }

                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    Log.d("FIRESTORE", "DocumentSnapshot data:333 " + commDoc.getData());
                                }
                            }
                        });*/
                    } else {
                        Log.d("FIRESTORE", "No such document");
                    }
                } else {
                    Log.d("FIRESTORE", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return communications.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(communications.isEmpty()) {
            return VIEW_TYPE_EMPTY_LIST_PLACEHOLDER;
        } else {
            return VIEW_TYPE_OBJECT_VIEW;
        }
    }
}
