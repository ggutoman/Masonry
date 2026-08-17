package com.gag.accounting.Adapter.Project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.accounting.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.ProjectDetail;
import org.gag.appdriver.Libraries.DateUtil.DateRepository;
import org.gag.appdriver.Room.Entities.EProjectMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Adapter_Pledge_Summary extends RecyclerView.Adapter<Adapter_Pledge_Summary.VH_Pledge_List> {

    private final Context loInstance;
    private final List<ProjectDetail> laProjects;

    private final List<String> laMembers;
    private final HashMap<String, List<ProjectDetail>> loMapDetails;

    public Adapter_Pledge_Summary(Context context, List<ProjectDetail> faProjects){
        loInstance = context;
        laProjects = faProjects;

        //initialize group list for every members
        laMembers = new ArrayList<>();
        loMapDetails = new HashMap<>();

        for (ProjectDetail loItem : laProjects){

            if (!laMembers.contains(loItem.getSMemberNme())){
                laMembers.add(loItem.getSMemberNme());
            }

            //add detail to map's list for the member
            if (!loMapDetails.containsKey(loItem.getSMemberNme())){

                //if not mapped, add new list
                loMapDetails.put(loItem.getSMemberNme(), new ArrayList<>(List.of(loItem)));
            }else {
                loMapDetails.get(loItem.getSMemberNme()).add(loItem);
            }

        }
    }

    @NonNull
    @Override
    public VH_Pledge_List onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new VH_Pledge_List(
                LayoutInflater.from(loInstance)
                        .inflate(R.layout.list_pledge_members,
                                parent,
                                false
                        )
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VH_Pledge_List holder, int position) {

        holder.mtv_name.setText(laMembers.get(position));

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

        holder.rcv_pledge.setAdapter(new Adapter_Pledge_Summary_Details(loInstance, loMapDetails.get(laMembers.get(position))));
        holder.rcv_pledge.setLayoutManager(new LinearLayoutManager(loInstance, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public int getItemCount() {
        return laMembers.size();
    }

    public static class VH_Pledge_List extends RecyclerView.ViewHolder {

        private MaterialTextView mtv_name, mtv_view;
        private RecyclerView rcv_pledge;

        public VH_Pledge_List(@NonNull View itemView) {
            super(itemView);

            mtv_name = itemView.findViewById(R.id.mtv_name);
            mtv_view = itemView.findViewById(R.id.mtv_view);
            rcv_pledge = itemView.findViewById(R.id.rcv_pledge);
        }
    }
}
