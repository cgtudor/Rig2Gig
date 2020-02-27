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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BandAdapter extends RecyclerView.Adapter<BandAdapter.ViewHolder>{

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private DocumentReference docRef;

    private ArrayList<BandListing> bandListings;
    private Context context;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public String listingRef;
        public ImageView imageViewPhoto;
        public TextView textViewName;
        public TextView textViewGenres;
        public TextView textViewLoc;
        public TextView textViewRating;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            imageViewPhoto = (ImageView) itemView.findViewById(R.id.imageViewPhoto);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewGenres = (TextView) itemView.findViewById(R.id.textViewGenres);
            textViewLoc = (TextView) itemView.findViewById(R.id.textViewLoc);
            textViewRating = (TextView) itemView.findViewById(R.id.textViewRating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public BandAdapter(ArrayList<BandListing> bandListings, Context context) {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        this.bandListings = bandListings;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.band_listing, parent, false);

        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BandListing bandListing = bandListings.get(position);

        holder.listingRef = bandListing.getListingRef();

        docRef= db.collection("bands").document(bandListing.getBandRef());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());
                        holder.textViewName.setText(document.get("name").toString());
                        holder.textViewGenres.setText(document.get("genres").toString());
                        holder.textViewLoc.setText(document.get("location").toString());
                        holder.textViewRating.setText(document.get("rating").toString());
                        StorageReference bandPic = storage.getReference().child("/images/venue-listings/" + bandListing.getListingRef() + ".jpg");
                        GlideApp.with(holder.imageViewPhoto.getContext()).load(bandPic).into(holder.imageViewPhoto);

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

        return bandListings.size();
    }
}

