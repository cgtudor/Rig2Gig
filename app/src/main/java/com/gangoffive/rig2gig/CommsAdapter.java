package com.gangoffive.rig2gig;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class CommsAdapter extends RecyclerView.Adapter<CommsAdapter.ViewHolder> {

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private DocumentReference commDocRef;

    private ArrayList<Communication> communications;
    private Context context;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        /*void onItemClick(int position);*/
        /*void onPhotoClick(int position);
        void onNameClick(int position);*/
        void onAcceptClick(int position);
        void onDeclineClick(int position);
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
        public ImageView imageViewAccept;
        public ImageView imageViewDecline;

        public ViewHolder(@NonNull View itemView, CommsAdapter.OnItemClickListener listener) {
            super(itemView);

            imageViewPhoto = (ImageView) itemView.findViewById(R.id.imageViewPhoto);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewType = (TextView) itemView.findViewById(R.id.textViewType);
            imageViewIcon = (ImageView) itemView.findViewById(R.id.imageViewIcon);
            textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            imageViewAccept = (ImageView) itemView.findViewById(R.id.imageViewAccept);
            imageViewDecline = (ImageView) itemView.findViewById(R.id.imageViewDecline);

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

            /*imageViewPhoto.setOnClickListener(new View.OnClickListener() {
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
            });*/

            imageViewAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onAcceptClick(position);
                        }
                    }
                }
            });

            imageViewDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onDeclineClick(position);
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
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comms_listing, parent, false);

        return new CommsAdapter.ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CommsAdapter.ViewHolder holder, int position) {
        Communication communication = communications.get(position);

        holder.commRef = communication.getCommRef();

        commDocRef = db.collection("communications")
                .document((FirebaseAuth.getInstance().getUid()))
                .collection("received")
                .document(holder.commRef);

        commDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot commDoc = task.getResult();
                    if (commDoc.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + commDoc.getData());

                        Timestamp postingDate = (Timestamp) commDoc.get("posting-date");
                        holder.textViewDate.setText(postingDate.toDate().toString());

                        switch(commDoc.get("type").toString()) {
                            case "contact-request":
                                holder.textViewType.setText("wants to contact you.");
                                //holder.imageViewIcon   change icon based on comm type
                                break;
                            case "contact-accept":
                                holder.textViewType.setText("has accepted your contact request.");
                                //contact info @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                                //communication.get(position).getUserRef() => database call to user to get user type
                                //find collection (usertype) that has same user ref.
                                break;
                            case "contact-send":
                                holder.textViewType.setText("has shared their contact details");
                                holder.imageViewAccept.setVisibility(View.GONE);
                                holder.imageViewDecline.setVisibility(View.GONE);
                                //contact info @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                                break;
                            case "contact-decline":
                                holder.textViewType.setText("has declined your contact request.");
                                break;
                            case "contact-retain":
                                holder.textViewType.setText("did not recieve your contact details");
                                holder.imageViewAccept.setVisibility(View.GONE);
                                holder.imageViewDecline.setVisibility(View.GONE);
                                break;
                            default:
                                //
                        }

                        String userRef = commDoc.get("sent-from").toString();
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

                                                                String photoID = refDoc.getId();

                                                                StorageReference venuePic = storage.getReference().child("/images/" + userType + "/" + photoID + ".jpg");
                                                                GlideApp.with(holder.imageViewPhoto.getContext()).load(venuePic).into(holder.imageViewPhoto);
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    Log.d("FIRESTORE", "DocumentSnapshot data:333 " + commDoc.getData());
                                }
                            }
                        });
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
}
