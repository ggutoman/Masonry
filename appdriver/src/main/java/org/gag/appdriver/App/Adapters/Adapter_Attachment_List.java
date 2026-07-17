package org.gag.appdriver.App.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.Attachments;
import org.gag.appdriver.R;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Adapter_Attachment_List extends RecyclerView.Adapter<Adapter_Attachment_List.VH_DV_Attachments> {

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

    public Adapter_Attachment_List(Context foInstance, List<Attachments> faReceipts, OnSelectItem foCallback){

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
                holder.mtv_status.setText("File not Found");
                break;
            case "1":
                holder.mtv_status.setText("Downloaded");
                break;
        }

        File loFile = new File(laAttachments.get(position).getSFilePth());

        // Defensive checks: file exists, is not empty
        if (loFile.exists() && loFile.isFile() && loFile.canRead() && loFile.length() > 0) {

            try {
                // Use FileProvider to get a safe content URI
                Uri uri = FileProvider.getUriForFile(
                        holder.itemView.getContext(),
                        holder.itemView.getContext().getPackageName() + ".provider",
                        loFile
                );

                //display image
                Glide.with(holder.itemView.getContext())
                        .asBitmap() // force bitmap decoding
                        .load(!(loFile.getName().contains(".png") || loFile.getName().contains(".jpg")) ? R.drawable.baseline_csv : uri)  // use safe URI instead of raw path
                        .apply(new RequestOptions()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE))
                        .error(R.drawable.no_image_avaialbel)
                        .into(holder.siv_img);

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loCallback.OnSelec(laAttachments.get(position));
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return laAttachmentsFiltered.size();
    }

    public class Adapter_DV_Attachment_Filter extends Filter {

        private final Adapter_Attachment_List loAdapter;
        private List<String> laStatus = new ArrayList<>(List.of("0", "1", "3", "4"));
        public void InitStatus(List<String> faStatus){
            laStatus = faStatus;
        }

        public Adapter_DV_Attachment_Filter(Adapter_Attachment_List foAdapter){
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
