package com.gag.accounting.Adapter.Project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.accounting.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Room.Entities.EFundTurnOver;
import org.gag.appdriver.Room.Entities.EProjectMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Adapter_Project_List extends RecyclerView.Adapter<Adapter_Project_List.VH_Project_List> {

    private final Context loInstance;
    private final List<EProjectMaster> laProjects;
    private final Adapter_Project_List_Filter loFilter = new Adapter_Project_List_Filter(this);
    private final OnSelectProject loCallback;

    private List<EProjectMaster> laProjectFiltered;

    public interface OnSelectProject {
        void OnSelect(EProjectMaster loTurnover);
    }

    public Adapter_Project_List(Context context, List<EProjectMaster> faProjects, OnSelectProject foCallback){
        loInstance = context;
        laProjects = faProjects;
        loCallback = foCallback;
        laProjectFiltered = faProjects;
    }

    public Adapter_Project_List_Filter GetFilter(){
        return loFilter;
    }

    @NonNull
    @Override
    public VH_Project_List onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new VH_Project_List(
                LayoutInflater.from(loInstance)
                        .inflate(R.layout.list_item_projects,
                                parent,
                                false
                        )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Project_List holder, int position) {

        holder.mtv_name.setText(laProjectFiltered.get(position).getSProjctNm());
        holder.mtv_date.setText(laProjectFiltered.get(position).getDTransact());

        switch (laProjectFiltered.get(position).getCProjctTp()){

            case "0":
                holder.mtv_type.setText("Brick Project");
                break;
            case "1":
                holder.mtv_type.setText("Stone Project");
                break;
            case "2":
                holder.mtv_type.setText("Concrete Project");
                break;
            case "3":
                holder.mtv_type.setText("Glass Block Project");
                break;
            case "4":
                holder.mtv_type.setText("Adobe Project");
                break;
            case "5":
                holder.mtv_type.setText("Reinforced Project");
                break;
        }

        switch (laProjectFiltered.get(position).getCTranStat()){

            case "0":
                holder.mtv_status.setText("Planned");
                break;
            case "1":
                holder.mtv_status.setText("On Going");
                break;
            case "2":
                holder.mtv_status.setText("On Hold");
                break;
            case "3":
                holder.mtv_status.setText("Completed");
                break;
            case "4":
                holder.mtv_status.setText("Cancelled");
                break;
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loCallback.OnSelect(laProjectFiltered.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return laProjectFiltered.size();
    }

    public class Adapter_Project_List_Filter extends Filter{

        private final Adapter_Project_List loAdapter;
        private List<String> laStatus = new ArrayList<>(List.of("0", "1", "2", "3", "4"));
        private List<String> laTypes = new ArrayList<>(List.of("0", "1", "2", "3", "4", "5"));

        public void InitStatus(List<String> faStatus){
            laStatus = faStatus;
        }

        public void InitTypes(List<String> faTypes){
            laTypes = faTypes;
        }

        public Adapter_Project_List_Filter(Adapter_Project_List foAdapter){
            loAdapter = foAdapter;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            if (charSequence.length() < 1){
                laProjectFiltered = laProjects;
            }else {

                List<EProjectMaster> filterSearch = new ArrayList<>();

                //first filter, via search text
                for (EProjectMaster loProject : laProjects){

                    if (loProject.getSProjctNm().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filterSearch.add(loProject);
                    }
                }
                laProjectFiltered = filterSearch;
            }

            laProjectFiltered = laProjectFiltered.stream()
                    .filter(loItem -> laStatus.contains(loItem.getCTranStat()) && laTypes.contains(loItem.getCProjctTp()) )
                    .collect(Collectors.toList());

            FilterResults loResults = new FilterResults();
            loResults.values = laProjectFiltered;
            loResults.count = laProjectFiltered.size();

            return loResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            laProjectFiltered = (List<EProjectMaster>) filterResults.values;
            loAdapter.notifyDataSetChanged();
        }
    }

    public static class VH_Project_List extends RecyclerView.ViewHolder {

        private View view;
        private MaterialTextView mtv_name, mtv_type, mtv_status, mtv_date;

        public VH_Project_List(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            mtv_name = itemView.findViewById(R.id.mtv_name);
            mtv_type = itemView.findViewById(R.id.mtv_type);
            mtv_status = itemView.findViewById(R.id.mtv_status);
            mtv_date = itemView.findViewById(R.id.mtv_date);
        }
    }
}
