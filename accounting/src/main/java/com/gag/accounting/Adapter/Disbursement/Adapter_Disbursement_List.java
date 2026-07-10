package com.gag.accounting.Adapter.Disbursement;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.accounting.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.ReceiptMasterList;
import org.gag.appdriver.Room.Entities.EDisbursement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Adapter_Disbursement_List extends RecyclerView.Adapter<Adapter_Disbursement_List.VH_Issued_Receipts> {

    private final List<EDisbursement> laDisbursement;
    private final OnSelectItem loCallback;
    private final Adapter_Disbursement_Filter loFilter;

    private List<EDisbursement> laDisbursementFiltered;

    public Adapter_Disbursement_Filter GetFilter(){
        return loFilter;
    }

    public interface OnSelectItem {
        void OnSelec(EDisbursement loItem);
    }

    public Adapter_Disbursement_List(List<EDisbursement> faReceipts, OnSelectItem foCallback){
        laDisbursement = faReceipts;
        laDisbursementFiltered = laDisbursement;
        loCallback = foCallback;
        loFilter = new Adapter_Disbursement_Filter(this);
    }

    @NonNull
    @Override
    public VH_Issued_Receipts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH_Issued_Receipts(
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.list_disbursements,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Issued_Receipts holder, int position) {

        holder.mtv_payee.setText(laDisbursementFiltered.get(position).getSPayeeNme());
        holder.mtv_src_amount.setText(laDisbursementFiltered.get(position).getNAmountxx());
        holder.mtv_voucher.setText(laDisbursementFiltered.get(position).getSVouchrNo());

        switch (laDisbursementFiltered.get(position).getCTranStat()){

            case "0":
                holder.mtv_vstatus.setText("Open");
                break;
            case "1":
                holder.mtv_vstatus.setText("Confirmed");
                break;
            case "2":
                holder.mtv_vstatus.setText("Released");
                break;
            case "3":
                holder.mtv_vstatus.setText("Cancelled");
                break;
            case "4":
                holder.mtv_vstatus.setText("Void");
                break;
            case "5":
                holder.mtv_vstatus.setText("Completed");
                break;
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loCallback.OnSelec(laDisbursementFiltered.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return laDisbursementFiltered.size();
    }

    public class Adapter_Disbursement_Filter extends Filter {

        private final Adapter_Disbursement_List loAdapter;
        private List<String> laStatus = new ArrayList<>(List.of("0", "1", "3", "4"));
        public void InitStatus(List<String> faStatus){
            laStatus = faStatus;
        }

        public Adapter_Disbursement_Filter(Adapter_Disbursement_List foAdapter){
            loAdapter = foAdapter;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            if (charSequence.length() < 1){
                laDisbursementFiltered = laDisbursement;
            }else {

                List<EDisbursement> filterSearch = new ArrayList<>();

                //first filter, via search text
                for (EDisbursement loDisbursement : laDisbursement){

                    if (loDisbursement.getSPayeeNme().toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                            loDisbursement.getSVouchrNo().toLowerCase().contains(charSequence.toString().toLowerCase())){

                        filterSearch.add(loDisbursement);
                    }
                }
                laDisbursementFiltered = filterSearch;
            }

            laDisbursementFiltered = laDisbursementFiltered.stream()
                    .filter(loItem -> laStatus.contains(loItem.getCTranStat()))
                    .collect(Collectors.toList());

            FilterResults loResults = new FilterResults();
            loResults.values = laDisbursementFiltered;
            loResults.count = laDisbursementFiltered.size();

            return loResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            laDisbursementFiltered = (List<EDisbursement>) filterResults.values;
            loAdapter.notifyDataSetChanged();
        }
    }

    public static class VH_Issued_Receipts extends RecyclerView.ViewHolder {

        private View view;
        private MaterialTextView mtv_payee, mtv_voucher, mtv_src_amount, mtv_vstatus;

        public VH_Issued_Receipts(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            mtv_payee = itemView.findViewById(R.id.mtv_payee);
            mtv_voucher = itemView.findViewById(R.id.mtv_voucher);
            mtv_src_amount = itemView.findViewById(R.id.mtv_src_amount);
            mtv_vstatus = itemView.findViewById(R.id.mtv_vstatus);
        }

    }
}
