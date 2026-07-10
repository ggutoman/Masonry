package com.gag.accounting.Adapter.Disbursement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gag.accounting.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.DisbursementDetail;
import org.gag.appdriver.App.Models.ReceiptDetail;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Disbursement_Transactions extends ArrayAdapter<DisbursementDetail> {

    private final Context loInstance;
    private final List<DisbursementDetail> laDetails;

    private List<DisbursementDetail> laDetailsFiltered;

    public Adapter_Disbursement_Transactions(@NonNull Context context, int resource, @NonNull List<DisbursementDetail> objects) {
        super(context, resource, objects);

        loInstance = context;
        laDetails = objects;
        laDetailsFiltered = objects;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(loInstance);
            view = inflater.inflate(R.layout.list_disbursement_details, parent, false);
        }

        MaterialTextView mtv_src_nme = view.findViewById(R.id.mtv_src_nme);
        MaterialTextView mtv_src_amount = view.findViewById(R.id.mtv_src_amount);
        MaterialTextView mtv_src_no = view.findViewById(R.id.mtv_src_no);

        mtv_src_nme.setText(laDetailsFiltered.get(position).getSSourceNme());
        mtv_src_no.setText(laDetailsFiltered.get(position).getSSourceNo());
        mtv_src_amount.setText(laDetailsFiltered.get(position).getNDisbAmt());

        return view;

    }

    @Override
    public int getCount() {
        return laDetailsFiltered.size();
    }

    @Nullable
    @Override
    public DisbursementDetail getItem(int position) {
        return laDetailsFiltered.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<DisbursementDetail> results = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    results.addAll(laDetails);
                } else {
                    for (DisbursementDetail loDetail : laDetails) {

                        if (loDetail.getSSourceNo().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            results.add(loDetail);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = results;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                laDetailsFiltered = (List<DisbursementDetail>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
