package com.gangoffive.rig2gig;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class DeleteInstrumentAdapter extends BaseAdapter
{
    List currentInstruments;
    Activity context;

    DeleteInstrumentAdapter(List instruments, Activity con)
    {
        currentInstruments = instruments;
        context = con;
    }

    @Override
    public int getCount() {
        return currentInstruments.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = context.getLayoutInflater().inflate(R.layout.deletable_text_view, null);
        TextView instrument = view.findViewById(R.id.instrument);
        ImageView delete = view.findViewById(R.id.delete);
        instrument.setText(currentInstruments.get(position).toString());
        return view;
    }
}
