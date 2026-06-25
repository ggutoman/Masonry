package com.gag.accounting.Adapter.Annual;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.accounting.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.App.Models.AnnualSummary;

import java.util.List;

public class Adapter_Annual_Summary extends RecyclerView.Adapter<Adapter_Annual_Summary.VH_Annual_Summary> {

    private final List<AnnualSummary> laList;

    public Adapter_Annual_Summary(List<AnnualSummary> faList){
        laList = faList;
    }

    @NonNull
    @Override
    public VH_Annual_Summary onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new VH_Annual_Summary(
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.adapter_list_annuals,
                                parent,
                                false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Annual_Summary holder, int position) {

        holder.mtv_transaction.setText(laList.get(position).getSTransNox());
        holder.mtv_totaltrans.setText(String.valueOf(laList.get(position).getNTotalTrans()));
        holder.mtv_totalcoll.setText(String.valueOf(laList.get(position).getNTotalColl()));
        holder.mtv_year.setText(laList.get(position).getNYearxx());
        holder.mtv_due.setText(laList.get(position).getDDueDate());

        switch (laList.get(position).getCTranStat()){
            case "1":
                holder.mtv_status.setText("Active");
                break;
            case "2":
                holder.mtv_status.setText("Approved");
                break;
            case "3":
                holder.mtv_status.setText("Disapproved");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return laList.size();
    }

    public static class VH_Annual_Summary extends RecyclerView.ViewHolder{

        private MaterialTextView mtv_transaction, mtv_status, mtv_totaltrans, mtv_totalcoll, mtv_year, mtv_due;

        public VH_Annual_Summary(@NonNull View itemView) {
            super(itemView);

            mtv_transaction = itemView.findViewById(R.id.mtv_transaction);
            mtv_status = itemView.findViewById(R.id.mtv_status);
            mtv_totaltrans = itemView.findViewById(R.id.mtv_totaltrans);
            mtv_totalcoll = itemView.findViewById(R.id.mtv_totalcoll);
            mtv_year = itemView.findViewById(R.id.mtv_year);
            mtv_due = itemView.findViewById(R.id.mtv_due);

        }
    }
}
