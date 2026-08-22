package org.gag.appdriver.App.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import org.gag.appdriver.App.ViewModels.VM_Attachments;
import org.gag.appdriver.R;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;

import java.io.File;
import java.util.List;

public class Fragment_Report_List extends Fragment {

    private VM_Attachments mViewmodel;
    private LoadDialog poLoad;
    private Message_Dialog poMessage;

    private ConstraintLayout layout_no_record;
    private TextInputEditText tie_search;
    private ImageButton btn_filter;
    private RecyclerView rcv_report_list;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.fragment__report__list, container, false);

        mViewmodel = new ViewModelProvider(requireActivity()).get(VM_Attachments.class);
        poLoad = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        poLoad.InitDialog();
        poMessage.InitDialog();

        InitViews(view);
        DownloadList();

        return view;
    }

    public void InitViews(View view){

        layout_no_record = view.findViewById(R.id.layout_no_record);
        tie_search = view.findViewById(R.id.tie_search);
        btn_filter = view.findViewById(R.id.btn_filter);
        rcv_report_list = view.findViewById(R.id.rcv_report_list);
    }

    public void DownloadList(){

        mViewmodel.DownloadReports(new VM_Attachments.OnDownload() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Downloading objects for report");
            }

            @Override
            public void OnFinished(String fsMessage) {
                poLoad.DismissDialog();
                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
