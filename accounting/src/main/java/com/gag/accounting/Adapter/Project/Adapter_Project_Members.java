package com.gag.accounting.Adapter.Project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.accounting.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.MemberPledges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Adapter_Project_Members extends RecyclerView.Adapter<Adapter_Project_Members.VH_Project_Members> {

    private final Context loContext;
    private final List<MemberPledges> laList;

    private final List<Project_Title> laProjects = new ArrayList<>();
    private final HashMap<String, List<MemberPledges>> loMapPledge = new HashMap<>();

    public Adapter_Project_Members(Context context, List<MemberPledges> objects) {

        loContext = context;
        laList = objects;

        //initialize group list per project
        for (MemberPledges pledges : laList){

            String projName = pledges.getSProjctNm();
            String tranStat = pledges.getCTranStat();

            boolean exists = false;
            for (Project_Title p : laProjects) {
                if (p.sProjctNm.equalsIgnoreCase(projName)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                Project_Title loProject = new Project_Title(projName, tranStat);
                laProjects.add(loProject);
                Toast.makeText(loContext, projName + " is added", Toast.LENGTH_SHORT).show();
            }

            //if item is not mapped to a group, map it to a new group, else, add to existing group
            if (!loMapPledge.containsKey(pledges.getSProjctNm())){
                loMapPledge.put(pledges.getSProjctNm(), new ArrayList<>(List.of(pledges)));
            }else {
                loMapPledge.get(pledges.getSProjctNm()).add(pledges);
            }
        }
    }

    @NonNull
    @Override
    public VH_Project_Members onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH_Project_Members(
                LayoutInflater.from(loContext).inflate(R.layout.list_project_members, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Project_Members holder, int position) {

        holder.mtv_name.setText(laProjects.get(position).sProjctNm);
        switch (laProjects.get(position).cTranStat){

            case "0":
                holder.mtv_status.setText("Planning");
                break;
            case "1":
                holder.mtv_status.setText("On Going");
                break;
            case "2":
                holder.mtv_status.setText("On Hold");
                break;
            case "3":
                holder.mtv_status.setText("Completed");
                break;
            case "4":
                holder.mtv_status.setText("Cancelled");
                break;
        }

        holder.mtv_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.mtv_view.getText().toString().equalsIgnoreCase("View Pledge")){
                    holder.rcv_pledge.setVisibility(View.VISIBLE);
                    holder.mtv_view.setText("Hide Pledge");
                }else{
                    holder.rcv_pledge.setVisibility(View.GONE);
                    holder.mtv_view.setText("View Pledge");
                }
            }
        });

        holder.rcv_pledge.setAdapter(new Adapter_Pledge_Member_Details(loContext, loMapPledge.get(laProjects.get(position).sProjctNm)));
        holder.rcv_pledge.setLayoutManager(new LinearLayoutManager(loContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public int getItemCount() {
        return laProjects.size();
    }

    public static class Project_Title{
        private final String sProjctNm;
        private final String cTranStat;

        public Project_Title(String sProjctNm, String cTranStat) {
            this.sProjctNm = sProjctNm;
            this.cTranStat = cTranStat;
        }
    }


    public static class VH_Project_Members extends RecyclerView.ViewHolder {

        private MaterialTextView mtv_name, mtv_view, mtv_status;
        private RecyclerView rcv_pledge;

        public VH_Project_Members(@NonNull View itemView) {
            super(itemView);

            mtv_name = itemView.findViewById(R.id.mtv_name);
            mtv_view = itemView.findViewById(R.id.mtv_view);
            mtv_status = itemView.findViewById(R.id.mtv_status);
            rcv_pledge = itemView.findViewById(R.id.rcv_pledge);
        }
    }
}