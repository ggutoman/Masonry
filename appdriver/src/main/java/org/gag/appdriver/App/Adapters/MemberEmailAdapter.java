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

import org.gag.appdriver.Room.Entities.EMemberEmailInfo;

import java.util.ArrayList;
import java.util.List;

public class MemberEmailAdapter extends ArrayAdapter<EMemberEmailInfo> {

    private final Context loContext;
    private final List<EMemberEmailInfo> laMemberEmail;
    private List<EMemberEmailInfo> laMemberEmailFiltered;

    public MemberEmailAdapter(@NonNull Context context, int resource, @NonNull List<EMemberEmailInfo> objects) {
        super(context, resource, objects);

        loContext = context;
        laMemberEmail = objects;
        laMemberEmailFiltered = objects;
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
        textView.setText(laMemberEmail.get(position).getSEmailAdd());

        return view;

    }

    @Override
    public int getCount() {
        return laMemberEmailFiltered.size();
    }

    @Nullable
    @Override
    public EMemberEmailInfo getItem(int position) {
        return laMemberEmailFiltered.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<EMemberEmailInfo> results = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    results.addAll(laMemberEmail);
                } else {
                    for (EMemberEmailInfo emailInfo : laMemberEmail) {
                        if (emailInfo.getSEmailAdd().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            results.add(emailInfo);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = results;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                laMemberEmailFiltered = (List<EMemberEmailInfo>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}