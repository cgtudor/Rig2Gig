package com.gangoffive.rig2gig;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    //private FirebaseFirestore db;
    //private CollectionReference colRef;

    private List<PerformerListing> performerListings;
    private Context context;

    public MyAdapter(List<PerformerListing> performerListings, Context context) {
        //db = FirebaseFirestore.getInstance();
        //colRef = db.collection("bands");

        this.performerListings = performerListings;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.performer_listing, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PerformerListing performerListing = performerListings.get(position);

        //GlideApp.with(this).load(bandPic).into(imageViewPhoto);
        holder.textViewName.setText(performerListing.getName());
        holder.textViewGenres.setText(performerListing.getGenres());
        holder.textViewLoc.setText(performerListing.getLocation());

        /*final StringBuilder rating = new StringBuilder("");

        DocumentReference docRef = colRef.document(performerListing.getBandRef());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());
                        rating.append(document.get("Rating").toString());


                    } else {
                        Log.d("FIRESTORE", "No such document");
                    }
                } else {
                    Log.d("FIRESTORE", "get failed with ", task.getException());
                }
            }
        });
        holder.textViewRating.setText(rating.toString());*/

    }

    @Override
    public int getItemCount() {
        return performerListings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //public ImageView imageViewPhoto;
        public TextView textViewName;
        public TextView textViewGenres;
        public TextView textViewLoc;
        //public TextView textViewRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //imageViewPhoto = (ImageView) itemView.findViewById((R.id.imageViewPhoto);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewGenres = (TextView) itemView.findViewById(R.id.textViewGenres);
            textViewLoc = (TextView) itemView.findViewById(R.id.textViewLoc);
            //textViewRating = (TextView) itemView.findViewById(R.id.textViewRating);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PerformanceListingDetailsActivity.class);
                    //intent.putExtra("listingRef", getRefSomeHow)
                    //startActivity(intent);
                }
            });*/
        }
    }
}
