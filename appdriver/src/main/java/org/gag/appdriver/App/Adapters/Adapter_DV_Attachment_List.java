package org.gag.appdriver.App.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.Attachments;
import org.gag.appdriver.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Adapter_DV_Attachment_List extends RecyclerView.Adapter<Adapter_DV_Attachment_List.VH_DV_Attachments> {

    private final Context loInstance;
    private final List<Attachments> laAttachments;
    private final OnSelectItem loCallback;
    private final Adapter_DV_Attachment_Filter loFilter;

    private List<Attachments> laAttachmentsFiltered;

    public Adapter_DV_Attachment_Filter GetFilter(){
        return loFilter;
    }

    public interface OnSelectItem {
        void OnSelec(Attachments loItem);
    }

    public Adapter_DV_Attachment_List(Context foInstance, List<Attachments> faReceipts, OnSelectItem foCallback){

        loInstance = foInstance;
        laAttachments = faReceipts;
        laAttachmentsFiltered = laAttachments;
        loCallback = foCallback;
        loFilter = new Adapter_DV_Attachment_Filter(this);
    }

    @NonNull
    @Override
    public VH_DV_Attachments onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH_DV_Attachments(
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.list_attachments,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH_DV_Attachments holder, int position) {

        holder.mtv_doc_title.setText(laAttachmentsFiltered.get(position).getSDocTitlex());
        holder.mtv_date_transact.setText(laAttachmentsFiltered.get(position).getDFile());

        switch (laAttachmentsFiltered.get(position).getCRecdStat()){

            case "0":
                holder.mtv_status.setText("Pending for Upload");
                break;
            case "1":
                holder.mtv_status.setText("Uploaded");
                break;
        }

        // Load from file path
        Glide.with(loInstance)
                .load(new File(laAttachmentsFiltered.get(position).getSFilePth()))
                .placeholder(org.gag.appdriver.R.drawable.no_image_avaialbel)
                .error(org.gag.appdriver.R.drawable.no_image_avaialbel)
                .skipMemoryCache(true)   // disable memory cache
                .diskCacheStrategy(DiskCacheStrategy.NONE) // disable disk cache
                .into(holder.siv_img);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loCallback.OnSelec(laAttachmentsFiltered.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return laAttachmentsFiltered.size();
    }

    public class Adapter_DV_Attachment_Filter extends Filter {

        private final Adapter_DV_Attachment_List loAdapter;
        private List<String> laStatus = new ArrayList<>(List.of("0", "1", "3", "4"));
        public void InitStatus(List<String> faStatus){
            laStatus = faStatus;
        }

        public Adapter_DV_Attachment_Filter(Adapter_DV_Attachment_List foAdapter){
            loAdapter = foAdapter;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            if (charSequence.length() < 1){
                laAttachmentsFiltered = laAttachments;
            }else {

                List<Attachments> filterSearch = new ArrayList<>();

                //first filter, via search text
                for (Attachments loAttachment : laAttachments){

                    if (loAttachment.getSDocTitlex().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filterSearch.add(loAttachment);
                    }
                }
                laAttachmentsFiltered = filterSearch;
            }

            laAttachmentsFiltered = laAttachmentsFiltered.stream()
                    .filter(loItem -> laStatus.contains(loItem.getCRecdStat()))
                    .collect(Collectors.toList());

            FilterResults loResults = new FilterResults();
            loResults.values = laAttachmentsFiltered;
            loResults.count = laAttachmentsFiltered.size();

            return loResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            laAttachmentsFiltered = (List<Attachments>) filterResults.values;
            loAdapter.notifyDataSetChanged();
        }
    }

    public static class VH_DV_Attachments extends RecyclerView.ViewHolder {

        private final View view;
        private final ShapeableImageView siv_img;
        private final MaterialTextView mtv_doc_title, mtv_date_transact, mtv_status;

        public VH_DV_Attachments(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            siv_img = itemView.findViewById(R.id.siv_img);
            mtv_doc_title = itemView.findViewById(R.id.mtv_doc_title);
            mtv_date_transact = itemView.findViewById(R.id.mtv_date_transact);
            mtv_status = itemView.findViewById(R.id.mtv_status);
        }

    }
}
