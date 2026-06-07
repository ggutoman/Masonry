package com.gag.masonry.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.masonry.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Room.DataObject.DOfficer;
import org.gag.appdriver.Room.Entities.EMemberInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Adapter_Officer_List extends RecyclerView.Adapter<Adapter_Officer_List.Adapter_Member_List_Holder> {

    private final Context loInstance;
    private final List<DOfficer.OfficerList> laOfficers;
    private final Adapter_OfficerList_Filter loFilter;
    private final OnSelect poCallback;

    private List<DOfficer.OfficerList> laOfficersFiltered;

    public Adapter_OfficerList_Filter GetFilter(){
        return loFilter;
    }

    public interface OnSelect{
        void Selected(DOfficer.OfficerList poItem);
    }

    public Adapter_Officer_List(Context foContext, List<DOfficer.OfficerList> faMembers, OnSelect foCallback){
        this.loInstance = foContext;
        this.laOfficers = faMembers;
        this.laOfficersFiltered = laOfficers;
        this.loFilter = new Adapter_OfficerList_Filter(this);
        this.poCallback = foCallback;
    }

    @NonNull
    @Override
    public Adapter_Member_List_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new Adapter_Member_List_Holder(
                LayoutInflater.from(loInstance).inflate(R.layout.list_officers, parent, false)
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Adapter_Member_List_Holder holder, int position) {

        DOfficer.OfficerList loOfficer = laOfficersFiltered.get(position);

        holder.mtv_name.setText(loOfficer.getSMemberNme());
        holder.mtv_position.setText(loOfficer.getSPositionNme());

        switch (loOfficer.getCStatusxx()){
            case "0":
                holder.mtv_status.setText("Suspended");
                break;
            case "1":
                holder.mtv_status.setText("Active/Incumbent");
                break;
            case "2":
                holder.mtv_status.setText("Reassigned");
                break;
            case "3":
                holder.mtv_status.setText("Removed");
                break;
            case "4":
                holder.mtv_status.setText("Resigned");
                break;
            case "5":
                holder.mtv_status.setText("Deceased");
                break;
            default:
                holder.mtv_status.setText("N/A");
                break;
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                poCallback.Selected(loOfficer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return laOfficersFiltered.size();
    }

    public class Adapter_OfficerList_Filter extends Filter{

        private final Adapter_Officer_List loAdapter;
        private List<String> laStatus = new ArrayList<>(List.of("0", "1", "2", "3", "4", "5"));

        public void InitStatus(List<String> faStatus){
            laStatus = faStatus;
        }

        public Adapter_OfficerList_Filter(Adapter_Officer_List foAdapter){
            loAdapter = foAdapter;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            if (charSequence.length() < 1){
                laOfficersFiltered = laOfficers;
            }else {

                List<DOfficer.OfficerList> filterSearch = new ArrayList<>();

                //first filter, via search text
                for (DOfficer.OfficerList loOfficer : laOfficers){

                    if (loOfficer.getSMemberNme().toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                            loOfficer.getSPositionNme().toLowerCase().contains(charSequence.toString().toLowerCase())){

                        filterSearch.add(loOfficer);
                    }
                }

                laOfficersFiltered = filterSearch;
            }

            laOfficersFiltered = laOfficersFiltered.stream()
                    .filter(loOfficer -> laStatus.contains(loOfficer.getCStatusxx()))
                    .collect(Collectors.toList());

            FilterResults loResults = new FilterResults();
            loResults.values = laOfficersFiltered;
            loResults.count = laOfficersFiltered.size();

            return loResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            laOfficersFiltered = (List<DOfficer.OfficerList>) filterResults.values;
            loAdapter.notifyDataSetChanged();
        }
    }

    public static class Adapter_Member_List_Holder extends RecyclerView.ViewHolder{

        private final View view;
        private final MaterialTextView mtv_name;
        private final MaterialTextView mtv_position;
        private final MaterialTextView mtv_status;

        public Adapter_Member_List_Holder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            mtv_name = itemView.findViewById(R.id.mtv_name);
            mtv_position = itemView.findViewById(R.id.mtv_position);
            mtv_status = itemView.findViewById(R.id.mtv_status);
        }
    }
}
