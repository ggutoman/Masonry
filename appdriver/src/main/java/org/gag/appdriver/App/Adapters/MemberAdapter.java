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

import org.gag.appdriver.Room.Entities.ELodgeInfo;
import org.gag.appdriver.Room.Entities.EMemberEmailInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends ArrayAdapter<EMemberInfo> {

    private final Context loContext;
    public final List<EMemberInfo> loMember;

    public List<EMemberInfo> loMemberFiltered;

    public MemberAdapter(@NonNull Context context, int resource, @NonNull List<EMemberInfo> objects) {
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

        String lsMiddlNme = loMemberFiltered.get(position).getSMiddName() == null ? "" : loMemberFiltered.get(position).getSMiddName();
        String lsSuffix = loMemberFiltered.get(position).getSSuffixNm() == null ? "" : loMemberFiltered.get(position).getSSuffixNm();

        String lsMemberNm = loMemberFiltered.get(position).getSFrstName() + " " +
                lsMiddlNme + " " +
                loMemberFiltered.get(position).getSLastName() + " " +
                lsSuffix;

        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(lsMemberNm);

        return view;

    }

    @Override
    public int getCount() {
        return loMemberFiltered.size();
    }

    @Nullable
    @Override
    public EMemberInfo getItem(int position) {
        return loMemberFiltered.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<EMemberInfo> results = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    results.addAll(loMember);
                } else {
                    for (EMemberInfo memberInfo : loMember) {

                        String lsMiddlNme = memberInfo.getSMiddName() == null ? "" : memberInfo.getSMiddName();
                        String lsSuffix = memberInfo.getSSuffixNm() == null ? "" : memberInfo.getSSuffixNm();

                        String lsMemberNm = memberInfo.getSFrstName() + " " +
                                lsMiddlNme + " " +
                                memberInfo.getSLastName() + " " +
                                lsSuffix;

                        if (lsMemberNm.toLowerCase().contains(constraint.toString().toLowerCase())) {
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
                loMemberFiltered = (List<EMemberInfo>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}