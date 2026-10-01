package org.gag.appdriver.App.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.AnnualSummary;
import org.gag.appdriver.R;
import org.gag.appdriver.Room.Entities.ESysNotifs;

import java.util.List;

public class Adapter_Notification extends RecyclerView.Adapter<Adapter_Notification.VH_Annual_Member_Info> {

    private final List<ESysNotifs> laList;
    private final OnAction loCallback;

    public interface OnAction{
        void OnViewDetails(ESysNotifs notif);
    }

    public Adapter_Notification(List<ESysNotifs> faList, OnAction foCallback){
        laList = faList;
        loCallback = foCallback;
    }

    @NonNull
    @Override
    public VH_Annual_Member_Info onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new VH_Annual_Member_Info(
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.adapter_list_notification,
                                parent,
                                false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Annual_Member_Info holder, int position) {

        holder.mtv_title.setText(String.valueOf(laList.get(position).getSTitlex()));
        holder.mtv_message.setText(String.valueOf(laList.get(position).getSMessagex()));

        holder.mtv_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loCallback.OnViewDetails(laList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return laList.size();
    }

    public static class VH_Annual_Member_Info extends RecyclerView.ViewHolder{

        private MaterialTextView mtv_view, mtv_title, mtv_message;

        public VH_Annual_Member_Info(@NonNull View itemView) {
            super(itemView);

            mtv_view = itemView.findViewById(R.id.mtv_view);
            mtv_title = itemView.findViewById(R.id.mtv_title);
            mtv_message = itemView.findViewById(R.id.mtv_message);

        }
    }
}
