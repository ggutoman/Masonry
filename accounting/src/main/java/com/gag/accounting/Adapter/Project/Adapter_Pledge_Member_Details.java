package com.gag.accounting.Adapter.Project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.accounting.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.MemberPledges;
import org.gag.appdriver.App.Models.ProjectDetail;
import org.gag.appdriver.Libraries.DateUtil.DateRepository;

import java.util.List;

public class Adapter_Pledge_Member_Details extends RecyclerView.Adapter<Adapter_Pledge_Member_Details.VH_Pledge_List_Details> {

    private final Context loInstance;
    private final List<MemberPledges> laProjects;

    private final DateRepository loDate;

    public Adapter_Pledge_Member_Details(Context context, List<MemberPledges> faProjects){
        loInstance = context;
        laProjects = faProjects;
        loDate = new DateRepository();
    }

    @NonNull
    @Override
    public VH_Pledge_List_Details onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new VH_Pledge_List_Details(
                LayoutInflater.from(loInstance)
                        .inflate(R.layout.list_pledge_member_details,
                                parent,
                                false
                        )
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VH_Pledge_List_Details holder, int position) {

        holder.mtv_date_pledge.setText(loDate.FormatDate(laProjects.get(position).getDPledgexx(), "MMMM d, yyyy"));
        holder.mtv_amt_pledge.setText(laProjects.get(position).getNPledgexx());
        holder.mtv_or.setText(laProjects.get(position).getSORNoxxxx());
        holder.mtv_paid.setText(laProjects.get(position).getNAmtPaidx());
    }

    @Override
    public int getItemCount() {
        return laProjects.size();
    }

    public static class VH_Pledge_List_Details extends RecyclerView.ViewHolder {

        private MaterialTextView mtv_date_pledge, mtv_amt_pledge, mtv_or, mtv_paid;

        public VH_Pledge_List_Details(@NonNull View itemView) {
            super(itemView);

            mtv_date_pledge = itemView.findViewById(R.id.mtv_date_pledge);
            mtv_amt_pledge = itemView.findViewById(R.id.mtv_amt_pledge);
            mtv_or = itemView.findViewById(R.id.mtv_or);
            mtv_paid = itemView.findViewById(R.id.mtv_paid);
        }
    }
}
