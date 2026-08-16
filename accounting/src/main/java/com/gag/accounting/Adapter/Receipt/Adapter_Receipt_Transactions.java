package com.gag.accounting.Adapter.Receipt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.accounting.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.ReceiptDetail;
import org.gag.appdriver.Room.Entities.EMemberContactInfo;
import org.gag.appdriver.Room.Entities.EReceiptDetail;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Receipt_Transactions extends ArrayAdapter<ReceiptDetail> {

    private final Context loInstance;
    private final List<ReceiptDetail> laDetails;

    private List<ReceiptDetail> laDetailsFiltered;

    public Adapter_Receipt_Transactions(@NonNull Context context, int resource, @NonNull List<ReceiptDetail> objects) {
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
            view = inflater.inflate(R.layout.list_receipt_details, parent, false);
        }

        MaterialTextView mtv_src_no = view.findViewById(R.id.mtv_src_no);
        MaterialTextView mtv_src_amount = view.findViewById(R.id.mtv_src_amount);
        MaterialTextView mtv_src_code = view.findViewById(R.id.mtv_src_code);

        mtv_src_no.setText(laDetailsFiltered.get(position).getSSourceDscrpt().isEmpty() ? laDetailsFiltered.get(position).getSSourceNo() : laDetailsFiltered.get(position).getSSourceDscrpt());
        mtv_src_amount.setText(laDetailsFiltered.get(position).getNAmtPaidx());

        mtv_src_no.setVisibility(laDetailsFiltered.get(position).getSSourceCD().equalsIgnoreCase("DUES") || laDetailsFiltered.get(position).getSSourceCD().equalsIgnoreCase("PROJ") ? View.VISIBLE : View.GONE);

        switch (laDetailsFiltered.get(position).getSSourceCD()){

            case "DUES":
                mtv_src_code.setText("Annual Dues");
                break;
            case "PROJ":
                mtv_src_code.setText("Project");
                break;
            case "BINT":
                mtv_src_code.setText("Bank Interest");
                break;
            case "OTHR":
                mtv_src_code.setText("Others");
                break;
        }
        return view;

    }

    @Override
    public int getCount() {
        return laDetailsFiltered.size();
    }

    @Nullable
    @Override
    public ReceiptDetail getItem(int position) {
        return laDetailsFiltered.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<ReceiptDetail> results = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    results.addAll(laDetails);
                } else {
                    for (ReceiptDetail loDetail : laDetails) {

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
                laDetailsFiltered = (List<ReceiptDetail>) results.values;
                notifyDataSetChanged();
            }
        };
    }


}
