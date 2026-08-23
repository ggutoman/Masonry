package org.gag.appdriver.App.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import org.gag.appdriver.Room.Entities.EReports;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Reports_List extends RecyclerView.Adapter<Adapter_Reports_List.VH_Reports> {

    private final Context loContext;
    private final List<EReports> laReports;
    private List<EReports> laReportsFiltered;
    private OnReport loCallback;

    private final Adapter_Reports_Filter loFilter = new Adapter_Reports_Filter(this);

    public interface OnReport{
        void OnSelect(EReports foReport);
    }

    public Adapter_Reports_List(Context context, List<EReports> objects, OnReport foCallback) {

        loContext = context;
        laReports = objects;
        laReportsFiltered = objects;

        loCallback = foCallback;
    }

    public Adapter_Reports_Filter GetFilter(){
        return loFilter;
    }

    @NonNull
    @Override
    public VH_Reports onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(loContext).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        return new VH_Reports(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Reports holder, int position) {

        holder.mtv_descr.setText(laReportsFiltered.get(position).getXxsDescript());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loCallback.OnSelect(laReportsFiltered.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return laReportsFiltered.size();
    }

    public class Adapter_Reports_Filter extends Filter{

        private final Adapter_Reports_List loAdapter;

        public Adapter_Reports_Filter(Adapter_Reports_List foAdapter){
            loAdapter = foAdapter;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            if (charSequence.length() < 1){
                laReportsFiltered = laReports;
            }else {

                List<EReports> filterSearch = new ArrayList<>();

                //first filter, via search text
                for (EReports loReport : laReports){

                    if (loReport.getXxsDescript().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filterSearch.add(loReport);
                    }
                }
                laReportsFiltered = filterSearch;
            }

            FilterResults loResults = new FilterResults();
            loResults.values = laReportsFiltered;
            loResults.count = laReportsFiltered.size();

            return loResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            laReportsFiltered = (List<EReports>) filterResults.values;
            loAdapter.notifyDataSetChanged();
        }
    }

    public static class VH_Reports extends RecyclerView.ViewHolder{

        View view;
        MaterialTextView mtv_descr;

        public VH_Reports(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            mtv_descr = itemView.findViewById(android.R.id.text1);
        }
    }

}
