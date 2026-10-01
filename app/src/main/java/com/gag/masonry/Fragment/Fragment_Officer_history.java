package com.gag.masonry.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;

import com.gag.masonry.Adapter.Officer_History_Adapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.OfficerHistory;
import org.gag.appdriver.Room.Entities.EPosition;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Fragment_Officer_history extends Fragment {

    private VM_Main mViewmodel;
    private LoadDialog poDialog;
    private Message_Dialog poMessage;
    private Officer_History_Adapter loAdapter;

    private PopupMenu loMenu;
    private SubMenu loSubPosMenu;
    private List<String> laPositions = new ArrayList<>();

    private String lsQuery, lsYear;

    private ConstraintLayout layout_no_record;
    private MaterialTextView mtv_title, mtv_subtitle;
    private TextInputLayout til_search;
    private TextInputEditText tie_search;
    private ImageButton btn_filter;
    private RecyclerView rcv_officer_list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_officer_history, container, false);

        mViewmodel = new ViewModelProvider(requireActivity()).get(VM_Main.class);
        poDialog = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        poDialog.InitDialog();
        poMessage.InitDialog();

        lsYear = mViewmodel.GetFormattedDate(mViewmodel.GetCurrentDate(), "yyyy");

        DownloadList();

        InitViews(view);
        InitListener();
        InitDataReceiver();

        loMenu = new PopupMenu(requireContext(), btn_filter);
        loMenu.getMenuInflater().inflate(R.menu.menu_filter_officer_history, loMenu.getMenu());

        //sub menu for positions
        loSubPosMenu = loMenu.getMenu().addSubMenu(Menu.NONE, 101, Menu.NONE, "By Position");
        loSubPosMenu.add(101, 102, Menu.NONE, "ALL");

        return view;
    }

    private void InitViews(View view){

        layout_no_record = view.findViewById(R.id.layout_no_record);
        mtv_title = view.findViewById(R.id.mtv_title);
        mtv_subtitle = view.findViewById(R.id.mtv_subtitle);
        til_search = view.findViewById(R.id.til_search);
        tie_search = view.findViewById(R.id.tie_search);
        btn_filter = view.findViewById(R.id.btn_filter);
        rcv_officer_list = view.findViewById(R.id.rcv_officer_list);
    }

    private String GetBaseQuery() {
        return " c.nYearxxxx= '" + lsYear + "' ";
    }

    private void InitDataReceiver(){

        lsQuery = GetBaseQuery();


        if (laPositions.size() > 0) lsQuery += "AND d.sPositnDs IN (" + String.join(",", laPositions) + ") ";

        if (getArguments() == null || getArguments().getString("member_id").isEmpty()){
            if (!(tie_search.getText() == null || tie_search.getText().toString().isEmpty())) lsQuery += " AND (b.sFrstName || ' ' || b.sLastName) LIKE '%" + tie_search.getText().toString() + "%' ";
        }else {
            lsQuery += " AND (b.sMemberID= '" + getArguments().getString("member_id") + "') ";
            til_search.setEnabled(false);
        }
        mViewmodel.FilterOfficerHistory(lsQuery);

        mViewmodel.FilterHistory().observe(getViewLifecycleOwner(), new Observer<String>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(String s) {

                if (s.isEmpty()) return;

                mtv_title.setText(getArguments() == null || getArguments().getString("member_name").isEmpty() ? "Officer History" : getArguments().getString("member_name"));
                mtv_subtitle.setText("(M.Y " + lsYear + " - M.Y " + (Integer.parseInt(lsYear) + 1) + ")");

                List<OfficerHistory> laHistory = mViewmodel.SearchOfficerHistory(s);
                if (laHistory == null || laHistory.size() < 1){
                    layout_no_record.setVisibility(View.VISIBLE);
                    rcv_officer_list.setVisibility(View.GONE);
                    return;
                }
                layout_no_record.setVisibility(View.GONE);
                rcv_officer_list.setVisibility(View.VISIBLE);

                loAdapter = new Officer_History_Adapter(laHistory, !(getArguments() == null || getArguments().getString("member_id").isEmpty()));
                loAdapter.notifyDataSetChanged();

                rcv_officer_list.setAdapter(loAdapter);
                rcv_officer_list.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
            }
        });

        mViewmodel.GetPositions().observe(getViewLifecycleOwner(), new Observer<List<EPosition>>() {
            @Override
            public void onChanged(List<EPosition> ePositions) {

                if (ePositions == null || ePositions.size() < 1) return;

                loSubPosMenu.clear();
                loSubPosMenu.add(101, 102, Menu.NONE, "ALL");

                int parentID = 103;
                for (EPosition loPosition : ePositions) {
                    loSubPosMenu.add(parentID, parentID++, Menu.NONE, loPosition.getSPositnDs());
                }
            }
        });
    }

    private void InitListener(){

        tie_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                InitDataReceiver();
            }
        });

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //initialize pop up object, menu object holder
                loMenu.show();

                //initialize year picker custom
                NumberPicker yearPicker = new NumberPicker(requireContext());
                yearPicker.setMinValue(Integer.parseInt(mViewmodel.GetFormattedDate(mViewmodel.GetCurrentDate(), "yyyy")));
                yearPicker.setMaxValue(9999);
                yearPicker.setValue(Calendar.getInstance().get(Calendar.YEAR));

                LinearLayout layout = new LinearLayout(requireContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(yearPicker);

                loMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.action_item_filter){

                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Select Year")
                                    .setView(layout)
                                    .setPositiveButton("OK", (dialog, which) -> {

                                        lsYear = String.valueOf(yearPicker.getValue());
                                        InitDataReceiver();
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();

                            return true;

                        }else if (menuItem.getItemId() == R.id.action_item_download){

                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Select Year")
                                    .setView(layout)
                                    .setPositiveButton("OK", (dialog, which) -> {

                                        lsYear = String.valueOf(yearPicker.getValue());
                                        poMessage.ShowMessage(2,  "Do you want download officer records for the year " + lsYear + " ?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                                            @Override
                                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                                poDialog.dismiss();
                                            }

                                            @Override
                                            public void OnNegative(@NotNull AlertDialog poDialog) {
                                                poDialog.dismiss();

                                                //download data from server with date to get
                                                DownloadList();
                                            }
                                        });
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();

                            return true;

                        } else if (menuItem.getItemId() == 102){ //all positions

                            laPositions.clear();
                            for (int i = 0; i < loSubPosMenu.size(); i++){
                                laPositions.add("'"+ loSubPosMenu.getItem(i).getTitle().toString() + "'");
                            }
                            InitDataReceiver();

                            return true;
                        }else {
                            if (!(menuItem.getItemId() == 101 || menuItem.getItemId() == R.id.action_by_date)){
                                laPositions.clear();
                                laPositions.add("'"+ menuItem.getTitle().toString()  + "'");

                                InitDataReceiver();
                            }
                        }
                        return false;
                    }
                });
            }
        });
    }

    private void DownloadList(){

        String lsMemberID;
        if (getArguments() == null || getArguments().getString("member_id").isEmpty()){
            lsMemberID = "";
        }else {
            lsMemberID = getArguments().getString("member_id");
        }

        mViewmodel.DownloadOfficerHistory(lsMemberID, lsYear, new VM_Main.OnDownloadData() {
            @Override
            public void OnDownload() {
                poDialog.ShowDialog("Downloading history. Plaese wait . .");
            }

            @Override
            public void OnFinished(String fsMessage) {
                poDialog.DismissDialog();
                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();

                InitDataReceiver();
            }
        });

    }
}