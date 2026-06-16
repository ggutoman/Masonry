package org.gag.appdriver.App.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.R;

import java.util.ArrayList;
import java.util.List;

public class LodgeCalendarAdapter extends ArrayAdapter<LodgeCalendarList> {

    private final Context loContext;
    public final List<LodgeCalendarList> lodges;

    public List<LodgeCalendarList> lodgesFiltered;

    public LodgeCalendarAdapter(@NonNull Context context, int resource, @NonNull List<LodgeCalendarList> objects) {
        super(context, resource, objects);

        loContext = context;
        lodges = objects;
        lodgesFiltered = objects;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(loContext);
            view = inflater.inflate(R.layout.adapter_list_lodge_calendar, parent, false);
        }

        MaterialTextView mtv_descr= view.findViewById(R.id.mtv_descr);
        MaterialTextView mtv_validity= view.findViewById(R.id.mtv_validity);

        mtv_descr.setText(lodgesFiltered.get(position).getSLodgeNme() + " " + "(" + lodgesFiltered.get(position).getNYearxxxx() + ")");
        mtv_validity.setText(lodgesFiltered.get(position).getDThruDate());

        return view;

    }

    @Override
    public int getCount() {
        return lodgesFiltered.size();
    }

    @Nullable
    @Override
    public LodgeCalendarList getItem(int position) {
        return lodgesFiltered.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<LodgeCalendarList> results = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    results.addAll(lodges);
                } else {
                    for (LodgeCalendarList lodgeCalendar : lodges) {

                        if (lodgeCalendar.getSLodgeNme().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                lodgeCalendar.getNYearxxxx().equalsIgnoreCase(constraint.toString().toLowerCase())) {

                            results.add(lodgeCalendar);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = results;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                lodgesFiltered = (List<LodgeCalendarList>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}