package org.gag.appdriver.App.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.gag.appdriver.App.Models.TownProvince;
import org.gag.appdriver.Room.DataObject.DTownInfo;

import java.util.ArrayList;
import java.util.List;

public class TownCityAdapter extends ArrayAdapter<TownProvince> {

    private final Context loContext;
    private final List<TownProvince> towncity;
    private List<TownProvince> towncityFiltered;

    public TownCityAdapter(@NonNull Context context, int resource, @NonNull List<TownProvince> objects) {
        super(context, resource, objects);

        loContext = context;
        towncity = objects;
        towncityFiltered = objects;
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
        textView.setText(towncityFiltered.get(position).getPsTownProvNme());

        return view;

    }

    @Override
    public int getCount() {
        return towncityFiltered.size();
    }

    @Nullable
    @Override
    public TownProvince getItem(int position) {
        return towncityFiltered.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<TownProvince> results = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    results.addAll(towncity);
                } else {
                    for (TownProvince town : towncity) {
                        if (town.getPsTownProvNme().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                town.getPsAddressx().toLowerCase().contains(constraint.toString().toLowerCase())) {

                            results.add(town);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = results;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                towncityFiltered = (List<TownProvince>) results.values;

                notifyDataSetChanged();
            }
        };
    }
}