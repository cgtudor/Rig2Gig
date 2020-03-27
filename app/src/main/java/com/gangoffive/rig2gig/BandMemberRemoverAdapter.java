package com.gangoffive.rig2gig;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class BandMemberRemoverAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<Drawable> images;
    private ArrayList<String> names;
    private List memberRefs;
    private LayoutInflater inflater;
    private StorageReference imageRef;
    private FirebaseStorage storage;
    private ManageBandMembersActivity bandManager;


    public BandMemberRemoverAdapter(ArrayList memberNames, List refs, ManageBandMembersActivity con)
    {
        context = con;
        bandManager = con;
        inflater = LayoutInflater.from(context);
        names = memberNames;
        memberRefs = refs;
        storage = FirebaseStorage.getInstance();
        images = new ArrayList();
        for (String member : names)
        {
            images.add(null);
        }
    }


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
            convertView = inflater.inflate(R.layout.band_member_list_item, parent, false);

        }
        /* View view = context.getLayoutInflater().inflate(R.layout.band_member_list_item, null);*/
        ImageView image = (ImageView)convertView.findViewById(R.id.bandMemberImage);
        imageRef = storage.getReference().child("/images/musicians/" + memberRefs.get(position) + ".jpg");
        if (images.get(position) == null)
        {
            GlideApp.with(context)
                    .load(imageRef)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
/*                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            images.set(position, resource);
                            return false;
                        }
                    })*/
                    .into(image);
        }
/*        else
        {
            image.setImageDrawable(images.get(position));
        }*/
        TextView name = (TextView)convertView.findViewById(R.id.firstName);
        name.setText(names.get(position).toString());
        TextView remove = (TextView)convertView.findViewById(R.id.remove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                bandManager.confirmRemoveMember(names.get(position).toString(), position);
            }
        });
        return convertView;
    }
}