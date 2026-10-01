package org.gag.appdriver.App.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.Adapter_Reports_List;
import org.gag.appdriver.App.ViewModels.VM_Attachments;
import org.gag.appdriver.R;
import org.gag.appdriver.Room.Entities.EReports;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class Fragment_Report_List extends Fragment {

    private VM_Attachments mViewmodel;
    private LoadDialog poLoad;
    private Message_Dialog poMessage;

    private String lsStackIDxx;

    private ConstraintLayout layout_no_record;
    private MaterialTextView mtv_title, mtv_no_record;
    private TextInputEditText tie_search;
    private ImageButton btn_filter;
    private RecyclerView rcv_report_list;

    private Adapter_Reports_List loAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.fragment__attachment__list, container, false);

        mViewmodel = new ViewModelProvider(requireActivity()).get(VM_Attachments.class);
        poLoad = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        poLoad.InitDialog();
        poMessage.InitDialog();

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackIDxx = entry.getName() == null ? "" : entry.getName();
        }

        InitViews(view);
        InitDataReceiver();
        InitListener();

        return view;
    }

    public void InitViews(View view){

        layout_no_record = view.findViewById(R.id.layout_no_record);
        mtv_title = view.findViewById(R.id.mtv_title);
        mtv_no_record = view.findViewById(R.id.mtv_no_record);
        tie_search = view.findViewById(R.id.tie_search);
        btn_filter = view.findViewById(R.id.btn_filter);
        rcv_report_list = view.findViewById(R.id.rcv_attachment_list);

        mtv_title.setText("List of Reports");
        mtv_no_record.setText("No reports found");

        rcv_report_list.setPadding(25, 0, 0, 0);
    }

    public void InitDataReceiver(){

        mViewmodel.GetObjects().observe(getViewLifecycleOwner(), new Observer<List<EReports>>() {
            @Override
            public void onChanged(List<EReports> eReports) {

                if (eReports == null || eReports.size() < 1){
                    layout_no_record.setVisibility(View.VISIBLE);
                    rcv_report_list.setVisibility(View.GONE);
                    return;
                }
                layout_no_record.setVisibility(View.GONE);
                rcv_report_list.setVisibility(View.VISIBLE);

                loAdapter = new Adapter_Reports_List(requireActivity(), eReports, new Adapter_Reports_List.OnReport() {
                    @Override
                    public void OnSelect(EReports foReport) {

                        Fragment_Attachment_List loAttachments = new Fragment_Attachment_List();
                        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

                        Bundle loBundle = new Bundle();
                        loBundle.putString("idxx", foReport.getIdxx());
                        loBundle.putString("descriptxx", foReport.getXxsDescript());
                        loBundle.putString("params", foReport.getXxParams());
                        loAttachments.setArguments(loBundle);

                        //add to child container, as bridge to initiate another fragment after call
                        fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, loAttachments);
                        fragmentTransaction.addToBackStack(lsStackIDxx);
                        fragmentTransaction.commit();
                    }
                });
                rcv_report_list.setAdapter(loAdapter);
                rcv_report_list.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
            }
        });
    }

    public void DownloadList(){

        poMessage.ShowMessage(2, "Do you want to download report sources?", "No", "Yes", new Message_Dialog.OnDialogClick() {
            @Override
            public void OnPositive(@NotNull AlertDialog poDialog) {
                poDialog.dismiss();
            }

            @Override
            public void OnNegative(@NotNull AlertDialog poDialog) {
                poDialog.dismiss();

                mViewmodel.DownloadReports(new VM_Attachments.OnDownload() {
                    @Override
                    public void OnLoad() {
                        poLoad.ShowDialog("Downloading objects for report");
                    }

                    @Override
                    public void OnFinished(String fsMessage) {
                        poLoad.DismissDialog();
                        Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();

                        InitDataReceiver();
                    }
                });
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

                if (loAdapter == null) return;
                loAdapter.GetFilter().filter(charSequence);
            }
        });

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //initialize pop up object, menu object holder
                PopupMenu loMenu = new PopupMenu(requireContext(), view);
                loMenu.getMenuInflater().inflate(R.menu.menu_attachment_filter, loMenu.getMenu());

                loMenu.getMenu().findItem(R.id.action_item_filter).setVisible(false);
                loMenu.getMenu().findItem(R.id.action_by_generate).setVisible(false);
                
                loMenu.show();

                loMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.action_item_download_dv){
                            DownloadList();
                        }
                        return false;
                    }
                });
            }
        });
    }
}
