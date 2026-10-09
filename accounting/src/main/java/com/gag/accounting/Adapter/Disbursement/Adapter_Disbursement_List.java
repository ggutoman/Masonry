package com.gag.accounting.Adapter.Disbursement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.gag.accounting.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.ReceiptMasterList;
import org.gag.appdriver.Room.Entities.EDisbursement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Adapter_Disbursement_List extends BaseExpandableListAdapter {

    private final OnSelectItem loCallback;

    private final Context loInstance;
    private List<String> laStatus = List.of("0", "1", "2", "3", "4");

    private final List<String> paParentList;
    private final HashMap<String, List<EDisbursement>> paDisb;

    private final List<String> paParentListFiltered;
    private final HashMap<String, List<EDisbursement>> paDisbFiltered;

    public interface OnSelectItem {
        void OnSelec(EDisbursement loItem);
    }

    public Adapter_Disbursement_List(Context foInstance, List<String> faParentList, HashMap<String, List<EDisbursement>> faDisb, OnSelectItem foCallback){

        loInstance = foInstance;

        paParentList = faParentList;
        paDisb = faDisb;

        paParentListFiltered = new ArrayList<>(paParentList);
        paDisbFiltered = new HashMap<>(paDisb);

        loCallback = foCallback;
    }

    public void InitStatus(List<String> faStatus){
        laStatus = faStatus;
    }

    public void Filter(String fsFilter) {

        //clear filtered list
        paParentListFiltered.clear();
        paDisbFiltered.clear();

        for (String parent : paParentList) {

            List<EDisbursement> children = paDisb.get(parent);
            List<EDisbursement> filteredChildren = new ArrayList<>();

            if (children.size() > 0){

                for (EDisbursement item : children) {

                    if (laStatus.contains(item.getCTranStat())) {
                        if (fsFilter.isEmpty() || (item.getSVouchrNo().toLowerCase().contains(fsFilter) || parent.toLowerCase().contains(fsFilter))) filteredChildren.add(item);
                    }
                }

                if (filteredChildren.size() > 0) {
                    paParentListFiltered.add(parent);
                    paDisbFiltered.put(parent, filteredChildren);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public String getGroup(int i) {
        return paParentListFiltered.get(i);
    }

    @Override
    public int getGroupCount() {
        return paParentListFiltered.size();
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
        mtv_group.setText(paParentListFiltered.get(i));

        return view;

    }

    @Override
    public EDisbursement getChild(int i, int i1) {
        return Objects.requireNonNull(paDisbFiltered.get(paParentListFiltered.get(i))).get(i1);
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public int getChildrenCount(int i) {
        return Objects.requireNonNull(paDisbFiltered.get(paParentListFiltered.get(i))).size();
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

            view = LayoutInflater.from(loInstance).inflate(R.layout.list_disbursements, viewGroup, false);
        }

        MaterialTextView mtv_voucher = view.findViewById(R.id.mtv_voucher);
        MaterialTextView mtv_src_amount = view.findViewById(R.id.mtv_src_amount);
        MaterialTextView mtv_vstatus = view.findViewById(R.id.mtv_vstatus);

        EDisbursement loItem = paDisbFiltered.get(paParentListFiltered.get(i)).get(i1);

        mtv_voucher.setText(loItem.getSVouchrNo());
        mtv_src_amount.setText(loItem.getNAmountxx());

        switch (loItem.getCTranStat()){

            case "0":
                mtv_vstatus.setText("Open");
                break;
            case "1":
                mtv_vstatus.setText("Confirmed");
                break;
            case "2":
                mtv_vstatus.setText("Released");
                break;
            case "3":
                mtv_vstatus.setText("Cancelled");
                break;
            case "4":
                mtv_vstatus.setText("Void");
                break;
            case "5":
                mtv_vstatus.setText("Completed");
                break;
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loCallback.OnSelec(loItem);
            }
        });
        return view;
    }

}
