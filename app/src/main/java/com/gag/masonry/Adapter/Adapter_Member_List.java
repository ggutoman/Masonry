package com.gag.masonry.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.masonry.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Libraries.TextLibrary.TextFormatter;
import org.gag.appdriver.Room.Entities.EMemberInfo;

import java.util.List;

public class Adapter_Member_List extends RecyclerView.Adapter<Adapter_Member_List.Adapter_Member_List_Holder> {

    private final Context loInstance;
    private final List<EMemberInfo> laMembers;
    private final List<EMemberInfo> laMembersFiltered;

    public Adapter_Member_List(Context foContext, List<EMemberInfo> faMembers){
        this.loInstance = foContext;
        this.laMembers = faMembers;
        this.laMembersFiltered = laMembers;
    }

    @NonNull
    @Override
    public Adapter_Member_List_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Adapter_Member_List_Holder(
                LayoutInflater.from(loInstance).inflate(R.layout.list_members, parent, false)
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Adapter_Member_List_Holder holder, int position) {

        EMemberInfo loMember = laMembersFiltered.get(position);

        String lsMidNme = loMember.getSMiddName() == null ? "" : loMember.getSMiddName();
        String lsMidInit = lsMidNme.isEmpty() ? "" : lsMidNme.substring(0, 1) + ".";

        String lsSuffix = loMember.getSSuffixNm() == null ? "" : " " + loMember.getSSuffixNm();

        holder.mtv_name.setText(
                loMember.getSFrstName() + " " +
                        lsMidInit + (lsMidInit.isEmpty() ? "" : " ") +
                        loMember.getSLastName() +
                        lsSuffix
        );
        holder.mtv_glipd.setText(loMember.getSGLPIDNoX());
        holder.mtv_datemember.setText(loMember.getDMembrshp());

        switch (loMember.getCMmbrStat()){
            case "0":
                holder.mtv_status.setText("Inactive");
                break;
            case "1":
                holder.mtv_status.setText("Active");
                holder.mtv_status.setTextColor(Color.GREEN);
                break;
            case "2":
                holder.mtv_status.setText("Suspended");
                holder.mtv_status.setTextColor(Color.RED);
                break;
            default:
                holder.mtv_status.setText("N/A");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return laMembersFiltered.size();
    }

    public static class Adapter_Member_List_Holder extends RecyclerView.ViewHolder{

        private MaterialTextView mtv_name, mtv_glipd, mtv_status, mtv_datemember;

        public Adapter_Member_List_Holder(@NonNull View itemView) {
            super(itemView);

            mtv_name = itemView.findViewById(R.id.mtv_name);
            mtv_glipd = itemView.findViewById(R.id.mtv_glipd);
            mtv_status = itemView.findViewById(R.id.mtv_status);
            mtv_datemember = itemView.findViewById(R.id.mtv_datemember);
        }
    }
}
