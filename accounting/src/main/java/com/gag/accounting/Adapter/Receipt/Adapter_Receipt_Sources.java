package com.gag.accounting.Adapter.Receipt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gag.accounting.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Constants.RECEIPT_CONSTANTS;

import java.util.List;

public class Adapter_Receipt_Sources extends ArrayAdapter<RECEIPT_CONSTANTS> {

    private final Context loInstance;
    private final List<RECEIPT_CONSTANTS> laSources;

    public Adapter_Receipt_Sources(@NonNull Context context, List<RECEIPT_CONSTANTS> sources) {
        super(context, 0, sources);

        loInstance = context;
        laSources = sources;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {

            LayoutInflater inflater = LayoutInflater.from(loInstance);
            view = inflater.inflate(R.layout.list_item_sources, parent, false);
        }

        MaterialTextView mtv_descr= view.findViewById(R.id.mtv_descr);
        MaterialTextView mtv_id= view.findViewById(R.id.mtv_id);

        mtv_descr.setText(laSources.get(position).getFsDescript());
        mtv_id.setText(laSources.get(position).getFsID());

        return view;

    }

    @Override
    public int getCount() {
        return laSources.size();
    }

    @Nullable
    @Override
    public RECEIPT_CONSTANTS getItem(int position) {
        return laSources.get(position);
    }

}
