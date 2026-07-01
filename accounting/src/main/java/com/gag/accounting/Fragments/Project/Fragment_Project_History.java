package com.gag.accounting.Fragments.Project;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.gag.accounting.Adapter.Project.Adapter_Project_List;
import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Projects;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import org.gag.appdriver.App.Fragments.Fragment_Child_Container;
import org.gag.appdriver.Room.Entities.EProjectMaster;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Fragment_Project_History extends Fragment {

    private VM_Projects mViewModel;
    private Message_Dialog poMessage;
    private LoadDialog poLoading;
    private Adapter_Project_List loAdapter;

    private String lsDfrom, lsDto;

    private TextInputEditText tie_search;
    private ImageButton btn_filter;
    private ConstraintLayout layout_no_record;
    private RecyclerView rcv_project_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__project__history, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(VM_Projects.class);
        poMessage = new Message_Dialog(requireActivity());
        poLoading = new LoadDialog(requireActivity());

        lsDfrom = mViewModel.GetCurrentDate();

        int currentMnth = Integer.parseInt(mViewModel.GetFormattedDate(lsDfrom, "MM"));
        lsDto = mViewModel.GetCountedDate( (12 - currentMnth) + currentMnth, 0, true);

        poMessage.InitDialog();
        poLoading.InitDialog();

        InitViews(view);
        InitDataReceiver();
        InitListener();

        return view;
    }

    private void InitViews(View view){
        tie_search = view.findViewById(R.id.tie_search);
        btn_filter = view.findViewById(R.id.btn_filter);
        layout_no_record = view.findViewById(R.id.layout_no_record);
        rcv_project_list = view.findViewById(R.id.rcv_project_list);
    }

    private void DownloadList(){

        if (getArguments() == null || getArguments().getString("year_id") == null){
            Toast.makeText(requireActivity(), "Please select year id", Toast.LENGTH_SHORT).show();
            return;
        }

        mViewModel.DownloadProjects(getArguments().getString("year_id") == null ? "" : getArguments().getString("year_id"), lsDfrom, lsDto, new VM_Projects.OnTransaction() {
            @Override
            public void OnLoad() {
                poLoading.ShowDialog("Downloading projects. Please wait . .");
            }

            @Override
            public void OnSuccess() {
                poLoading.DismissDialog();

                Toast.makeText(requireActivity(), "Projects downloaded successfully", Toast.LENGTH_SHORT).show();
                InitDataReceiver();
            }

            @Override
            public void OnFailed(String fsMessage) {
                poLoading.DismissDialog();

                poMessage.ShowMessage(1, fsMessage, "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
            }
        });
    }

    private void InitDataReceiver(){

        if (getArguments() == null || getArguments().getString("year_id") == null){
            Toast.makeText(requireActivity(), "Please select year id", Toast.LENGTH_SHORT).show();
            return;
        }

        mViewModel.GetProjectList(getArguments().getString("year_id"), lsDfrom, lsDto).observe(getViewLifecycleOwner(), new Observer<List<EProjectMaster>>() {
            @Override
            public void onChanged(List<EProjectMaster> eProjectMasters) {

                if (eProjectMasters == null || eProjectMasters.size() < 1){
                    rcv_project_list.setVisibility(View.GONE);
                    layout_no_record.setVisibility(View.VISIBLE);
                    return;
                }
                rcv_project_list.setVisibility(View.VISIBLE);
                layout_no_record.setVisibility(View.GONE);

                loAdapter = new Adapter_Project_List(requireContext(), eProjectMasters, new Adapter_Project_List.OnSelectProject() {
                    @Override
                    public void OnSelect(EProjectMaster loProj) {

                        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

                        //show information of selected project
                        Fragment_Project loProject = new Fragment_Project();
                        Bundle loBundle = new Bundle();

                        loBundle.putString("lodge_id", getArguments().getString("lodge_id"));
                        loBundle.putString("project_cd", loProj.getSProjctCd());
                        loProject.setArguments(loBundle);

                        //add to child container, as bridge to initiate another fragment after call
                        fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, new Fragment_Child_Container().newInstance("project_entry", loProject));
                        fragmentTransaction.addToBackStack("project_entry");

                        fragmentTransaction.commit();

                    }
                });

                rcv_project_list.setAdapter(loAdapter);
                rcv_project_list.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));

            }
        });
    }

    private void FilterStatus(List<String> faStatus){

        if (loAdapter == null) return;

        loAdapter.GetFilter().filter(tie_search.getText() == null ? "" : tie_search.getText().toString());
        loAdapter.GetFilter().InitStatus(faStatus);
        loAdapter.notifyDataSetChanged();
    }

    private void FilterTypes(List<String> faTypes){

        if (loAdapter == null) return;

        loAdapter.GetFilter().filter(tie_search.getText() == null ? "" : tie_search.getText().toString());
        loAdapter.GetFilter().InitTypes(faTypes);
        loAdapter.notifyDataSetChanged();
    }

    private void InitListener(){

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //initialize pop up object, menu object holder
                PopupMenu loMenu = new PopupMenu(requireContext(), view);
                loMenu.getMenuInflater().inflate(R.menu.menu_filter_projects, loMenu.getMenu());
                loMenu.show();

                loMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.action_item_filter) {

                            MaterialDatePicker.Builder<Pair<Long, Long>> loBuilder = MaterialDatePicker.Builder.dateRangePicker();
                            loBuilder.setTitleText("Select Date Range");

                            MaterialDatePicker<Pair<Long, Long>> loPicker = loBuilder.build();
                            loPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                                @Override
                                public void onPositiveButtonClick(Pair<Long, Long> selection) {

                                    //set date range parameters for downloading history
                                    lsDfrom = mViewModel.GetFormatLongDate(selection.first);
                                    lsDto = mViewModel.GetFormatLongDate(selection.second);

                                    //filter list by date
                                    InitDataReceiver();
                                }
                            });
                            loPicker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");

                            return true;

                        } else if (menuItem.getItemId() == R.id.action_item_download) {

                            MaterialDatePicker.Builder<Pair<Long, Long>> loBuilder = MaterialDatePicker.Builder.dateRangePicker();
                            loBuilder.setTitleText("Select Date Range");

                            MaterialDatePicker<Pair<Long, Long>> loPicker = loBuilder.build();
                            loPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                                @Override
                                public void onPositiveButtonClick(Pair<Long, Long> selection) {

                                    //set date range parameters for downloading history
                                    lsDfrom = mViewModel.GetFormatLongDate(selection.first);
                                    lsDto = mViewModel.GetFormatLongDate(selection.second);

                                    poMessage.ShowMessage(2, "Do you want download list of fund entries within " + lsDfrom + " to " + lsDto + " ?", "No", "Yes", new Message_Dialog.OnDialogClick() {
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
                                }
                            });
                            loPicker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");
                        } else if (menuItem.getItemId() == R.id.action_item_all_status) {
                            FilterStatus(List.of("0", "1", "2", "3", "4"));
                        } else if (menuItem.getItemId() == R.id.action_item_plan) {
                            FilterStatus(List.of("0"));
                        } else if (menuItem.getItemId() == R.id.action_item_on_going) {
                            FilterStatus(List.of("1"));
                        } else if (menuItem.getItemId() == R.id.action_item_on_hold) {
                            FilterStatus(List.of("2"));
                        } else if (menuItem.getItemId() == R.id.action_item_complete) {
                            FilterStatus(List.of("3"));
                        } else if (menuItem.getItemId() == R.id.action_item_cancelled) {
                            FilterStatus(List.of("4"));
                        }

                        else if (menuItem.getItemId() == R.id.action_item_all_type) {
                            FilterTypes(List.of("0", "1", "2", "3", "4", "5"));
                        } else if (menuItem.getItemId() == R.id.action_item_brick) {
                            FilterTypes(List.of("0"));
                        } else if (menuItem.getItemId() == R.id.action_item_stone) {
                            FilterTypes(List.of("1"));
                        } else if (menuItem.getItemId() == R.id.action_item_concrete) {
                            FilterTypes(List.of("2"));
                        } else if (menuItem.getItemId() == R.id.action_item_glass) {
                            FilterTypes(List.of("3"));
                        } else if (menuItem.getItemId() == R.id.action_item_adobe) {
                            FilterTypes(List.of("4"));
                        } else if (menuItem.getItemId() == R.id.action_item_reinforce) {
                            FilterTypes(List.of("5"));
                        }

                        return true;
                    }
                });
            }
        });
    }
}