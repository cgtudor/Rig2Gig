package com.gangoffive.rig2gig;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    private List<PerformerListing> performerListings;
    private Context context;

    public MyAdapter(List<PerformerListing> performerListings, Context context) {
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

        holder.textViewName.setText(performerListing.getName());
        holder.textViewGenres.setText(performerListing.getGenres());
        holder.textViewLoc.setText(performerListing.getLocation());
    }

    @Override
    public int getItemCount() {
        return performerListings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewName;
        public TextView textViewGenres;
        public TextView textViewLoc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewGenres = (TextView) itemView.findViewById(R.id.textViewGenres);
            textViewLoc = (TextView) itemView.findViewById(R.id.textViewLoc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent intent = new Intent(context, CristiansFragementHere.class);
                    //intent.putExtra("listingRef", getRefSomeHow)
                    //startActivity(intent);
                }
            });
        }
    }
}
