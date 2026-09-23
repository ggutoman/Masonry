package com.gag.masonry.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.gag.masonry.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.OfficerHistory;
import org.gag.appdriver.Libraries.DateUtil.DateRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class OfficerHistoryExpandableAdapter extends BaseExpandableListAdapter {

    private final DateRepository poDate;
    private final List<String> memberNames;
    private final HashMap<String, List<OfficerHistory>> roleMap;

    public OfficerHistoryExpandableAdapter(List<String> memberName,
                                           HashMap<String, List<OfficerHistory>> roleMap) {

        this.poDate = new DateRepository();
        this.memberNames = memberName;
        this.roleMap = roleMap;
    }

    @Override
    public int getGroupCount() { return memberNames.size(); }

    @Override
    public Object getGroup(int groupPosition) { return memberNames.get(groupPosition); }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_menu_parent, parent, false);
        }

        ConstraintLayout parent_layout = convertView.findViewById(R.id.layout_group);
        MaterialTextView tvYear = convertView.findViewById(R.id.mtv_title);
        ShapeableImageView img_icon = convertView.findViewById(R.id.img_icon);

        parent_layout.setPadding(-15, 15, -15, 15);

        img_icon.setVisibility(View.GONE);
        tvYear.setText(memberNames.get(groupPosition));
        tvYear.setAlpha(0.8f);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(roleMap.get(memberNames.get(groupPosition))).size();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(roleMap.get(memberNames.get(groupPosition))).get(childPosition);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_officer_history, parent, false);
        }

        MaterialTextView mtv_position = convertView.findViewById(R.id.mtv_position);
        MaterialTextView mtv_dtransact = convertView.findViewById(R.id.mtv_dtransact);

        OfficerHistory history = Objects.requireNonNull(roleMap.get(memberNames.get(groupPosition))).get(childPosition);

        mtv_position.setText(history.getSPositnDs());
        mtv_dtransact.setText("Ended on " + poDate.FormatDate(history.getDTransact(), "MMMM d, yyyy"));

        return convertView;
    }

    @Override public boolean hasStableIds() { return false; }
    @Override public long getGroupId(int groupPosition) { return groupPosition; }
    @Override public long getChildId(int groupPosition, int childPosition) { return childPosition; }
    @Override public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
}

