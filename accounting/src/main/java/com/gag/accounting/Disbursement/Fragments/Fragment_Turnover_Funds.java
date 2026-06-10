package com.gag.accounting.Disbursement.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gag.accounting.Disbursement.ViewModel.VM_Funds;
import com.gag.accounting.R;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.LodgeCalendarAdapter;
import org.gag.appdriver.Room.DataObject.DLodgeCalendar;

import java.util.List;

public class Fragment_Turnover_Funds extends Fragment {

    private VM_Funds mViewmodel;
    private LodgeCalendarAdapter loLodgeCalAdapter;

    private TextInputEditText tie_transaction_no, tie_fund_amt, tie_ending_bal, tie_remarks;
    private MaterialAutoCompleteTextView auto_lodge_cal;
    private MaterialTextView mtv_status;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.fragment_turnover_funds, container, false);

        mViewmodel = new ViewModelProvider(requireActivity()).get(VM_Funds.class);

        InitViews(view);
        InitDataReceiver();
        InitListener();

        return view;
    }

    private void InitViews(View view){

        tie_transaction_no = view.findViewById(R.id.tie_transaction_no);
        auto_lodge_cal = view.findViewById(R.id.auto_lodge_cal);
        tie_fund_amt = view.findViewById(R.id.tie_fund_amt);
        tie_ending_bal = view.findViewById(R.id.tie_ending_bal);
        mtv_status = view.findViewById(R.id.mtv_status);
        tie_remarks = view.findViewById(R.id.tie_remarks);
    }

    private void InitDataReceiver(){

        mViewmodel.GetLodgeCalendars().observe(getViewLifecycleOwner(), new Observer<List<DLodgeCalendar.LodgeCalendarList>>() {
            @Override
            public void onChanged(List<DLodgeCalendar.LodgeCalendarList> lodgeCalendarLists) {

                if (lodgeCalendarLists == null) return;

                loLodgeCalAdapter = new LodgeCalendarAdapter(
                        requireActivity(),
                        org.gag.appdriver.R.layout.adapter_list_lodge_calendar,
                        lodgeCalendarLists
                );

                auto_lodge_cal.setAdapter(loLodgeCalAdapter);

            }
        });
    }

    private void InitListener(){

        auto_lodge_cal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                DLodgeCalendar.LodgeCalendarList loSelectedCal = (DLodgeCalendar.LodgeCalendarList) adapterView.getItemAtPosition(i);
                auto_lodge_cal.setText(loSelectedCal.getSLodgeNme() + "(" + loSelectedCal.getNYearxxxx() + ")", false);
            }
        });
    }
}