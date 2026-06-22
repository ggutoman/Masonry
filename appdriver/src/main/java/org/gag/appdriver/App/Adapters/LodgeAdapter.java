package org.gag.appdriver.App.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.R;
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
            view = inflater.inflate(R.layout.adapter_list_lodge, parent, false);
        }

        MaterialTextView mtv_name = view.findViewById(R.id.mtv_name);
        MaterialTextView mtv_address = view.findViewById(R.id.mtv_address);

        mtv_name.setText(lodges.get(position).getSLodgeNme());

        String lsFullAddress = lodges.get(position).getSAddressx() +  ", " + lodges.get(position).getSTownName() + " " + lodges.get(position).getSProvName();
        mtv_address.setText(lsFullAddress);

        return view;

    }
}