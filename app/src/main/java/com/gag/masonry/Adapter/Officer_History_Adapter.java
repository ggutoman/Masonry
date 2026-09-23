package com.gag.masonry.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.OfficerHistory;
import org.gag.appdriver.Libraries.DateUtil.DateRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Officer_History_Adapter extends RecyclerView.Adapter<Officer_History_Adapter.VH_Officer_History> {

    private final DateRepository poDate;
    private final List<OfficerHistory> laOfficerHistory;
    private final List<OfficerHistory> laOfficerHistoryFiltered;
    private final Boolean isByMember;

    private final List<String> laNmes = new ArrayList<>();
    private final HashMap<String, List<OfficerHistory>> loMapHistory = new HashMap<>();

    public Officer_History_Adapter(List<OfficerHistory> faOfficerHistory, Boolean isByMember){

        this.poDate = new DateRepository();
        this.laOfficerHistory = faOfficerHistory;
        this.laOfficerHistoryFiltered = laOfficerHistory;
        this.isByMember = isByMember;

        if (!this.isByMember){

            //group record by member id
            for (OfficerHistory loItem : laOfficerHistoryFiltered) {

                String memberName = loItem.getSMemberNme();

                // only add if member name not in set
                if (!laNmes.contains(memberName)) {
                    laNmes.add(memberName);
                }

                //map history detail if member name is not in set
                if (!loMapHistory.containsKey(memberName)) loMapHistory.put(loItem.getSMemberNme(), new ArrayList<>());
                Objects.requireNonNull(loMapHistory.get(loItem.getSMemberNme())).add(loItem);
            }
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

        holder.mtv_dtransact.setVisibility(isByMember ? View.VISIBLE : View.GONE);
        holder.mtv_name.setVisibility(isByMember ? View.VISIBLE : View.GONE);
        holder.rcv_details.setVisibility(isByMember ? View.GONE : View.VISIBLE);

        if (!isByMember){

            List<String> groups = Collections.singletonList(laNmes.get(position));
            HashMap<String, List<OfficerHistory>> map = new HashMap<>();
            map.put(laNmes.get(position), loMapHistory.get(laNmes.get(position)));

            // Create expandable adapter (must extend BaseExpandableListAdapter)
            OfficerHistoryExpandableAdapter expandableAdapter =
                    new OfficerHistoryExpandableAdapter(groups, map);

            holder.rcv_details.setAdapter(expandableAdapter);

            holder.rcv_details.setOnGroupExpandListener(groupPosition -> {
                setListViewHeightBasedOnChildren(holder.rcv_details, expandableAdapter);
            });

            holder.rcv_details.setOnGroupCollapseListener(groupPosition -> {
                setListViewHeightBasedOnChildren(holder.rcv_details, expandableAdapter);
            });
        }else{
            holder.mtv_name.setText(laOfficerHistory.get(position).getSPositnDs());
            holder.mtv_dtransact.setText("Ended on " + poDate.FormatDate(laOfficerHistory.get(position).getDTransact(), "MMMM d, yyyy"));
        }
    }


    @Override
    public int getItemCount() {
        return isByMember ? laOfficerHistory.size() : laNmes.size();
    }

    public static void setListViewHeightBasedOnChildren(ExpandableListView listView, OfficerHistoryExpandableAdapter adapter) {
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY);

        for (int i = 0; i < adapter.getGroupCount(); i++) {
            View groupItem = adapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupItem.getMeasuredHeight();

            if (listView.isGroupExpanded(i)) {
                for (int j = 0; j < adapter.getChildrenCount(i); j++) {
                    View listItem = adapter.getChildView(i, j, false, null, listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight();
                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static class VH_Officer_History extends RecyclerView.ViewHolder{

        private final MaterialTextView mtv_name;
        private final MaterialTextView mtv_dtransact;
        private final ExpandableListView rcv_details;

        public VH_Officer_History(@NonNull View itemView) {
            super(itemView);

            mtv_name = itemView.findViewById(com.gag.masonry.R.id.mtv_name);
            mtv_dtransact = itemView.findViewById(com.gag.masonry.R.id.mtv_dtransact);
            rcv_details = itemView.findViewById(com.gag.masonry.R.id.rcv_details);
        }
    }
}
