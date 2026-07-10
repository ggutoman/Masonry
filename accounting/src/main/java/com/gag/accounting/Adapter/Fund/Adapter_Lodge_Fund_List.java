package com.gag.accounting.Adapter.Fund;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.accounting.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.LodgeFundMasterList;
import org.gag.appdriver.Constants.RECEIPT_CONSTANTS;
import org.gag.appdriver.Room.Entities.EFundTurnOver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Adapter_Lodge_Fund_List extends ArrayAdapter<LodgeFundMasterList> {

    private final Context loInstance;
    private final List<LodgeFundMasterList> laFundMaster;

    private List<LodgeFundMasterList> laFundMasterFiltered;


    public Adapter_Lodge_Fund_List(@NonNull Context context, @NonNull List<LodgeFundMasterList> objects) {
        super(context, 0, objects);

        loInstance = context;
        laFundMaster = objects;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {

            LayoutInflater inflater = LayoutInflater.from(loInstance);
            view = inflater.inflate(R.layout.list_item_lodge_funds, parent, false);
        }

        MaterialTextView mtv_lodge_nm= view.findViewById(R.id.mtv_lodge_nm);
        MaterialTextView mtv_fund_bal= view.findViewById(R.id.mtv_fund_bal);
        MaterialTextView mtv_id= view.findViewById(R.id.mtv_id);

        mtv_lodge_nm.setText(laFundMaster.get(position).getSLodgeNme());
        mtv_fund_bal.setText(laFundMaster.get(position).getNBalancex());
        mtv_id.setText(laFundMaster.get(position).getSLodgeIDx());

        return view;

    }

    @Override
    public int getCount() {
        return laFundMaster.size();
    }

    @Nullable
    @Override
    public LodgeFundMasterList getItem(int position) {
        return laFundMaster.get(position);
    }
}
