package com.gag.accounting.Adapter.Receipt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.accounting.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Room.Entities.EReceiptDetail;

import java.util.List;

public class Adapter_Receipt_Transactions extends RecyclerView.Adapter<Adapter_Receipt_Transactions.VH_Receipt>{

    private final Context loInstance;
    private final List<EReceiptDetail> laDetails;

    public Adapter_Receipt_Transactions(Context foInstance, List<EReceiptDetail> faDetails){
        loInstance = foInstance;
        laDetails = faDetails;
    }

    @NonNull
    @Override
    public VH_Receipt onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new VH_Receipt(
                LayoutInflater
                        .from(loInstance)
                        .inflate(R.layout.list_receipt_details, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Receipt holder, int position) {

        holder.mtv_src_no.setText(laDetails.get(position).getSSourceNo());

        switch (laDetails.get(position).getSSourceCD()){

            case "DUES":
                holder.mtv_src_code.setText("Annual Due");
                break;
            case "PROJ":
                holder.mtv_src_code.setText("Project");
                break;
            case "BINT":
                holder.mtv_src_code.setText("Bank Interest");
                break;
            case "OTH":
                holder.mtv_src_code.setText("Others");
                break;
        }

        holder.mtv_src_amount.setText(laDetails.get(position).getNAmtPaidx());
    }

    @Override
    public int getItemCount() {
        return laDetails.size();
    }

    public static class VH_Receipt extends RecyclerView.ViewHolder {

        private final MaterialTextView mtv_src_no;
        private final MaterialTextView mtv_src_amount;
        private final MaterialTextView mtv_src_code;

        public VH_Receipt(@NonNull View itemView) {
            super(itemView);

            mtv_src_no = itemView.findViewById(R.id.mtv_src_no);
            mtv_src_amount = itemView.findViewById(R.id.mtv_src_amount);
            mtv_src_code = itemView.findViewById(R.id.mtv_src_code);

        }
    }
}
