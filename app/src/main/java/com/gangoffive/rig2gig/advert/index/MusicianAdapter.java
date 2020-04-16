package com.gangoffive.rig2gig.advert.index;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MusicianAdapter extends RecyclerView.Adapter<MusicianAdapter.ViewHolder>{

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private DocumentReference docRef;

    private ArrayList<MusicianListing> musicianListings;
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
        public TextView textViewPos;
        public TextView textViewLoc;
        public TextView textViewRating;
        public TextView textViewRatingText;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            imageViewPhoto = (ImageView) itemView.findViewById(R.id.imageViewPhoto);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewGenres = (TextView) itemView.findViewById(R.id.textViewGenres);
            textViewPos = (TextView) itemView.findViewById(R.id.textViewPos);
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

    public MusicianAdapter(ArrayList<MusicianListing> musicianListings, Context context) {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        this.musicianListings = musicianListings;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.musician_listing, parent, false);

        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        MusicianListing musicianListing = musicianListings.get(position);

        holder.listingRef = musicianListing.getListingRef();

        docRef= db.collection("musicians").document(musicianListing.getMusicianRef());

        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());
                        holder.textViewName.setText(document.get("name").toString());
                        String genres = document.get("genres").toString();
                        genres = genres.substring(1, genres.length() - 1);
                        holder.textViewGenres.setText(genres);
                        String positions = "";
                        for(String position : musicianListing.getPosition()) {
                            positions += position + " ";
                        }
                        positions = positions.trim();
                        holder.textViewPos.setText(positions);
                        holder.textViewLoc.setText(document.get("location").toString());
                        holder.textViewRating.setText(document.get("rating").toString());
                        holder.textViewRatingText.setText("out of 5");
                        StorageReference bandPic = storage.getReference().child("/images/musician-listings/" + musicianListing.getListingRef() + ".jpg");
                        GlideApp.with(holder.imageViewPhoto.getContext())
                                .load(bandPic)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .skipMemoryCache(false)
                                .into(holder.imageViewPhoto);

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

        return musicianListings.size();
    }
}
