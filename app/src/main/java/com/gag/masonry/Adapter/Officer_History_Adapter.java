package com.gag.masonry.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.OfficerHistory;
import org.gag.appdriver.R;
import org.gag.appdriver.Room.Entities.EMemberInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Officer_History_Adapter extends RecyclerView.Adapter<Officer_History_Adapter.VH_Officer_History> {

    private final List<OfficerHistory> laOfficerHistory;
    private final List<String> laNmes = new ArrayList<>();

    private final HashMap<String, HashMap<String, List<OfficerHistory>>> loMapHistory = new HashMap<>();

    public Officer_History_Adapter(List<OfficerHistory> faOfficerHistory){

        this.laOfficerHistory = faOfficerHistory;

        Set<String> addedMemberIds = new HashSet<>();

        //group record by member id
        for (OfficerHistory loItem : laOfficerHistory) {

            String memberId = loItem.getSMemberID();
            String memberName = loItem.getSMemberNme();
            String yearId = loItem.getNYearxxxx();

            // only add if member ID not in set
            if (!addedMemberIds.contains(memberId)) {
                laNmes.add(memberName);
                addedMemberIds.add(memberId);
            }

            //map history detail if member id and year id is not in set
            loMapHistory
                    .computeIfAbsent(memberId, k -> new HashMap<>())
                    .computeIfAbsent(yearId, k -> new ArrayList<>())
                    .add(loItem);
        }
    }

    @NonNull
    @Override
    public Officer_History_Adapter.VH_Officer_History onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new VH_Officer_History(
                LayoutInflater.from(parent.getContext()).inflate(
                        com.gag.masonry.R.layout.list_officer_history_group,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Officer_History holder, int position) {

        String memberId = laOfficerHistory.get(position).getSMemberID();
        String memberName = laOfficerHistory.get(position).getSMemberNme();

        holder.mtv_name.setText(memberName);
        holder.mtv_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.mtv_view.getText().toString().equalsIgnoreCase("show")){
                    holder.rcv_details.setVisibility(View.VISIBLE);
                    holder.mtv_view.setText("Hide");
                }else{
                    holder.rcv_details.setVisibility(View.GONE);
                    holder.mtv_view.setText("Show");
                }
            }
        });

        // Get the year map for this member
        HashMap<String, List<OfficerHistory>> yearMap = loMapHistory.get(memberId);

        // Create expandable adapter (must extend BaseExpandableListAdapter)
        OfficerHistoryExpandableAdapter expandableAdapter =
                new OfficerHistoryExpandableAdapter(memberName, yearMap);

        holder.rcv_details.setAdapter(expandableAdapter);
    }


    @Override
    public int getItemCount() {
        return laNmes.size();
    }

    public static class VH_Officer_History extends RecyclerView.ViewHolder{

        private MaterialTextView mtv_name, mtv_view;
        private ExpandableListView rcv_details;

        public VH_Officer_History(@NonNull View itemView) {
            super(itemView);

            mtv_name = itemView.findViewById(com.gag.masonry.R.id.mtv_name);
            mtv_view = itemView.findViewById(com.gag.masonry.R.id.mtv_view);
            rcv_details = itemView.findViewById(com.gag.masonry.R.id.rcv_details);
        }
    }
}
