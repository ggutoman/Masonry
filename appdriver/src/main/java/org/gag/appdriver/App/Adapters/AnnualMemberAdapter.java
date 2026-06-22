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

import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.Room.Entities.EMemberInfo;

import java.util.ArrayList;
import java.util.List;

public class AnnualMemberAdapter extends ArrayAdapter<AnnualMembers> {

    private final Context loContext;
    public final List<AnnualMembers> loMember;

    public List<AnnualMembers> loMemberFiltered;

    public AnnualMemberAdapter(@NonNull Context context, int resource, @NonNull List<AnnualMembers> objects) {
        super(context, resource, objects);

        loContext = context;
        loMember = objects;
        loMemberFiltered = objects;
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
        textView.setText(loMemberFiltered.get(position).getSMemberNme());

        return view;

    }

    @Override
    public int getCount() {
        return loMemberFiltered.size();
    }

    @Nullable
    @Override
    public AnnualMembers getItem(int position) {
        return loMemberFiltered.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<AnnualMembers> results = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    results.addAll(loMember);
                } else {
                    for (AnnualMembers memberInfo : loMember) {

                        if (memberInfo.getSMemberNme().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            results.add(memberInfo);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = results;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                loMemberFiltered = (List<AnnualMembers>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}