package com.gag.masonry.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.gag.masonry.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.OfficerHistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OfficerHistoryExpandableAdapter extends BaseExpandableListAdapter {
    private final String memberName;
    private final List<String> yearIds;
    private final HashMap<String, List<OfficerHistory>> yearMap;

    public OfficerHistoryExpandableAdapter(String memberName,
                                           HashMap<String, List<OfficerHistory>> yearMap) {
        this.memberName = memberName;
        this.yearMap = yearMap;
        this.yearIds = new ArrayList<>(yearMap.keySet());
    }

    @Override
    public int getGroupCount() { return yearIds.size(); }

    @Override
    public int getChildrenCount(int groupPosition) {
        return yearMap.get(yearIds.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) { return yearIds.get(groupPosition); }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return yearMap.get(yearIds.get(groupPosition)).get(childPosition);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_menu_parent, parent, false);
        }

        ConstraintLayout layout_group = convertView.findViewById(R.id.layout_group);
        MaterialTextView tvYear = convertView.findViewById(R.id.mtv_title);
        ShapeableImageView img_icon = convertView.findViewById(R.id.img_icon);

        layout_group.setPadding(15, 15, 0, 0);

        img_icon.setVisibility(View.GONE);
        tvYear.setText(yearIds.get(groupPosition));
        tvYear.setAlpha(0.8f);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_officer_history, parent, false);
        }

        MaterialTextView mtv_position = convertView.findViewById(R.id.mtv_position);
        MaterialTextView mtv_status = convertView.findViewById(R.id.mtv_status);
        MaterialTextView mtv_dtransact = convertView.findViewById(R.id.mtv_dtransact);

        OfficerHistory history = yearMap.get(yearIds.get(groupPosition)).get(childPosition);

        String lsStatus = "N/A";
        switch (history.getCNewStatx()){
            case "0":
                lsStatus = "Suspended";
                break;
            case "1":
                lsStatus = "Active";
                break;
            case "2":
                lsStatus = "Reassigned";
                break;
            case "3":
                lsStatus = "Removed";
                break;
            case "4":
                lsStatus = "Resigned";
                break;
            case "5":
                lsStatus = "Deceased";
                break;
        }


        mtv_position.setText(history.getSPositnDs());
        mtv_status.setText(lsStatus);
        mtv_dtransact.setText(history.getDTransact());

        return convertView;
    }

    @Override public boolean hasStableIds() { return false; }
    @Override public long getGroupId(int groupPosition) { return groupPosition; }
    @Override public long getChildId(int groupPosition, int childPosition) { return childPosition; }
    @Override public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
}

