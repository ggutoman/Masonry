package org.gag.appdriver.App.Adapters;

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

import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.R;

import java.util.ArrayList;
import java.util.List;

public class LodgeCalendarListAdapter extends RecyclerView.Adapter<LodgeCalendarListAdapter.VH_Lodge_Calendar_List> {

    private final Context loContext;
    private final LodgeFilter loFilter;
    private final List<LodgeCalendarList> lodges;
    private final OnSelectCalendar loCallback;

    public List<LodgeCalendarList> lodgesFiltered;

    public interface OnSelectCalendar{
        void Selected(LodgeCalendarList poItem);
    }

    public LodgeCalendarListAdapter(Context context, List<LodgeCalendarList> objects, OnSelectCalendar foCallback){

        loContext = context;
        lodges = objects;
        lodgesFiltered = objects;
        loCallback = foCallback;

        loFilter = new LodgeFilter(this);
    }

    public LodgeFilter GetFilter(){
        return loFilter;
    }

    @NonNull
    @Override
    public VH_Lodge_Calendar_List onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new VH_Lodge_Calendar_List(
                LayoutInflater.from(loContext).inflate(R.layout.adapter_list_lodge_calendar, parent, false)
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VH_Lodge_Calendar_List holder, int position) {

        holder.mtv_descr.setText(lodgesFiltered.get(position).getSLodgeNme() + " " + "(" + lodgesFiltered.get(position).getNYearxxxx() + ")");
        holder.mtv_validity.setText(lodgesFiltered.get(position).getDThruDate());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loCallback.Selected(lodgesFiltered.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return lodgesFiltered.size();
    }

    public class LodgeFilter extends Filter{

        private LodgeCalendarListAdapter loAdapter;

        public LodgeFilter(LodgeCalendarListAdapter foAdapter){
            loAdapter = foAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<LodgeCalendarList> results = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                results.addAll(lodges);
            } else {
                for (LodgeCalendarList lodgeCalendar : lodges) {

                    if (lodgeCalendar.getSLodgeNme().toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                            lodgeCalendar.getNYearxxxx().equalsIgnoreCase(charSequence.toString().toLowerCase())) {

                        results.add(lodgeCalendar);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = results;

            return filterResults;

        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            lodgesFiltered = (List<LodgeCalendarList>) filterResults.values;
            loAdapter.notifyDataSetChanged();
        }
    }

    public static class VH_Lodge_Calendar_List extends RecyclerView.ViewHolder{

        private View view;
        private MaterialTextView mtv_descr, mtv_validity;

        public VH_Lodge_Calendar_List(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            mtv_descr = itemView.findViewById(R.id.mtv_descr);
            mtv_validity = itemView.findViewById(R.id.mtv_validity);
        }
    }
}