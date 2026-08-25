package org.gag.appdriver.App.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.AnnualSummary;
import org.gag.appdriver.R;

import java.util.List;

public class Adapter_Annual_Member_Info extends RecyclerView.Adapter<Adapter_Annual_Member_Info.VH_Annual_Member_Info> {

    private final List<AnnualSummary> laList;

    public Adapter_Annual_Member_Info(List<AnnualSummary> faList){
        laList = faList;
    }

    @NonNull
    @Override
    public VH_Annual_Member_Info onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new VH_Annual_Member_Info(
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.adapter_list_annual_member,
                                parent,
                                false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Annual_Member_Info holder, int position) {

        holder.mtv_transaction.setText(laList.get(position).getSTransNox());
        holder.mtv_totaltrans.setText(String.valueOf(laList.get(position).getNTotalTrans()));
        holder.mtv_totalcoll.setText(String.valueOf(laList.get(position).getNTotalColl()));
        holder.mtv_due.setText(laList.get(position).getDDueDate());
    }

    @Override
    public int getItemCount() {
        return laList.size();
    }

    public static class VH_Annual_Member_Info extends RecyclerView.ViewHolder{

        private MaterialTextView mtv_transaction, mtv_totaltrans, mtv_totalcoll, mtv_due;

        public VH_Annual_Member_Info(@NonNull View itemView) {
            super(itemView);

            mtv_transaction = itemView.findViewById(R.id.mtv_transaction);
            mtv_totaltrans = itemView.findViewById(R.id.mtv_totaltrans);
            mtv_totalcoll = itemView.findViewById(R.id.mtv_totalcoll);
            mtv_due = itemView.findViewById(R.id.mtv_due);

        }
    }
}
