package com.gag.accounting.Adapter.Annual;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.accounting.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.AnnualMembers;

import java.util.List;

public class Adapter_Annual_Members extends RecyclerView.Adapter<Adapter_Annual_Members.VH_Annual_Summary> {

    private final List<AnnualMembers> laList;

    public Adapter_Annual_Members(List<AnnualMembers> faList){
        laList = faList;
    }

    @NonNull
    @Override
    public VH_Annual_Summary onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new VH_Annual_Summary(
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.adapter_list_annual_members,
                                parent,
                                false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Annual_Summary holder, int position) {

        holder.mtv_name.setText(laList.get(position).getSMemberNme());
        holder.mtv_amtdue.setText(laList.get(position).getNAmtDuexx());
        holder.mtv_amtpaid.setText(laList.get(position).getNAmtPaidx());
    }

    @Override
    public int getItemCount() {
        return laList.size();
    }

    public static class VH_Annual_Summary extends RecyclerView.ViewHolder{

        private MaterialTextView mtv_name;
        private MaterialTextView mtv_amtdue;
        private MaterialTextView mtv_amtpaid;

        public VH_Annual_Summary(@NonNull View itemView) {
            super(itemView);

            mtv_name = itemView.findViewById(R.id.mtv_name);
            mtv_amtdue = itemView.findViewById(R.id.mtv_amtdue);
            mtv_amtpaid = itemView.findViewById(R.id.mtv_amtpaid);

        }
    }
}
