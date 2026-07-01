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
import org.gag.appdriver.App.Models.ProjectDetail;

import java.util.ArrayList;
import java.util.List;

public class ProjectMemberrAdapter extends ArrayAdapter<ProjectDetail> {

    private final Context loContext;
    public final List<ProjectDetail> loProject;

    public List<ProjectDetail> loProjectFiltered;

    public ProjectMemberrAdapter(@NonNull Context context, int resource, @NonNull List<ProjectDetail> objects) {
        super(context, resource, objects);

        loContext = context;
        loProject = objects;
        loProjectFiltered = objects;
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
        textView.setText(loProjectFiltered.get(position).getSMemberNme());

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