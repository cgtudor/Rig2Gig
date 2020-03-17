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

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MusiciansBandsAdapter extends RecyclerView.Adapter<MusiciansBandsAdapter.AdapterViewHolder>
{
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DocumentReference docRef;
    private Context context;

    private ArrayList<MusiciansBands> musiciansBandsArrayList;

    private MusiciansBandsAdapter.OnItemClickListener listener;

    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(MusiciansBandsAdapter.OnItemClickListener listener)
    {
        this.listener = listener;
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder
    {
        public String listingReference;
        public ImageView imageView;
        public TextView textView;

        public AdapterViewHolder(@NonNull View itemView, MusiciansBandsAdapter.OnItemClickListener listener)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.band_picture);
            textView = itemView.findViewById(R.id.band_name);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                    {
                        int position = getAdapterPosition();

                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public MusiciansBandsAdapter(ArrayList<MusiciansBands> musicianBandsList, Context context)
    {
        this.musiciansBandsArrayList = musicianBandsList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_musician_bands, parent, false);

        return new AdapterViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position)
    {
        MusiciansBands currentBand = musiciansBandsArrayList.get(position);

        holder.listingReference = currentBand.getReference();

        docRef = db.collection("bands").document(currentBand.getReference());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists())
                    {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        holder.textView.setText(document.get("name").toString());
                        StorageReference bandPic = storage.getReference().child("/images/bands/" + currentBand.getReference() + ".jpg");
                        GlideApp.with(holder.imageView.getContext()).load(bandPic).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.imageView);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return musiciansBandsArrayList.size();
    }
}
