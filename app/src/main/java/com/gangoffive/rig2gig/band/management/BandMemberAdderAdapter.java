package com.gangoffive.rig2gig.band.management;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gangoffive.rig2gig.firebase.GlideApp;
import com.gangoffive.rig2gig.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

public class BandMemberAdderAdapter  extends BaseAdapter
{
    private Context context;
    private ArrayList<Drawable> images;
    private ArrayList<String> names;
    private ArrayList<Boolean> invitesSent;
    private List musicianRefs, userRefs;
    private LayoutInflater inflater;
    private StorageReference imageRef;
    private FirebaseStorage storage;
    private MusicianSearchActivity bandManager;
    private FirebaseFirestore db;

    /**
     * Constructor for BandMemberAdderAdapter
     * @param musicianNames names of musicians
     * @param musicRefs musician ids
     * @param userIds user ids
     * @param invited list of those already invited
     * @param con activity using adapter
     */
    public BandMemberAdderAdapter(ArrayList musicianNames, List musicRefs, List userIds, ArrayList invited, MusicianSearchActivity con)
    {
        db = FirebaseFirestore.getInstance();
        context = con;
        bandManager = con;
        inflater = LayoutInflater.from(context);
        names = musicianNames;
        musicianRefs = musicRefs;
        userRefs = userIds;
        invitesSent = invited;
        storage = FirebaseStorage.getInstance();
        images = new ArrayList();
        for (String member : names)
        {
            images.add(null);
        }
    }

    /**
     * @return size of names
     */
    @Override
    public int getCount() {
        if (names != null)
        {
            return names.size();
        }
        return 0;
    }

    /**
     * Unused
     * @param position position
     * @return null
     */
    @Override
    public Object getItem(int position) {
        return position;
    }

    /**
     * Unused
     * @param position position
     * @return 0
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Returns 'textView' containing image, name and 'remove from band' text view
     * @param position of instrument clicked
     * @param convertView view from which position was clicked
     * @param parent viewgroup from which position was clicked
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.band_member_adder_item, parent, false);

        }
        /* View view = context.getLayoutInflater().inflate(R.layout.band_member_list_item, null);*/
        ImageView image = (ImageView)convertView.findViewById(R.id.bandMemberImage);
        imageRef = storage.getReference().child("/images/musicians/" + musicianRefs.get(position) + ".jpg");
        if (images.get(position) == null)
        {
            GlideApp.with(context)
                    .load(imageRef)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(image);
        }
        TextView name = (TextView)convertView.findViewById(R.id.firstName);
        name.setText(names.get(position).toString());
        TextView invite = (TextView)convertView.findViewById(R.id.invite);

        if (invitesSent.get(position) == true)
        {
            image.setAlpha(.5f);
            name.setAlpha(.5f);
            invite.setText("Invited");
            invite.setAlpha(.5f);
        }
        else
        {
            invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (!((MusicianSearchActivity)context).isInvitingMember())
                    {
                        ((MusicianSearchActivity)context).setInvitingMember(true);
                        bandManager.beginConfirmAddMember(names.get(position).toString(), position);
                    }

                }
            });
        }
        return convertView;
    }
}
