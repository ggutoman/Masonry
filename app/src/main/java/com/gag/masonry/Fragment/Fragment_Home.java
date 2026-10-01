package com.gag.masonry.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.Adapter_Notification;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.App.Models.MemberDashboardInfo;
import org.gag.appdriver.Room.Entities.ESysNotifs;
import org.gag.appdriver.Utilities.Disclosure_Dialog;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Fragment_Home extends Fragment {

    private VM_Main mviewModel;
    private Message_Dialog poMessage;
    private LoadDialog poLoad;

    private OnBackPressedCallback backCallback;

    private MaterialTextView mtv_username, mtv_lodge, mtv_year;

    private ConstraintLayout layout_no_record;
    private RecyclerView rcv_record;
    private BottomNavigationView bar_menu;

    private int bottomBarSelect = 0;

    private String lsMemberId, lsDfrom, lsDto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_home, container, false);

        mviewModel = new ViewModelProvider(requireActivity()).get(VM_Main.class);
        poMessage = new Message_Dialog(requireActivity());
        poLoad = new LoadDialog(requireActivity());

        poMessage.InitDialog();
        poLoad.InitDialog();

        String lsCurrentYear = mviewModel.GetFormattedDate(mviewModel.GetCurrentDate(), "yyyy");

        //get max date for december
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(lsCurrentYear));
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);

        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        //initialize default range of date to display for the whole year
        lsDfrom = mviewModel.GetFormattedDate(lsCurrentYear + "-01-01", "yyyy-MM-dd");
        lsDto =  mviewModel.GetFormattedDate(lsCurrentYear + "-12-" + String.valueOf(maxDay), "yyyy-MM-dd");

        InitViews(view);
        InitData();
        InitListeners();

        backCallback = new OnBackPressedCallback(false) { // start disabled
            @Override
            public void handleOnBackPressed() {
                poMessage.ShowMessage(2,
                        "You're about to exit the application. Continue?",
                        "No", "Yes",
                        new Message_Dialog.OnDialogClick() {
                            @Override
                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();
                            }

                            @Override
                            public void OnNegative(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();
                                requireActivity().finishAffinity();
                            }
                        });
            }
        };

        requireActivity().getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), backCallback);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (backCallback != null) backCallback.setEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (backCallback != null) backCallback.setEnabled(false);
    }

    private void InitViews(View view){

        mtv_username = view.findViewById(R.id.mtv_username);
        mtv_lodge = view.findViewById(R.id.mtv_lodge);
        mtv_year = view.findViewById(R.id.mtv_year);

        bar_menu = view.findViewById(R.id.bar_menu);
        rcv_record = view.findViewById(R.id.rcv_record);
        layout_no_record = view.findViewById(R.id.layout_no_record);
    }

    private void InitData(){

        mviewModel.ObserveMemberInfo().observe(getViewLifecycleOwner(), new Observer<MemberDashboardInfo>() {
            @Override
            public void onChanged(MemberDashboardInfo eMemberInfo) {

                if (eMemberInfo == null) return;

                String firstName  = eMemberInfo.getSFrstName() == null ? "" : eMemberInfo.getSFrstName();
                String middleName = eMemberInfo.getSMiddName() == null ? "" : eMemberInfo.getSMiddName();
                String lastName   = eMemberInfo.getSLastName() == null ? "" : eMemberInfo.getSLastName();
                String suffix     = eMemberInfo.getSSuffixNm()  == null ? "" : eMemberInfo.getSSuffixNm();

                String middleInit = !middleName.isEmpty() ? middleName.substring(0, 1) + "." : "";
                String suffixInit = !suffix.isEmpty() ? " " + suffix : "";
                String fullName = firstName + " " + middleInit + " " + lastName + suffixInit;


                lsMemberId = eMemberInfo.getSMemberID();

                mtv_username.setText(fullName);
                mtv_lodge.setText(eMemberInfo.getSLodgeNme());

                if (lsMemberId == null || lsMemberId.isEmpty()) return;

                mviewModel.GetNotifications(lsMemberId).observe(getViewLifecycleOwner(), new Observer<List<ESysNotifs>>() {
                    @Override
                    public void onChanged(List<ESysNotifs> eSysNotifs) {

                        if (eSysNotifs == null || eSysNotifs.size() < 1){
                            layout_no_record.setVisibility(View.VISIBLE);
                            rcv_record.setVisibility(View.GONE);
                            return;
                        }

                        List<ESysNotifs> laNotifs = new ArrayList<>();
                        for (ESysNotifs notif : eSysNotifs){

                            if (bottomBarSelect > 0){
                                if (notif.getSSourcexx().equalsIgnoreCase("PROJ") || notif.getSSourcexx().equalsIgnoreCase("PLDG")) laNotifs.add(notif);
                            }else{
                                if (notif.getSSourcexx().equalsIgnoreCase("MBR") || notif.getSSourcexx().equalsIgnoreCase("DUES")) laNotifs.add(notif);
                            }

                        }

                        layout_no_record.setVisibility(laNotifs.size() > 0 ? View.GONE : View.VISIBLE);
                        rcv_record.setVisibility(laNotifs.size() > 0 ? View.VISIBLE : View.GONE);

                        rcv_record.setAdapter(new Adapter_Notification(laNotifs, new Adapter_Notification.OnAction() {
                            @Override
                            public void OnViewDetails(ESysNotifs notif) {

                                Disclosure_Dialog loDialog = new Disclosure_Dialog(requireActivity());
                                loDialog.InitDialog();

                                //load content of notification
                                loDialog.ShowDisclosure(org.gag.appdriver.R.drawable.masonry_logo, notif.getSMessagex(), "Close", "", new Disclosure_Dialog.OnDialogClick() {
                                    @Override
                                    public void OnPositive(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();

                                        //after viewing, update notification as read
                                        mviewModel.TagNotifRead(notif, new VM_Main.OnDownloadData() {
                                            @Override
                                            public void OnDownload() {
                                                Toast.makeText(requireActivity(), "Tagging notification as read. Please wait . . .", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void OnFinished(String fsMessage) {
                                                poLoad.DismissDialog();
                                                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                                });
                            }
                        }));
                        rcv_record.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
                    }
                });
            }
        });

        mviewModel.GetLodgeCalendarList(lsDfrom, lsDto).observe(getViewLifecycleOwner(), new Observer<List<LodgeCalendarList>>() {
            @Override
            public void onChanged(List<LodgeCalendarList> lodgeCalendarLists) {

                if (lodgeCalendarLists == null || lodgeCalendarLists.size() < 1){
                    mtv_year.setVisibility(View.GONE);
                    return;
                }

                for (LodgeCalendarList calendars : lodgeCalendarLists){

                    if (calendars.getNYearxxxx().equalsIgnoreCase(mviewModel.GetFormattedDate(mviewModel.GetCurrentDate(), "yyyy"))){
                        mtv_year.setText("(M.Y " + calendars.getNYearxxxx() + " - " + String.valueOf(Integer.parseInt(calendars.getNYearxxxx()) + 1) + ")");
                        mtv_year.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        });

    }

    private void InitListeners(){

        bar_menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                //reset display
                layout_no_record.setVisibility(View.VISIBLE);
                rcv_record.setVisibility(View.GONE);

                if (item.getItemId() == com.gag.accounting.R.id.action_item_dues) {
                    bottomBarSelect = 0;
                    InitData();
                    return true;
                }else if (item.getItemId() == com.gag.accounting.R.id.action_item_project) {
                    bottomBarSelect = 1;
                    InitData();
                    return true;
                }

                return false;
            }
        });
    }

}