package org.gag.appdriver.App.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.gag.appdriver.Room.Entities.EMemberContactInfo;

import java.util.ArrayList;
import java.util.List;

public class MemberContactAdapter extends ArrayAdapter<EMemberContactInfo> {

    private final Context loContext;
    private final List<EMemberContactInfo> laMemberContact;
    private List<EMemberContactInfo> laMemberContactFiltered;

    public MemberContactAdapter(@NonNull Context context, int resource, @NonNull List<EMemberContactInfo> objects) {
        super(context, resource, objects);

        loContext = context;
        laMemberContact = objects;
        laMemberContactFiltered = objects;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(loContext);
            view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }

        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(laMemberContactFiltered.get(position).getSContctNo());

        return view;

    }

    @Override
    public int getCount() {
        return laMemberContactFiltered.size();
    }

    @Nullable
    @Override
    public EMemberContactInfo getItem(int position) {
        return laMemberContactFiltered.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<EMemberContactInfo> results = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    results.addAll(laMemberContact);
                } else {
                    for (EMemberContactInfo contactInfo : laMemberContact) {
                        if (contactInfo.getSContctNo().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                contactInfo.getSRemarksx().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            results.add(contactInfo);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = results;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                laMemberContactFiltered = (List<EMemberContactInfo>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}