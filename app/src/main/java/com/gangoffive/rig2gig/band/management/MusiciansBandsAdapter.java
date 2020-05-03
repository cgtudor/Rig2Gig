package com.gangoffive.rig2gig.band.management;

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

/**
 * This class is used to help display a musicians band on a card.
 * @author Ben souch
 * @version #0.3b
 * @since #0.2b
 */
public class MusiciansBandsAdapter extends RecyclerView.Adapter<MusiciansBandsAdapter.AdapterViewHolder>
{
    private final FirebaseFirestore DB = FirebaseFirestore.getInstance();
    private final FirebaseStorage STORAGE = FirebaseStorage.getInstance();
    private DocumentReference docRef;
    private Context context;

    private ArrayList<MusiciansBands> musiciansBandsArrayList;

    private MusiciansBandsAdapter.OnItemClickListener listener;

    /**
     * This interface is used to force the implementation of the onItemClick method for all relevant classes.
     * @since #0.2b
     */
    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }

    /**
     * Constructor that sets up the listener variable with the passed in listener.
     * @param listener References the listener to be used by the class.
     * @since #0.2b
     */
    public void setOnItemClickListener(MusiciansBandsAdapter.OnItemClickListener listener)
    {
        this.listener = listener;
    }

    /**
     * This inner class is used to produce the Card View.
     * @author Ben souch
     * @version #0.3b
     * @since #0.2b
     */
    public static class AdapterViewHolder extends RecyclerView.ViewHolder
    {
        public String listingReference;
        public ImageView imageView;
        public TextView textView;

        /**
         * Constructor used to setup imageView and textView variables.
         * @param itemView Represents the view.
         * @param listener Represents the passed in musician adapter.
         * @since #0.2b
         */
        public AdapterViewHolder(@NonNull View itemView, MusiciansBandsAdapter.OnItemClickListener listener)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.band_picture);
            textView = itemView.findViewById(R.id.band_name);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                /**
                 * This method is used to handle the click on a Card.
                 * @param v Represents the view.
                 * @since #0.2b
                 */
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

    /**
     * Constructor used to initialise the musiciansBandsArrayList and the context.
     * @param musicianBandsList
     * @param context
     * @since #0.2b
     */
    public MusiciansBandsAdapter(ArrayList<MusiciansBands> musicianBandsList, Context context)
    {
        this.musiciansBandsArrayList = musicianBandsList;
        this.context = context;
    }

    /**
     * This method is used to create the view holder uponc reation of the class.
     * @param parent Represents the parent o fthe class.
     * @param viewType Represents the viewType.
     * @return Returns the created adapter view holder.
     * @since #0.2b
     */
    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_musician_bands, parent, false);

        return new AdapterViewHolder(view, listener);
    }

    /**
     * This method is used to load an image from Firebase for the Card.
     * @param holder Represents the adapter view holder.
     * @param position Represents the position in the ArrayList of the selected card.
     * @since #0.2b
     */
    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position)
    {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        MusiciansBands currentBand = musiciansBandsArrayList.get(position);

        holder.listingReference = currentBand.getReference();

        docRef = DB.collection("bands").document(currentBand.getReference());

        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            /**
             * This method is used to determine the completion of a get request of Firebase.
             * @param task References the result of the get request.
             * @since #0.2b
             */
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
                        StorageReference bandPic = STORAGE.getReference().child("/images/bands/" + currentBand.getReference() + ".jpg");
                        GlideApp.with(holder.imageView.getContext())
                                .load(bandPic)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .skipMemoryCache(false)
                                .into(holder.imageView);
                    }
                }
            }
        });
    }

    /**
     * This method is used to get the size of the musiciansBandsArrayList.
     * @return Returns the size of the musiciansBandsArrayList as an int.
     * @since #0.2b
     */
    @Override
    public int getItemCount()
    {
        return musiciansBandsArrayList.size();
    }
}
