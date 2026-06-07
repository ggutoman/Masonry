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
import org.gag.appdriver.Room.Entities.EPosition;

import java.util.ArrayList;
import java.util.List;

public class PositionAdapter extends ArrayAdapter<EPosition> {

    private final Context loContext;
    public final List<EPosition> positions;

    public List<EPosition> positionsFiltered;

    public PositionAdapter(@NonNull Context context, int resource, @NonNull List<EPosition> objects) {
        super(context, resource, objects);

        loContext = context;
        positions = objects;
        positionsFiltered = objects;
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
        textView.setText(positionsFiltered.get(position).getSPositnDs());

        return view;

    }

    @Override
    public int getCount() {
        return positionsFiltered.size();
    }

    @Nullable
    @Override
    public EPosition getItem(int position) {
        return positionsFiltered.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<EPosition> results = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    results.addAll(positions);
                } else {
                    for (EPosition positionInfo : positions) {
                        if (positionInfo.getSPositnDs().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            results.add(positionInfo);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = results;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                positionsFiltered = (List<EPosition>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}