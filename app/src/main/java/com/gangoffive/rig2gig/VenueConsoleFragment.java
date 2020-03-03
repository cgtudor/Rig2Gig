package com.gangoffive.rig2gig;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class VenueConsoleFragment extends Fragment implements View.OnClickListener
{
    private List<DocumentSnapshot> venueAdverts;
    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = FSTORE.collection("venue-listings");
    private Query getVenueAdverts = collectionReference;;
    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";
    boolean advertExists;
    String venueLogin = "cavWo1C735Rft0NHvhcL";
    View view;

    private CardView card_view_view_performers;
    private CardView card_view_edit_venue;
    private CardView card_view_create_advert;
    private CardView card_view_edit_advert;
    private CardView card_view_view_advert;
    private CardView card_view_delete_advert;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_venue_console, container, false);

        card_view_view_performers = (CardView) view.findViewById(R.id.card_view_view_performers);
        card_view_edit_venue = (CardView) view.findViewById(R.id.card_view_edit_venue);
        card_view_create_advert = (CardView) view.findViewById(R.id.card_view_create_advert);
        card_view_edit_advert = (CardView) view.findViewById(R.id.card_view_edit_advert);
        card_view_view_advert = (CardView) view.findViewById(R.id.card_view_view_advert);
        card_view_delete_advert = (CardView) view.findViewById(R.id.card_view_delete_advert);

        card_view_view_performers.setOnClickListener(this);
        card_view_edit_venue.setOnClickListener(this);
        card_view_create_advert.setOnClickListener(this);
        card_view_edit_advert.setOnClickListener(this);
        card_view_view_advert.setOnClickListener(this);
        card_view_delete_advert.setOnClickListener(this);

        databaseQuery();

        return view;
    }

    private void databaseQuery()
    {
        getVenueAdverts.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                System.out.println("ENTERED ON COMPLETE METHOD-----------------------------------------------");

                venueAdverts = queryDocumentSnapshots.getDocuments();
                if(!venueAdverts.isEmpty())
                {
                    Log.d(TAG, "get successful with data");

                    for(DocumentSnapshot adverts : venueAdverts)
                    {
                        CardView editProfileLayout;

                        if(adverts.get("venue-ref").toString().equals(venueLogin))
                        {
                            advertExists = true;
                            System.out.println("PRINT OUT IN THE MIDDLE OF THE DATABASEQUERY() ============ " + advertExists);

                            editProfileLayout = (CardView) view.findViewById(R.id.card_view_view_performers);
                            editProfileLayout.setVisibility(View.VISIBLE);

                            editProfileLayout = (CardView) view.findViewById(R.id.card_view_edit_venue);
                            editProfileLayout.setVisibility(View.VISIBLE);

                            editProfileLayout = (CardView) view.findViewById(R.id.card_view_edit_advert);
                            editProfileLayout.setVisibility(View.VISIBLE);

                            editProfileLayout = (CardView) view.findViewById(R.id.card_view_view_advert);
                            editProfileLayout.setVisibility(View.VISIBLE);

                            editProfileLayout = (CardView) view.findViewById(R.id.card_view_delete_advert);
                            editProfileLayout.setVisibility(View.VISIBLE);
                            break;
                        }
                        else
                        {
                            editProfileLayout = (CardView) view.findViewById(R.id.card_view_view_performers);
                            editProfileLayout.setVisibility(View.VISIBLE);

                            editProfileLayout = (CardView) view.findViewById(R.id.card_view_edit_venue);
                            editProfileLayout.setVisibility(View.VISIBLE);

                            editProfileLayout = (CardView) view.findViewById(R.id.card_view_create_advert);
                            editProfileLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
                else
                {
                    Log.d(TAG, "get successful without data");
                    advertExists = false;
                }
            }
        });

        System.out.println("PRINT OUT AT THE END OF DATABASEQUERY() ============ " + advertExists);
    }

    @Override
    public void onClick(View v)
    {
        
    }
}
