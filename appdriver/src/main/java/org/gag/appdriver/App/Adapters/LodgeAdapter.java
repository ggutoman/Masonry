package org.gag.appdriver.App.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.gag.appdriver.Room.Entities.ELodgeInfo;

import java.util.List;

public class LodgeAdapter extends ArrayAdapter<ELodgeInfo> {

    private final Context loContext;
    public final List<ELodgeInfo> lodges;

    public LodgeAdapter(@NonNull Context context, int resource, @NonNull List<ELodgeInfo> objects) {
        super(context, resource, objects);

        loContext = context;
        lodges = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(loContext);
            view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }

        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(lodges.get(position).getSLodgeNme());

        return view;

    }
}