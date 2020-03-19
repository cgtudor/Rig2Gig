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

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.ViewHolder> {

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private DocumentReference docRef;

    private ArrayList<VenueListing> venueListings;
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
        public TextView textViewLoc;
        public TextView textViewRating;
        public TextView textViewRatingText;

        public ViewHolder(@NonNull View itemView, VenueAdapter.OnItemClickListener listener) {
            super(itemView);

            imageViewPhoto = (ImageView) itemView.findViewById(R.id.imageViewPhoto);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewLoc = (TextView) itemView.findViewById(R.id.textViewLoc);
            textViewRating = (TextView) itemView.findViewById(R.id.textViewRating);
            textViewRatingText = (TextView) itemView.findViewById(R.id.textViewRatingText);

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

    public VenueAdapter(ArrayList<VenueListing> venueListings, Context context) {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        this.venueListings = venueListings;
        this.context = context;
    }

    @NonNull
    @Override
    public VenueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.venue_listing, parent, false);

        return new VenueAdapter.ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull VenueAdapter.ViewHolder holder, int position) {
        VenueListing venueListing = venueListings.get(position);

        holder.listingRef = venueListing.getListingRef();

        docRef= db.collection("venues").document(venueListing.getVenueRef());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());
                        holder.textViewName.setText(document.get("name").toString());
                        holder.textViewLoc.setText(document.get("location").toString());
                        holder.textViewRating.setText(document.get("rating").toString());
                        holder.textViewRatingText.setText("out of 5");
                        StorageReference venuePic = storage.getReference().child("/images/venue-listings/" + venueListing.getListingRef() + ".jpg");
                        GlideApp.with(holder.imageViewPhoto.getContext()).load(venuePic).into(holder.imageViewPhoto);

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
        return venueListings.size();
    }
}
