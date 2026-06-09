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
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.masonry.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Libraries.TextLibrary.TextFormatter;
import org.gag.appdriver.Room.Entities.EMemberInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Adapter_Member_List extends RecyclerView.Adapter<Adapter_Member_List.Adapter_Member_List_Holder> {

    private final Context loInstance;
    private final List<EMemberInfo> laMembers;
    private final Adapter_Member_List_Filter loFilter;
    private final OnSelect poCallback;

    private List<EMemberInfo> laMembersFiltered;

    public Adapter_Member_List_Filter GetFilter(){
        return loFilter;
    }

    public interface OnSelect{
        void Selected(EMemberInfo foMember);
    }

    public Adapter_Member_List(Context foContext, List<EMemberInfo> faMembers, OnSelect foCallback){
        this.loInstance = foContext;
        this.laMembers = faMembers;
        this.laMembersFiltered = laMembers;
        this.loFilter = new Adapter_Member_List_Filter(this);
        this.poCallback = foCallback;
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
                holder.siv_status.setImageResource(org.gag.appdriver.R.drawable.baseline_inactive);
                break;
            case "1":
                holder.siv_status.setImageResource(org.gag.appdriver.R.drawable.baseline_active);
                break;
            case "2":
                holder.siv_status.setImageResource(org.gag.appdriver.R.drawable.baseline_suspended);
                break;
            default:

                break;
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                poCallback.Selected(loMember);
            }
        });
    }

    @Override
    public int getItemCount() {
        return laMembersFiltered.size();
    }

    public class Adapter_Member_List_Filter extends Filter{

        private final Adapter_Member_List loAdapter;
        private List<String> laStatus = new ArrayList<>(List.of("0", "1", "2"));

        public void InitStatus(List<String> faStatus){
            laStatus = faStatus;
        }

        public Adapter_Member_List_Filter(Adapter_Member_List foAdapter){
            loAdapter = foAdapter;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            if (charSequence.length() < 1){
                laMembersFiltered = laMembers;
            }else {

                List<EMemberInfo> filterSearch = new ArrayList<>();

                //first filter, via search text
                for (EMemberInfo loMember : laMembers){

                    String lsMiddlNme = loMember.getSMiddName() == null ? "" : loMember.getSMiddName();
                    String lsSuffix = loMember.getSSuffixNm() == null ? "" : loMember.getSSuffixNm();

                    String lsMemberNm = loMember.getSFrstName() + " " +
                                            lsMiddlNme + " " +
                                            loMember.getSLastName() + " " +
                                            lsSuffix;

                    if (lsMemberNm.toLowerCase().contains(charSequence.toString().toLowerCase()) || loMember.getSGLPIDNoX().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filterSearch.add(loMember);
                    }
                }

                laMembersFiltered = filterSearch;
            }

            laMembersFiltered = laMembersFiltered.stream()
                    .filter(loMember -> laStatus.contains(loMember.getCMmbrStat()))
                    .collect(Collectors.toList());

            FilterResults loResults = new FilterResults();
            loResults.values = laMembersFiltered;
            loResults.count = laMembersFiltered.size();

            return loResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            laMembersFiltered = (List<EMemberInfo>) filterResults.values;
            loAdapter.notifyDataSetChanged();
        }
    }

    public static class Adapter_Member_List_Holder extends RecyclerView.ViewHolder{

        private final View view;
        private ShapeableImageView siv_status;
        private final MaterialTextView mtv_name;
        private final MaterialTextView mtv_glipd;
        private final MaterialTextView mtv_datemember;

        public Adapter_Member_List_Holder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            siv_status = itemView.findViewById(R.id.siv_status);
            mtv_name = itemView.findViewById(R.id.mtv_name);
            mtv_glipd = itemView.findViewById(R.id.mtv_glipd);
            mtv_datemember = itemView.findViewById(R.id.mtv_datemember);
        }
    }
}
