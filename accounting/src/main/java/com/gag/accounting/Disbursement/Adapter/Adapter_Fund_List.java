package com.gag.accounting.Disbursement.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.accounting.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Room.Entities.EFundTurnOver;
import org.gag.appdriver.Room.Entities.EMemberInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Adapter_Fund_List extends RecyclerView.Adapter<Adapter_Fund_List.VH_Fund_List> {

    private final Context loInstance;
    private final List<EFundTurnOver> laTurnOvers;
    private final Adapter_Fund_List_Filter loFilter = new Adapter_Fund_List_Filter(this);
    private List<EFundTurnOver> laTurnOversFiltered;

    public Adapter_Fund_List(Context context, List<EFundTurnOver> faTurnOvers){
        loInstance = context;
        laTurnOvers = faTurnOvers;
        laTurnOversFiltered = faTurnOvers;
    }

    public Adapter_Fund_List_Filter GetFilter(){
        return loFilter;
    }

    @NonNull
    @Override
    public VH_Fund_List onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new VH_Fund_List(
                LayoutInflater.from(loInstance)
                        .inflate(R.layout.list_item_funds,
                                parent,
                                false
                        )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Fund_List holder, int position) {

        holder.mtv_transaction.setText(laTurnOversFiltered.get(position).getSTransNox());
        holder.mtv_fund_amount.setText(laTurnOversFiltered.get(position).getNAmountxx());
        holder.mtv_date.setText(laTurnOversFiltered.get(position).getDTransact());

        switch (laTurnOversFiltered.get(position).getCTranStat()){

            case "1":
                holder.mtv_status.setText("Pending for Approval");
                holder.mtv_status.setTextColor(Color.GRAY);
                break;
            case "2":
                holder.mtv_status.setText("Approved");
                holder.mtv_status.setTextColor(Color.GREEN);
                break;
            case "3":
                holder.mtv_status.setText("Rejected");
                holder.mtv_status.setTextColor(Color.RED);
                break;
            default:
                holder.mtv_status.setText("N/A");
                holder.mtv_status.setTextColor(Color.GRAY);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return laTurnOversFiltered.size();
    }

    public class Adapter_Fund_List_Filter extends Filter{

        private final Adapter_Fund_List loAdapter;
        private List<String> laStatus = new ArrayList<>(List.of("0", "1", "2"));

        public void InitStatus(List<String> faStatus){
            laStatus = faStatus;
        }

        public Adapter_Fund_List_Filter(Adapter_Fund_List foAdapter){
            loAdapter = foAdapter;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            if (charSequence.length() < 1){
                laTurnOversFiltered = laTurnOvers;
            }else {

                List<EFundTurnOver> filterSearch = new ArrayList<>();

                //first filter, via search text
                for (EFundTurnOver loTurnOver : laTurnOvers){

                    if (loTurnOver.getSTransNox().contains(charSequence.toString().toLowerCase()) ||
                            loTurnOver.getNAmountxx().contains(charSequence.toString().toLowerCase())){

                        filterSearch.add(loTurnOver);
                    }
                }

                laTurnOversFiltered = filterSearch;
            }

            laTurnOversFiltered = laTurnOversFiltered.stream()
                    .filter(loItem -> laStatus.contains(loItem.getCTranStat()))
                    .collect(Collectors.toList());

            FilterResults loResults = new FilterResults();
            loResults.values = laTurnOversFiltered;
            loResults.count = laTurnOversFiltered.size();

            return loResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            laTurnOversFiltered = (List<EFundTurnOver>) filterResults.values;
            loAdapter.notifyDataSetChanged();
        }
    }

    public static class VH_Fund_List extends RecyclerView.ViewHolder {

        private MaterialTextView mtv_transaction, mtv_fund_amount, mtv_date, mtv_status;

        public VH_Fund_List(@NonNull View itemView) {
            super(itemView);

            mtv_transaction = itemView.findViewById(R.id.mtv_transaction);
            mtv_fund_amount = itemView.findViewById(R.id.mtv_fund_amount);
            mtv_date = itemView.findViewById(R.id.mtv_date);
            mtv_status = itemView.findViewById(R.id.mtv_status);
        }
    }
}
