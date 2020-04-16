package com.gangoffive.rig2gig.advert.management;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gangoffive.rig2gig.R;

import java.util.List;

public class DeleteInstrumentAdapter extends BaseAdapter
{
    List currentInstruments;
    Activity context;

    /**
     * DeleteInstrumentAdapter constructor
     * @param instruments list of current instruments
     * @param con activity creating adapter
     */
    public DeleteInstrumentAdapter(List instruments, Activity con)
    {
        currentInstruments = instruments;
        context = con;
    }

    /**
     * get count of currentInstruments
     * @return size of currentInstruments
     */
    @Override
    public int getCount() {
        return currentInstruments.size();
    }

    /**
     * Unused
     * @param position position
     * @return null
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * Unused
     * @param position position
     * @return 0
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Returns position 'button' containing position name and cross image
     * @param position of instrument clicked
     * @param convertView view from which position was clicked
     * @param parent viewgroup from which position was clicked
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = context.getLayoutInflater().inflate(R.layout.deletable_text_view, null);
        TextView instrument = view.findViewById(R.id.instrument);
        ImageView delete = view.findViewById(R.id.delete);
        instrument.setText(currentInstruments.get(position).toString());
        return view;
    }
}
