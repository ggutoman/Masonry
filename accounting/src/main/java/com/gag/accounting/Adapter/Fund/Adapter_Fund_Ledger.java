package com.gag.accounting.Adapter.Fund;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gag.accounting.R;
import com.google.android.material.textview.MaterialTextView;
import org.gag.appdriver.Room.Entities.EFundLedger;
import java.util.List;

public class Adapter_Fund_Ledger extends RecyclerView.Adapter<Adapter_Fund_Ledger.VH_Fund_Ledger> {

    private final List<EFundLedger> laFundLedger;

    public Adapter_Fund_Ledger(List<EFundLedger> faLedgers){
        laFundLedger = faLedgers;
    }

    @NonNull
    @Override
    public VH_Fund_Ledger onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH_Fund_Ledger(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_ledger, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Fund_Ledger holder, int position) {

        holder.mtv_date.setText(laFundLedger.get(position).getDTransact());

        switch (laFundLedger.get(position).getSSourceCD()){

            case "OVER":
                holder.mtv_type.setText("Fund Turnover / Balance Forward");
                holder.mtv_amount_label.setText("Amount IN");
                holder.mtv_amount.setText(laFundLedger.get(position).getNAmountIn());
                break;
            case "RCPT":
                holder.mtv_type.setText("Receipt");
                holder.mtv_amount_label.setText("Amount IN");
                holder.mtv_amount.setText(laFundLedger.get(position).getNAmountIn());
                break;
            case ";DISB":
                holder.mtv_type.setText("Disbursement");
                holder.mtv_amount_label.setText("Amount Out");
                holder.mtv_amount.setText(laFundLedger.get(position).getNAmountOt());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return laFundLedger.size();
    }


    public static class VH_Fund_Ledger extends RecyclerView.ViewHolder{

        private MaterialTextView mtv_date, mtv_type, mtv_amount_label, mtv_amount;

        public VH_Fund_Ledger(@NonNull View itemView) {
            super(itemView);

            mtv_date = itemView.findViewById(R.id.mtv_date);
            mtv_type = itemView.findViewById(R.id.mtv_type);
            mtv_amount_label = itemView.findViewById(R.id.mtv_amount_label);
            mtv_amount = itemView.findViewById(R.id.mtv_amount);
        }
    }
}
