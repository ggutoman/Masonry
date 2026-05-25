package com.gag.masonry.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.gag.masonry.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Constants.MENU_ITEM_CONSTANTS;
import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS;

import java.util.HashMap;
import java.util.List;

public class Adapter_Drawer extends BaseExpandableListAdapter {

    private Context loInstance;
    private List<MENU_PARENT_CONSTANTS> paParentList;
    private HashMap<String, List<MENU_ITEM_CONSTANTS>> paChildList;

    public Adapter_Drawer(Context foinstance, List<MENU_PARENT_CONSTANTS> faParentList, HashMap<String, List<MENU_ITEM_CONSTANTS>> faChildList){
        this.loInstance = foinstance;
        this.paParentList = faParentList;
        this.paChildList = faChildList;
    }

    @Override
    public Object getGroup(int i) {
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
    public MENU_ITEM_CONSTANTS getChild(int i, int i1) {
        return paChildList.get(paParentList.get(i)).get(i1);
    }

    @Override
    public int getChildrenCount(int i) {
        return paChildList.get(paParentList.get(i)).size();
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
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

        img_icon.setImageResource(paParentList.get(i).getFnIconx());
        mtv_title.setText(paParentList.get(i).getFsTitlex());

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        if (view == null){

            view = LayoutInflater.from(loInstance).inflate(R.layout.list_menu_item, viewGroup, false);
        }

        MaterialTextView mtv_item = view.findViewById(R.id.mtv_item);
        mtv_item.setText(getChild(i, i1).getFsTitlex());

        return view;
    }
}
