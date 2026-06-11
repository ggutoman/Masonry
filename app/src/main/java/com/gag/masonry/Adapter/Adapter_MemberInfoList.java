package com.gag.masonry.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.gag.masonry.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Constants.MENU_ITEM_CONSTANTS;
import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Adapter_MemberInfoList extends BaseExpandableListAdapter {

    private final Context loInstance;
    private final ArrayList<String> paParentList;
    private final HashMap<String, ArrayList<String>> paChildList;

    public Adapter_MemberInfoList(Context foinstance, ArrayList<String> faParentList, HashMap<String, ArrayList<String>> faChildList){
        this.loInstance = foinstance;
        this.paParentList = faParentList;
        this.paChildList = faChildList;
    }

    @Override
    public String getGroup(int i) {
        return paParentList.get(i);
    }

    @Override
    public int getGroupCount() {
        return paParentList.size();
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(paChildList.get(paParentList.get(groupPosition))).get(childPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
       return Objects.requireNonNull(paChildList.get(paParentList.get(groupPosition))).size();
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        if (view == null){

            view = LayoutInflater.from(loInstance).inflate(R.layout.list_menu_parent, viewGroup, false);
        }

        ShapeableImageView img_icon = view.findViewById(R.id.img_icon);
        MaterialTextView mtv_title = view.findViewById(R.id.mtv_title);

        img_icon.setVisibility(View.INVISIBLE);
        mtv_title.setText(paParentList.get(i));

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        if (view == null){

            view = LayoutInflater.from(loInstance).inflate(R.layout.list_menu_item, viewGroup, false);
        }
        MaterialTextView mtv_item = view.findViewById(R.id.mtv_item);
        mtv_item.setText(getChild(i, i1));

        mtv_item.setPadding(25, 0, 0, 0);

        return view;
    }
}