package com.gag.accounting.Adapter.Disbursement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gag.accounting.R;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Constants.DISBURSEMENT_CONSTANTS;
import org.gag.appdriver.Room.Entities.EDisbursementAttachment;

import java.util.List;

public class Adapter_Disbursement_Attachments extends ArrayAdapter<EDisbursementAttachment> {

    private final Context loInstance;
    public final List<EDisbursementAttachment> laAttachments;

    public Adapter_Disbursement_Attachments(@NonNull Context context, List<EDisbursementAttachment> faAttachments) {
        super(context, 0, faAttachments);

        loInstance = context;
        laAttachments = faAttachments;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {

            LayoutInflater inflater = LayoutInflater.from(loInstance);
            view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView text1= view.findViewById(android.R.id.text1);
        text1.setText(laAttachments.get(position).getSFileName());

        return view;

    }

    @Override
    public int getCount() {
        return laAttachments.size();
    }

    @Nullable
    @Override
    public EDisbursementAttachment getItem(int position) {
        return laAttachments.get(position);
    }

}
