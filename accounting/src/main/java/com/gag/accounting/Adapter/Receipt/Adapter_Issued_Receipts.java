package com.gag.accounting.Adapter.Receipt;

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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Adapter_Issued_Receipts extends RecyclerView.Adapter<Adapter_Issued_Receipts.VH_Issued_Receipts> {

    private final List<ReceiptMasterList> laReceipts;
    private List<ReceiptMasterList> laReceiptsFiltered;
    private final OnSelectItem loCallback;
    private final Adapter_Receipt_Filter loFilter;

    public Adapter_Receipt_Filter GetFilter(){
        return loFilter;
    }

    public interface OnSelectItem {
        void OnSelec(ReceiptMasterList loItem);
    }

    public Adapter_Issued_Receipts(List<ReceiptMasterList> faReceipts, OnSelectItem foCallback){
        laReceipts = faReceipts;
        laReceiptsFiltered = laReceipts;
        loCallback = foCallback;
        loFilter = new Adapter_Receipt_Filter(this);
    }

    @NonNull
    @Override
    public VH_Issued_Receipts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH_Issued_Receipts(
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.list_receipt_master_list,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Issued_Receipts holder, int position) {

        holder.mtv_src_nme.setText(laReceiptsFiltered.get(position).getSMemberNme());
        holder.mtv_src_amount.setText(laReceiptsFiltered.get(position).getNTotlPaid());

        switch (laReceiptsFiltered.get(position).getCTranStat()){

            case "0":
                holder.mtv_status.setText("Open");
                break;
            case "1":
                holder.mtv_status.setText("Confirmed");
                break;
            case "3":
                holder.mtv_status.setText("Cancelled");
                break;
            case "4":
                holder.mtv_status.setText("Void");
                break;
        }
        holder.mtv_date.setText(laReceiptsFiltered.get(position).getDTransact());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loCallback.OnSelec(laReceiptsFiltered.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return laReceiptsFiltered.size();
    }

    public class Adapter_Receipt_Filter extends Filter {

        private final Adapter_Issued_Receipts loAdapter;
        private List<String> laStatus = new ArrayList<>(List.of("0", "1", "3", "4"));
        public void InitStatus(List<String> faStatus){
            laStatus = faStatus;
        }

        public Adapter_Receipt_Filter(Adapter_Issued_Receipts foAdapter){
            loAdapter = foAdapter;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            if (charSequence.length() < 1){
                laReceiptsFiltered = laReceipts;
            }else {

                List<ReceiptMasterList> filterSearch = new ArrayList<>();

                //first filter, via search text
                for (ReceiptMasterList loReceipt : laReceipts){

                    if (loReceipt.getSMemberNme().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filterSearch.add(loReceipt);
                    }
                }
                laReceiptsFiltered = filterSearch;
            }

            laReceiptsFiltered = laReceiptsFiltered.stream()
                    .filter(loItem -> laStatus.contains(loItem.getCTranStat()))
                    .collect(Collectors.toList());

            FilterResults loResults = new FilterResults();
            loResults.values = laReceiptsFiltered;
            loResults.count = laReceiptsFiltered.size();

            return loResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            laReceiptsFiltered = (List<ReceiptMasterList>) filterResults.values;
            loAdapter.notifyDataSetChanged();
        }
    }

    public static class VH_Issued_Receipts extends RecyclerView.ViewHolder {

        private View view;
        private MaterialTextView mtv_src_nme, mtv_src_amount, mtv_status, mtv_date;

        public VH_Issued_Receipts(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            mtv_src_nme = itemView.findViewById(R.id.mtv_src_nme);
            mtv_src_amount = itemView.findViewById(R.id.mtv_src_amount);
            mtv_status = itemView.findViewById(R.id.mtv_status);
            mtv_date = itemView.findViewById(R.id.mtv_date);
        }

    }
}
