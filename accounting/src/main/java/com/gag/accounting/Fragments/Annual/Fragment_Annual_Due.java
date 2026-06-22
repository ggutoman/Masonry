package com.gag.accounting.Fragments.Annual;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Annual;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.AnnualMemberAdapter;
import org.gag.appdriver.App.Adapters.LodgeCalendarAdapter;
import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.Room.Entities.EAnnualMaster;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Fragment_Annual_Due extends Fragment {

    private VM_Annual mViewModel;
    private LoadDialog poLoad;
    private Message_Dialog poMessage;

    private EAnnualMaster poAnualMaster;
    private LodgeCalendarAdapter poCalAdapter;
    private AnnualMemberAdapter poDetailAdapter;
    private AnnualMembers loSelectedDetail;


    private MaterialTextView mtv_status, btn_download, btn_view,  mtv_totaltrans,  mtv_totalcoll;
    private MaterialAutoCompleteTextView auto_lodge_cal, auto_lodge_member;
    private TextInputEditText tie_transaction_no, tie_due, til_remarks, tie_remarks, tie_due_amount, tie_paid_amount, tie_remarks_detail;
    private CheckBox chk_exempt;
    private ImageButton btn_add_member;
    private ConstraintLayout layout_tools;
    private LinearLayout layout_buttons;
    private MaterialButton btn_save, btn_disapprove;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_annual_due, container, false);

        mViewModel = new ViewModelProvider(this).get(VM_Annual.class);
        poLoad = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        poLoad.InitDialog();
        poMessage.InitDialog();

        InitViews(view);
        InitListener();

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            String name = entry.getName() == null ? "" : entry.getName();

            switch (name){

                case "annual_due_info":
                    auto_lodge_cal.setEnabled(true);
                    layout_tools.setVisibility(View.VISIBLE);

                    DownloadInfo();
                    break;

                case "annual_due_entry":
                    auto_lodge_cal.setEnabled(false);
                    layout_tools.setVisibility(View.GONE);
                    break;
            }
        }

        return view;
    }

    private void InitViews(View view){

        mtv_status = view.findViewById(R.id.mtv_status);
        btn_download = view.findViewById(R.id.btn_download);
        btn_view = view.findViewById(R.id.btn_view);
        mtv_totaltrans = view.findViewById(R.id.mtv_totaltrans);
        mtv_totalcoll = view.findViewById(R.id.mtv_totalcoll);

        tie_transaction_no = view.findViewById(R.id.tie_transaction_no);
        auto_lodge_cal = view.findViewById(R.id.auto_lodge_cal);
        auto_lodge_member = view.findViewById(R.id.auto_lodge_member);
        tie_due = view.findViewById(R.id.tie_due);
        til_remarks = view.findViewById(R.id.til_remarks);
        tie_remarks = view.findViewById(R.id.tie_remarks);
        tie_due_amount = view.findViewById(R.id.tie_due_amount);
        tie_paid_amount = view.findViewById(R.id.tie_paid_amount);
        tie_remarks_detail = view.findViewById(R.id.tie_remarks_detail);
        chk_exempt = view.findViewById(R.id.chk_exempt);

        layout_tools = view.findViewById(R.id.layout_tools);
        layout_buttons = view.findViewById(R.id.layout_buttons);

        btn_save = view.findViewById(R.id.btn_save);
        btn_disapprove = view.findViewById(R.id.btn_disapprove);


    }

    private void DownloadInfo(){

        if (getArguments() == null) return;
        mViewModel.DownloadAnnual(getArguments().getString("year_id"), new VM_Annual.OnTransaction() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Downloading annual billing. Please wait . .");
            }

            @Override
            public void OnSuccess() {
                poLoad.DismissDialog();

                InitDataReceiver();
            }

            @Override
            public void OnFailed(String fsMessage) {
                poLoad.DismissDialog();

                poMessage.ShowMessage(1, fsMessage, "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        InitDataReceiver();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
            }
        });

    }

    private void InitDataReceiver(){

        String lsYearIDxx, lsLodgeIDxx;
        if (getArguments() == null){
            lsYearIDxx = "";
            lsLodgeIDxx = "";
        } else {
            lsYearIDxx = getArguments().getString("year_id");
            lsLodgeIDxx = getArguments().getString("lodge_id");
        }

        mViewModel.GetAnnualMaster(lsYearIDxx).observe(getViewLifecycleOwner(), new Observer<EAnnualMaster>() {
            @Override
            public void onChanged(EAnnualMaster eAnnualMaster) {

                if (eAnnualMaster == null){

                    poAnualMaster = new EAnnualMaster(
                            "",
                            "",
                            "1900-00-00",
                            "",
                            "0.00",
                            "0.00",
                            "1",
                            mViewModel.GetUserID(),
                            mViewModel.GetCurrentDate(),
                            mViewModel.GetCurrentDateTime()
                    );
                }else {
                    poAnualMaster = eAnnualMaster;
                }

                tie_transaction_no.setText(poAnualMaster.getSTransNox());
                tie_due.setText(poAnualMaster.getDDueDatex());
                tie_remarks.setText(poAnualMaster.getSRemarksx());

                switch (poAnualMaster.getCTranStat()){

                    case "1":
                        mtv_status.setText("Pending for Approval");
                        break;
                    case "2":
                        mtv_status.setText("Approved");
                        break;
                    case "3":
                        mtv_status.setText("Disapproved");
                        break;
                }

                mViewModel.GetLodgeCalendars(lsLodgeIDxx).observe(getViewLifecycleOwner(), new Observer<List<LodgeCalendarList>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onChanged(List<LodgeCalendarList> lodgeCalendarLists) {

                        if (lodgeCalendarLists == null) return;

                        poCalAdapter = new LodgeCalendarAdapter(requireActivity(),
                                org.gag.appdriver.R.layout.adapter_list_lodge_calendar,
                                lodgeCalendarLists);

                        auto_lodge_cal.setAdapter(poCalAdapter);

                        if (poAnualMaster.getSYearIDxx() == null || poAnualMaster.getSYearIDxx().isEmpty()) return;
                        for (LodgeCalendarList loItem : lodgeCalendarLists){

                            if (loItem.getSYearIDxx().equalsIgnoreCase(poAnualMaster.getSYearIDxx())){
                                auto_lodge_cal.setText(loItem.getSLodgeNme() + "(" + loItem.getNYearxxxx() + ")");
                            }
                        }
                    }
                });

                //add to list the existing records of annual detail
                mViewModel.GetAnnualDetail(poAnualMaster.getSTransNox()).observe(getViewLifecycleOwner(), new Observer<List<AnnualMembers>>() {
                    @Override
                    public void onChanged(List<AnnualMembers> annualMembers) {

                        if (annualMembers == null) return;

                        mViewModel.ClearDetail();
                        for (AnnualMembers loItem : annualMembers){

                            mViewModel.AddAnnualDetail(
                                    loItem.getSMemberID(),
                                    loItem.getSMemberNme(),
                                    loItem.getCExemptID(),
                                    loItem.getSRemarksx(),
                                    loItem.getNAmtDuexx(),
                                    loItem.getNAmtPaidx()
                            );
                        }
                    }
                });

                mViewModel.GetAnnualDetail().observe(getViewLifecycleOwner(), new Observer<List<AnnualMembers>>() {
                    @Override
                    public void onChanged(List<AnnualMembers> annualMembers) {

                        if (annualMembers == null) return;

                        poDetailAdapter = new AnnualMemberAdapter(requireActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, annualMembers);
                        auto_lodge_member.setAdapter(poDetailAdapter);

                        //compute total amount paid and due from detail
                        double ldbl_paid = 0.00, ldbl_due = 0.00;
                        for (AnnualMembers loAnnual : annualMembers){

                            ldbl_paid += Double.parseDouble(loAnnual.getNAmtDuexx());
                            ldbl_due += Double.parseDouble(loAnnual.getNAmtPaidx());
                        }

                        mtv_totaltrans.setText(String.valueOf(ldbl_due));
                        mtv_totalcoll.setText(String.valueOf(ldbl_paid));
                    }
                });

            }
        });
    }

    private void InitListener(){

        auto_lodge_member.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (poDetailAdapter == null) return;
                loSelectedDetail = poDetailAdapter.getItem(i);

                //display details
                auto_lodge_cal.setText(loSelectedDetail.getSMemberNme(), false);
                tie_due.setText(loSelectedDetail.getNAmtDuexx());
                tie_paid_amount.setText(loSelectedDetail.getNAmtPaidx());
                tie_remarks_detail.setText(loSelectedDetail.getSRemarksx());
                chk_exempt.setChecked(loSelectedDetail.getCExemptID().equalsIgnoreCase("1"));
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DownloadInfo();
            }
        });

        btn_add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}