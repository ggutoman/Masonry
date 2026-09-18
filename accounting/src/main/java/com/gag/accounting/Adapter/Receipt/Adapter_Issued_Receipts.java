package com.gag.accounting.Adapter.Receipt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.gag.accounting.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.ReceiptMasterList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Adapter_Issued_Receipts extends BaseExpandableListAdapter {

    private final Context loInstance;

    private final List<String> paParentList;
    private final HashMap<String, List<ReceiptMasterList>> paChildList;

    private List<String> paParentFiltered;
    private HashMap<String, List<ReceiptMasterList>> paChildFiltered;

    private List<String> laStatus = List.of("0", "1", "3", "4");

    private final OnSelectItem loCallback;

    public interface OnSelectItem {
        void OnSelect(ReceiptMasterList loItem);
    }

    public Adapter_Issued_Receipts(Context foInstance, List<String> foParentList, HashMap<String, List<ReceiptMasterList>> foItems, OnSelectItem foCallback){
        loInstance = foInstance;
        paParentList = foParentList;
        paChildList = foItems;
        loCallback = foCallback;

        // start filtered lists as full copies
        paParentFiltered = new ArrayList<>(paParentList);
        paChildFiltered = new HashMap<>(paChildList);
    }

    public void InitStatus(List<String> faStatus){
        laStatus = faStatus;
    }

    public void Filter(String fsFilter) {

        //clear filtered list
        paParentFiltered.clear();
        paChildFiltered.clear();

        for (String parent : paParentList) {

            List<ReceiptMasterList> children = paChildList.get(parent);
            List<ReceiptMasterList> filteredChildren = new ArrayList<>();

            if (children.size() > 0){

                for (ReceiptMasterList item : children) {

                    if (laStatus.contains(item.getCTranStat())) {
                        if (fsFilter.isEmpty() || (item.getSORNoxxxx().toLowerCase().contains(fsFilter) || parent.toLowerCase().contains(fsFilter))) filteredChildren.add(item);
                    }
                }

                if (filteredChildren.size() > 0) {
                    paParentFiltered.add(parent);
                    paChildFiltered.put(parent, filteredChildren);
                }
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public String getGroup(int i) {
        return paParentFiltered.get(i);
    }

    @Override
    public int getGroupCount() {
        return paParentFiltered.size();
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        if (view == null){

            view = LayoutInflater.from(loInstance).inflate(R.layout.list_item_parent, viewGroup, false);
        }
        MaterialTextView mtv_group = view.findViewById(R.id.mtv_group);
        mtv_group.setText(paParentFiltered.get(i));

        return view;
    }

    @Override
    public Object getChild(int i, int i1) {
        return Objects.requireNonNull(paChildFiltered.get(paParentFiltered.get(i))).get(i1);
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public int getChildrenCount(int i) {
        return Objects.requireNonNull(paChildFiltered.get(paParentFiltered.get(i))).size();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        if (view == null){

            view = LayoutInflater.from(loInstance).inflate(R.layout.list_receipt_master_list, viewGroup, false);
        }

        MaterialTextView mtv_or = view.findViewById(R.id.mtv_or);
        MaterialTextView mtv_src_amount = view.findViewById(R.id.mtv_src_amount);
        MaterialTextView mtv_status = view.findViewById(R.id.mtv_status);

        ReceiptMasterList item = paChildFiltered.get(paParentFiltered.get(i)).get(i1);

        mtv_or.setText(item.getSORNoxxxx());
        mtv_src_amount.setText(item.getNTotlPaid());

        switch (item.getCTranStat()){

            case "0":
                mtv_status.setText("Open");
                break;
            case "1":
                mtv_status.setText("Confirmed");
                break;
            case "2":
                mtv_status.setText("Cancelled");
                break;
        }

        //capture selection
        view.setOnClickListener(view1 -> loCallback.OnSelect(item));

        return view;
    }

}
