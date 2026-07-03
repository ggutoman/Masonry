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

import org.gag.appdriver.App.Models.LodgeInfo;
import org.gag.appdriver.R;
import org.gag.appdriver.Room.Entities.ELodgeInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LodgeAdapterList extends RecyclerView.Adapter<LodgeAdapterList.VH_Lodge> {

    private final Context loContext;
    private final SelectItem loCallback;
    public final List<LodgeInfo> lodges;
    private final Adapter_Lodge_List_Filter loFilter;

    public List<LodgeInfo> lodgesFiltered;

    public Adapter_Lodge_List_Filter GetFilter(){
        return loFilter;
    }

    public interface SelectItem{
        void OnSelect(LodgeInfo lodge);
    }

    public LodgeAdapterList( @NonNull Context context, @NonNull List<LodgeInfo> objects, SelectItem foCallback){

        loContext = context;
        lodges = objects;
        loCallback = foCallback;

        loFilter = new Adapter_Lodge_List_Filter(this);
        lodgesFiltered = lodges;
    }

    @NonNull
    @Override
    public VH_Lodge onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new VH_Lodge(
                LayoutInflater.from(loContext).inflate(R.layout.adapter_list_lodge, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Lodge holder, int position) {

        holder.mtv_name.setText(lodgesFiltered.get(position).getSLodgeNme());

        String lsFullAddress = lodgesFiltered.get(position).getSAddressx() +  ", " + lodgesFiltered.get(position).getSTownName() + " " + lodgesFiltered.get(position).getSProvName();
        holder.mtv_address.setText(lsFullAddress);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loCallback.OnSelect(lodgesFiltered.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return lodgesFiltered.size();
    }

    public class Adapter_Lodge_List_Filter extends Filter {

        private final LodgeAdapterList loAdapter;

        public Adapter_Lodge_List_Filter(LodgeAdapterList foAdapter){
            loAdapter = foAdapter;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            if (charSequence.length() < 1){
                lodgesFiltered = lodges;
            }else {

                List<LodgeInfo> filterSearch = new ArrayList<>();

                //first filter, via search text
                for (LodgeInfo loLodge : lodges){

                    if (loLodge.getSLodgeNme().toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                            loLodge.getSAddressx().toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                            loLodge.getSTownName().toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                            loLodge.getSProvName().toLowerCase().contains(charSequence.toString().toLowerCase())
                    ){
                        filterSearch.add(loLodge);
                    }
                }

                lodgesFiltered = filterSearch;
            }

            FilterResults loResults = new FilterResults();
            loResults.values = lodgesFiltered;
            loResults.count = lodgesFiltered.size();

            return loResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            lodgesFiltered = (List<LodgeInfo>) filterResults.values;
            loAdapter.notifyDataSetChanged();
        }
    }

    public static class VH_Lodge extends RecyclerView.ViewHolder{

        public View view;

        public MaterialTextView mtv_name, mtv_address;

        public VH_Lodge(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            mtv_name = itemView.findViewById(R.id.mtv_name);
            mtv_address = itemView.findViewById(R.id.mtv_address);

        }
    }
}