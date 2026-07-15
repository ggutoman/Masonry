package org.gag.appdriver.Utilities;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.Adapter_DV_Attachment_List;
import org.gag.appdriver.App.Models.Attachments;
import org.gag.appdriver.App.ViewModels.VM_Attachments;
import org.gag.appdriver.R;
import org.gag.appdriver.Room.Entities.EDisbursementAttachment;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Attachment_List extends Fragment {

    private VM_Attachments mViewModel;
    private LoadDialog poLoad;
    private String lsStackIDxx, lsSource, lsDfrom, lsDto;

    private MaterialTextView mtv_title;
    private ImageButton btn_filter;
    private ConstraintLayout layout_no_record;
    private RecyclerView rcv_attachment_list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__attachment__list, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(VM_Attachments.class);
        poLoad = new LoadDialog(requireActivity());

        poLoad.InitDialog();

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackIDxx = entry.getName() == null ? "" : entry.getName();
        }

        lsSource = getArguments() == null ? "" : getArguments().getString("source");

        InitViews(view);
        InitDataReceiver();
        DownloadAttachments();

        return view;
    }

    private void InitViews(View view){
        mtv_title = view.findViewById(R.id.mtv_title);
        btn_filter = view.findViewById(R.id.btn_filter);
        layout_no_record = view.findViewById(R.id.layout_no_record);
        rcv_attachment_list = view.findViewById(R.id.rcv_attachment_list);
    }

    private void DownloadAttachments(){

        switch (lsStackIDxx){

            case "disbursement":
                mViewModel.DownloadDisbursement(lsSource, new VM_Attachments.OnDownload() {
                    @Override
                    public void OnLoad() {
                        poLoad.ShowDialog("Downloading Attachments. Please wait . .");
                    }

                    @Override
                    public void OnFinished(String fsMessage) {
                        poLoad.DismissDialog();
                        Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    private void LoadDisbursements(){

        if (lsSource.isEmpty()){
            Toast.makeText(requireActivity(), "No transaction number initialized", Toast.LENGTH_SHORT).show();

            rcv_attachment_list.setVisibility(View.GONE);
            layout_no_record.setVisibility(View.VISIBLE);
            return;
        }

        mViewModel.GetDisbursements(lsSource).observe(getViewLifecycleOwner(), new Observer<List<EDisbursementAttachment>>() {
            @Override
            public void onChanged(List<EDisbursementAttachment> eDisbursementAttachments) {

                if (eDisbursementAttachments == null){
                    rcv_attachment_list.setVisibility(View.GONE);
                    layout_no_record.setVisibility(View.VISIBLE);
                    return;
                }
                rcv_attachment_list.setVisibility(View.VISIBLE);
                layout_no_record.setVisibility(View.GONE);

                List<Attachments> laAttachments = new ArrayList<>();
                for (EDisbursementAttachment loDV : eDisbursementAttachments){

                    //if date is not filtered, then load all list
                    if ((lsDfrom == null || lsDfrom.isEmpty()) || (lsDto == null || lsDto.isEmpty())){

                        laAttachments.add(new Attachments(
                                loDV.getSDocTitle(),
                                loDV.getDTransact(),
                                loDV.getSFileName(),
                                loDV.getSFinFleNm(),
                                loDV.getCRecdStat()
                        ));

                    }else {

                        //add list if file date is in range from filtered date
                        if ((mViewModel.FormatToDate(lsDfrom).after(mViewModel.FormatToDate(loDV.getDTransact())) || mViewModel.FormatToDate(lsDfrom).equals(mViewModel.FormatToDate(loDV.getDTransact()))) ||
                                mViewModel.FormatToDate(lsDto).before(mViewModel.FormatToDate(loDV.getDTransact())) || mViewModel.FormatToDate(lsDfrom).equals(mViewModel.FormatToDate(loDV.getDTransact()))){

                            laAttachments.add(new Attachments(
                                    loDV.getSDocTitle(),
                                    loDV.getDTransact(),
                                    loDV.getSFileName(),
                                    loDV.getSFinFleNm(),
                                    loDV.getCRecdStat()
                            ));
                        }

                    }
                }

                rcv_attachment_list.setAdapter(new Adapter_DV_Attachment_List(requireActivity(), laAttachments, new Adapter_DV_Attachment_List.OnSelectItem() {
                    @Override
                    public void OnSelec(Attachments loItem) {

                    }
                }));

            }
        });
    }

    private void InitDataReceiver(){

        switch (lsStackIDxx){

            case "disbursement":
                LoadDisbursements();
                break;
        }
    }
}