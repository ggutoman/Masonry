package com.gag.masonry.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gag.masonry.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Constants.MENU_ICONS;
import org.gag.appdriver.Constants.MENU_ITEM_CONSTANTS;
import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS;
import org.gag.appdriver.Room.Entities.EUserObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Adapter_Drawer extends BaseExpandableListAdapter {

    private final Context loInstance;
    private final ArrayList<EUserObjects> paParentList;
    private final HashMap<String, ArrayList<EUserObjects>> paChildList;

    public Adapter_Drawer(Context foinstance, ArrayList<EUserObjects> faParentList, HashMap<String, ArrayList<EUserObjects>> faChildList){
        this.loInstance = foinstance;
        this.paParentList = faParentList;
        this.paChildList = faChildList;
    }

    @Override
    public EUserObjects getGroup(int i) {
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
    public EUserObjects getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(paChildList.get(paParentList.get(groupPosition).getIdxx())).get(childPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
       return Objects.requireNonNull(paChildList.get(paParentList.get(groupPosition).getIdxx())).size();
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

        MaterialTextView mtv_title = view.findViewById(R.id.mtv_title);
        ShapeableImageView img_icon = view.findViewById(R.id.img_icon);

        mtv_title.setText(paParentList.get(i).getSDescriptxx());

        img_icon.setImageResource(MENU_ICONS.Companion.fromId(paParentList.get(i).getIdxx()) == null ? org.gag.appdriver.R.drawable.baseline_error_24 : MENU_ICONS.Companion.fromId(paParentList.get(i).getIdxx()).getFnIconxx());

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        if (view == null){

            view = LayoutInflater.from(loInstance).inflate(R.layout.layout_child_menus, viewGroup, false);
        }
        ExpandableListView exp_child = view.findViewById(R.id.exp_child_menus);

        return view;
    }
}