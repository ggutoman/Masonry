package com.gag.accounting.Adapter.Project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.ProjectDetail;
import org.gag.appdriver.Libraries.DateUtil.DateRepository;
import org.gag.appdriver.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Project_Member_Entry extends ArrayAdapter<ProjectDetail> {

    private final Context loContext;
    private final DateRepository poDate;
    public final List<ProjectDetail> loProject;

    public List<ProjectDetail> loProjectFiltered;

    public Adapter_Project_Member_Entry(@NonNull Context context, int resource, @NonNull List<ProjectDetail> objects) {
        super(context, resource, objects);

        loContext = context;

        poDate = new DateRepository();
        loProject = objects;
        loProjectFiltered = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(loContext);
            view = inflater.inflate(R.layout.list_pledge_member_entry, parent, false);
        }


        MaterialTextView mtv_name = view.findViewById(R.id.mtv_name);
        MaterialTextView mtv_amt_pledge = view.findViewById(R.id.mtv_amt_pledge);
        MaterialTextView mtv_date = view.findViewById(R.id.mtv_date);

        mtv_name.setText(loProjectFiltered.get(position).getSMemberNme());
        mtv_amt_pledge.setText(loProjectFiltered.get(position).getNPledgexx());
        mtv_date.setText("Pledged until " + poDate.FormatDate(loProjectFiltered.get(position).getDPledgexx(), "MMMM dd, yyyy"));

        return view;

    }

    @Override
    public int getCount() {
        return loProjectFiltered.size();
    }

    @Nullable
    @Override
    public ProjectDetail getItem(int position) {
        return loProjectFiltered.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<ProjectDetail> results = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    results.addAll(loProject);
                } else {
                    for (ProjectDetail projectInfo : loProject) {

                        if (projectInfo.getSMemberNme().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            results.add(projectInfo);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = results;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                loProjectFiltered = (List<ProjectDetail>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}