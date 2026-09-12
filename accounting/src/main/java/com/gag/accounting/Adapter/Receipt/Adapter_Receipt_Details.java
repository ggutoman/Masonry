package com.gag.accounting.Adapter.Receipt;

import android.annotation.SuppressLint;
import android.content.Context;
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

import org.gag.appdriver.App.Core.Projects;
import org.gag.appdriver.App.Models.ReceiptDetail;
import org.gag.appdriver.Libraries.DateUtil.DateRepository;
import org.gag.appdriver.Room.Entities.EProjectMaster;
import org.gag.appdriver.Room.Entities.EReceiptDetail;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Receipt_Details extends RecyclerView.Adapter<Adapter_Receipt_Details.VH_Receipt_Details> {

    private final Context loInstance;
    private final Projects poProject;
    private final List<EReceiptDetail> laDetails;

    public Adapter_Receipt_Details(Context foInstance, List<EReceiptDetail> foDetails){

        loInstance = foInstance;
        poProject = new Projects(loInstance);
        laDetails = foDetails;
    }

    @NonNull
    @Override
    public VH_Receipt_Details onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH_Receipt_Details(
                LayoutInflater.from(loInstance).inflate(R.layout.list_issued_receipts, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Receipt_Details holder, int position) {

        //holder.mtv_src_no.setVisibility(laDetails.get(position).getSSourceCD().equalsIgnoreCase("PROJ") ? View.VISIBLE : View.GONE);
        switch (laDetails.get(position).getSSourceCD()){
            case "DUES":
                holder.mtv_src_no.setText("Membership Fee");
                holder.mtv_src_code.setText("Annual Dues");
                break;
            case "PROJ":
                EProjectMaster loProject = poProject.GetProjectInfo(laDetails.get(position).getSSourceNo());
                String lsProjDescr = loProject == null || loProject.getSProjctNm().isEmpty() ? laDetails.get(position).getSSourceNo() : loProject.getSProjctNm();
                holder.mtv_src_no.setText(lsProjDescr);

                holder.mtv_src_code.setText("Project");
                break;
            default:
                holder.mtv_src_no.setText(laDetails.get(position).getSSourceNo());
                holder.mtv_src_code.setText(laDetails.get(position).getSSourceCD() .equalsIgnoreCase("BINT") ? "Bank Interest" : "Others");
                break;
        }
        holder.mtv_src_amount.setText(laDetails.get(position).getNAmtPaidx());
    }

    @Override
    public int getItemCount() {
        return laDetails.size();
    }

    public static class VH_Receipt_Details extends RecyclerView.ViewHolder {

        private MaterialTextView  mtv_src_no, mtv_src_code, mtv_src_amount;

        public VH_Receipt_Details(@NonNull View itemView) {
            super(itemView);

            mtv_src_no = itemView.findViewById(R.id.mtv_src_no);
            mtv_src_code = itemView.findViewById(R.id.mtv_src_code);
            mtv_src_amount = itemView.findViewById(R.id.mtv_src_amount);
        }

    }

}
