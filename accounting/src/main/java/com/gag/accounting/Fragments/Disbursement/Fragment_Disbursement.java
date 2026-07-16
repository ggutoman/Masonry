package com.gag.accounting.Fragments.Disbursement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gag.accounting.Adapter.Disbursement.Adapter_Disbursement_Attachments;
import com.gag.accounting.Adapter.Disbursement.Adapter_Disbursement_Sources;
import com.gag.accounting.Adapter.Disbursement.Adapter_Disbursement_Transactions;
import com.gag.accounting.Dialog.Dialog_DV_Attachment;
import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Disbursement;
import com.gag.accounting.ViewModel.VM_Receipts;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.LodgeAdapter;
import org.gag.appdriver.App.Core.GestureListener;
import org.gag.appdriver.App.Interface.OnSwipeListener;
import org.gag.appdriver.App.Models.DisbursementDetail;
import org.gag.appdriver.App.Models.LodgeFundMasterList;
import org.gag.appdriver.App.Models.LodgeInfo;
import org.gag.appdriver.Constants.DISBURSEMENT_CONSTANTS;
import org.gag.appdriver.Room.Entities.EDisbursement;
import org.gag.appdriver.Room.Entities.EDisbursementAttachment;
import org.gag.appdriver.Room.Entities.EProjectMaster;
import org.gag.appdriver.Utilities.Fragment_Attachment_List;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Fragment_Disbursement extends Fragment implements OnSwipeListener {

    private VM_Disbursement mViewModel;
    private LoadDialog poLoad;
    private Message_Dialog poMessage;
    private EDisbursement loDisbursement;
    private List<EDisbursementAttachment> laAttachments;

    private int lnSelectAttached = -1;

    private String lsStackIDxx, lsTransNox;
    private LodgeInfo loSelectLodge;
    private DISBURSEMENT_CONSTANTS loSelectType;
    private DisbursementDetail loSelectTransaction;
    private Adapter_Disbursement_Attachments loAdapterAttachment;
    private EDisbursementAttachment loSelectAttachment;
    private File loAttached;

    private View parent_view;
    private View view_image;
    private ShapeableImageView img_file;
    private MaterialTextView mtv_filenme, mtv_filepth;
    private ImageButton btn_transaction;

    private MaterialTextView mtv_status, mtv_status_attachment, mtv_fileloc, btn_download, btn_download_sources, btn_view_details, btn_save_attachment, btn_view_attachment;
    private TextInputLayout til_source_type, til_source_no, til_lodge;
    private TextInputEditText tie_transaction_no, tie_dtransact, tie_payee, tie_particulars, tie_voucher, tie_amount, tie_doctitle, tie_remarks;
    private MaterialAutoCompleteTextView auto_source_type, auto_source_no, auto_lodge, auto_attachment_transaction;
    private ConstraintLayout layout_tools;
    private MaterialButton btn_save;
    private ImageButton btn_attachment;

    private GestureDetector gestureDetector;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == Activity.RESULT_OK) {

            //get attached file and validate
            if (loAttached == null || !loAttached.exists()){
                Toast.makeText(requireActivity(), "File not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            //save file as dv attachment
            new Dialog_DV_Attachment(requireActivity(), loAttached, new Dialog_DV_Attachment.OnAttach() {
                @Override
                public void OnSuccess(String fsDocTitle, String fsRemarks) {

                    mViewModel.AttachFile("", loDisbursement.getSTransNox(), fsDocTitle, fsRemarks, loAttached, "0");
                }

                @Override
                public void OnFailed(String fsMessage) {
                    Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
                }
            }).Show();
        }
    });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__disbursement, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(VM_Disbursement.class);
        poLoad = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        gestureDetector = new GestureDetector(requireContext(), new GestureListener(this));

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackIDxx = entry.getName() == null ? "" : entry.getName();
        }

        poLoad.InitDialog();
        poMessage.InitDialog();

        lsTransNox = getArguments() == null || getArguments().getString("transaction_id") == null ? "" : getArguments().getString("transaction_id");

        if (!lsTransNox.isEmpty()) DownloadDisbursement();

        InitViews(view);
        InitDataReceiver();
        InitListener();

        return view;
    }

    private void InitViews(View view){

        view_image = view.findViewById(R.id.view_image);
        img_file = view_image.findViewById(org.gag.appdriver.R.id.img_file);
        mtv_filenme = view_image.findViewById(org.gag.appdriver.R.id.mtv_filenme);
        mtv_filepth = view_image.findViewById(org.gag.appdriver.R.id.mtv_filepth);
        btn_transaction = view_image.findViewById(org.gag.appdriver.R.id.btn_transaction);

        mtv_status = view.findViewById(R.id.mtv_status);
        mtv_status_attachment = view.findViewById(R.id.mtv_status_attachment);
        btn_download = view.findViewById(R.id.btn_download);
        btn_download_sources= view.findViewById(R.id.btn_download_source);

        til_lodge = view.findViewById(R.id.til_lodge);
        til_source_type = view.findViewById(R.id.til_source_type);
        til_source_no = view.findViewById(R.id.til_source_no);

        tie_transaction_no = view.findViewById(R.id.tie_transaction_no);
        tie_dtransact = view.findViewById(R.id.tie_dtransact);
        tie_payee = view.findViewById(R.id.tie_payee);
        tie_particulars = view.findViewById(R.id.tie_particulars);
        tie_voucher = view.findViewById(R.id.tie_voucher);
        tie_amount = view.findViewById(R.id.tie_amount);

        auto_lodge = view.findViewById(R.id.auto_lodge);
        auto_source_type = view.findViewById(R.id.auto_source_type);
        auto_source_no = view.findViewById(R.id.auto_source_no);
        auto_attachment_transaction= view.findViewById(R.id.auto_attachment_transaction);
        tie_doctitle = view.findViewById(R.id.tie_doctitle);
        mtv_fileloc = view.findViewById(R.id.mtv_fileloc);
        tie_remarks = view.findViewById(R.id.tie_remarks);

        layout_tools = view.findViewById(R.id.layout_tools);

        btn_save_attachment = view.findViewById(R.id.btn_save_attachment);
        btn_view_attachment = view.findViewById(R.id.btn_view_attachment);
        btn_view_details = view.findViewById(R.id.btn_view_details);
        btn_attachment = view.findViewById(R.id.btn_attachment);
        btn_save = view.findViewById(R.id.btn_save);
    }

    private void ClearFields(List<View> fields, boolean clearAdapter) {

        for (View view : fields) {

            if (view instanceof TextInputEditText){
                ((TextInputEditText) view).setText("");
            }else if (view instanceof MaterialAutoCompleteTextView){
                ((MaterialAutoCompleteTextView) view).setText("");
                if (clearAdapter) ((MaterialAutoCompleteTextView) view).setAdapter(null);
            }else if (view instanceof CheckBox){
                ((CheckBox) view).setChecked(false);
            }else if (view instanceof MaterialButton){
                view.setVisibility(View.GONE);
            }
        }
    }

    private void AllowFields(List<View> fields, boolean isAllowed) {

        for (View view : fields) {

            view.setEnabled(isAllowed);
        }
    }

    private void InitDataReceiver(){

        auto_source_type.setAdapter(new Adapter_Disbursement_Sources(
                requireContext(),
                mViewModel.GetSources()
        ));

        mViewModel.ObserveLodgeList().observe(getViewLifecycleOwner(), new Observer<List<LodgeInfo>>() {
            @Override
            public void onChanged(List<LodgeInfo> lodgeInfos) {

                if (lodgeInfos == null) return;

                auto_lodge.setAdapter(new LodgeAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        lodgeInfos
                ));

                //initialize default selection of lodge
                if (loSelectLodge == null){

                    for (LodgeInfo loLodge : lodgeInfos){

                        if (loLodge.getSLodgeIDx().equalsIgnoreCase(mViewModel.GetLodgeID())){
                            loSelectLodge = loLodge;
                            auto_lodge.setText(loSelectLodge.getSLodgeNme(), false);
                            break;
                        }

                    }

                }
            }
        });

        mViewModel.GetDisbursement(lsTransNox).observe(getViewLifecycleOwner(), new Observer<EDisbursement>() {
            @Override
            public void onChanged(EDisbursement eDisbursement) {

                if (eDisbursement == null){

                    loDisbursement = new EDisbursement(
                            "",
                            mViewModel.GetCurrentDate(),
                            "FND",
                            "",
                            "",
                            "",
                            "",
                            "0.00",
                            "0",
                            "",
                            "",
                            "",
                            "",
                            mViewModel.GetUserID(),
                            mViewModel.GetCurrentDate()
                    );

                    layout_tools.setVisibility(View.GONE);
                }else {
                    loDisbursement = eDisbursement;

                    layout_tools.setVisibility(View.VISIBLE);
                }

                ClearFields(List.of(tie_transaction_no, tie_dtransact, tie_payee, tie_particulars, tie_voucher, auto_source_no, tie_amount, tie_doctitle, tie_remarks, mtv_status_attachment, mtv_fileloc), true);

                tie_transaction_no.setText(loDisbursement.getSTransNox());
                tie_dtransact.setText(loDisbursement.getDTransact());
                tie_payee.setText(loDisbursement.getSPayeeNme());
                tie_particulars.setText(loDisbursement.getSParticlr());
                tie_voucher.setText(loDisbursement.getSVouchrNo());
                tie_amount.setText(loDisbursement.getNAmountxx());

                switch (loDisbursement.getCTranStat()){

                    case "0":
                        mtv_status.setText("Open");
                        break;
                    case "1":
                        mtv_status.setText("Confirmed");
                        break;
                    case "2":
                        mtv_status.setText("Released");
                        break;
                    case "3":
                        mtv_status.setText("Cancelled");
                        break;
                    case "4":
                        mtv_status.setText("Void");
                        break;
                    case "5":
                        mtv_status.setText("Completed");
                        break;
                }
                til_lodge.setVisibility(loDisbursement.getCTranStat().equalsIgnoreCase("0") ? View.VISIBLE : View.GONE);
                btn_save.setVisibility(loDisbursement.getCTranStat().equalsIgnoreCase("0") ? View.VISIBLE : View.GONE);
                btn_download_sources.setVisibility(loDisbursement.getCTranStat().equalsIgnoreCase("0") ? View.VISIBLE : View.GONE);

                AllowFields(List.of(tie_payee, tie_particulars, tie_voucher, til_source_type, til_source_no, tie_amount, tie_doctitle, tie_remarks), loDisbursement.getCTranStat().equals("0"));

                //load source type on selection
                for (DISBURSEMENT_CONSTANTS loSrcTypes : mViewModel.GetSources()){

                    if (loDisbursement.getSSourceCD().equalsIgnoreCase(loSrcTypes.getFsID())){
                        loSelectType = loSrcTypes;
                        auto_source_type.setText(loSrcTypes.getFsDescript());

                        if (loSelectLodge == null || loSelectLodge.getSLodgeIDx().isEmpty()) break;

                        if (loDisbursement.getSSourceCD().equalsIgnoreCase("FND")){
                            DownloadLodgeFunds();
                        }else if (loDisbursement.getSSourceCD().equalsIgnoreCase("PROJ")){
                            DownloadProjects();
                        }

                        mViewModel.SelectType(loDisbursement.getSSourceCD());
                        break;
                    }

                }

                //load saved attachments
                mViewModel.GetAttachments(loDisbursement.getSTransNox()).observe(getViewLifecycleOwner(), new Observer<List<EDisbursementAttachment>>() {
                    @Override
                    public void onChanged(List<EDisbursementAttachment> eDisbursementAttachments) {

                        if (eDisbursementAttachments == null) return;

                        //clear adapter
                        ClearFields(List.of(auto_attachment_transaction, mtv_fileloc, mtv_status_attachment), true);

                        //clear list of attachment first
                        mViewModel.ClearAttachments();

                        //reload attachments
                        for (EDisbursementAttachment loAttachmennt : eDisbursementAttachments){
                            mViewModel.AttachFile(loAttachmennt.getSTransNox(), loDisbursement.getSTransNox(), loAttachmennt.getSDocTitle(), loAttachmennt.getSRemarksx(), new File(loAttachmennt.getSFinFleNm()), loAttachmennt.getCRecdStat());
                        }
                    }
                });
            }
        });

        mViewModel.ObserveSelectType().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {

                switch (s){

                    case "FND":

                        mViewModel.GetLodgeFunds(loSelectLodge == null ? "" : loSelectLodge.getSLodgeIDx()).observe(getViewLifecycleOwner(), new Observer<List<LodgeFundMasterList>>() {
                            @Override
                            public void onChanged(List<LodgeFundMasterList> lodgeFundMasterLists) {

                                if (lodgeFundMasterLists == null){
                                    return;
                                }

                                List<DisbursementDetail> laDetails = new ArrayList<>();
                                for (LodgeFundMasterList loFund : lodgeFundMasterLists){

                                    DisbursementDetail loDisb = new DisbursementDetail(
                                            "FND",
                                            loFund.getSLodgeNme(),
                                            loFund.getSLodgeIDx(),
                                            loFund.getNBalancex()
                                    );

                                    //add to list
                                    laDetails.add(loDisb);

                                    //select transaction if matched the entry source no
                                    if (loFund.getSLodgeIDx().equalsIgnoreCase(loDisbursement.getSSourceNo())){
                                        loSelectTransaction = loDisb;

                                        auto_source_no.setText(loSelectTransaction.getSSourceNo(), false);
                                    }
                                }

                                auto_source_no.setAdapter(
                                        new Adapter_Disbursement_Transactions(
                                                requireContext(),
                                                R.layout.list_disbursement_details,
                                                laDetails
                                        )
                                );
                            }
                        });
                        break;
                    case "PROJ":

                        mViewModel.GetLodgeProjects(loSelectLodge == null ? "" : loSelectLodge.getSLodgeIDx()).observe(getViewLifecycleOwner(), new Observer<List<EProjectMaster>>() {
                            @Override
                            public void onChanged(List<EProjectMaster> eProjectMasters) {

                                if (eProjectMasters == null){
                                    Toast.makeText(requireActivity(), "No project source", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                List<DisbursementDetail> laDetails = new ArrayList<>();
                                for (EProjectMaster loProject : eProjectMasters){

                                    DisbursementDetail loDisb = new DisbursementDetail(
                                            "PROJ",
                                            loProject.getSProjctNm(),
                                            loProject.getSProjctCd(),
                                            loProject.getNCollTotl()
                                    );

                                    //add to list
                                    laDetails.add(loDisb);

                                    //select transaction if matched the entry source no
                                    if (loProject.getSProjctCd().equalsIgnoreCase(loDisbursement.getSSourceNo())){
                                        loSelectTransaction = loDisb;

                                        auto_source_no.setText(loSelectTransaction.getSSourceNo(), false);
                                    }
                                }

                                auto_source_no.setAdapter(
                                        new Adapter_Disbursement_Transactions(
                                                requireContext(),
                                                R.layout.list_disbursement_details,
                                                laDetails
                                        )
                                );
                            }
                        });
                        break;
                }
            }
        });

        mViewModel.ObserveAttachedFile().observe(getViewLifecycleOwner(), new Observer<List<EDisbursementAttachment>>() {
            @Override
            public void onChanged(List<EDisbursementAttachment> eDisbursementAttachments) {

                if (eDisbursementAttachments == null) return;

                //initialize attachment
                laAttachments = eDisbursementAttachments;

                //reset object that holds the attached file
                loAttached = null;
                loSelectAttachment = null;

                ClearFields(List.of(tie_doctitle, tie_remarks, auto_attachment_transaction, mtv_fileloc, mtv_status_attachment), false);

                //reload adapter
                loAdapterAttachment = new Adapter_Disbursement_Attachments(
                        requireActivity(),
                        eDisbursementAttachments
                );
                auto_attachment_transaction.setAdapter(loAdapterAttachment);
            }
        });
    }

    private void DownloadDisbursement(){

        mViewModel.DownloadDisbursementInfo(lsTransNox, new VM_Receipts.OnDownload() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Downloading disbursement. Please wait . .");
            }

            @Override
            public void OnFinished(String fsMessage) {
                poLoad.DismissDialog();
                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DownloadProjects(){

        mViewModel.DownloadProjects(loSelectLodge == null ? "" : loSelectLodge.getSLodgeIDx(), new VM_Receipts.OnDownload() {
            @Override
            public void OnLoad() {
                Toast.makeText(requireActivity(), "Downloading projects. Please wait . .", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnFinished(String fsMessage) {
                poLoad.DismissDialog();
                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();

                mViewModel.SelectType(loSelectType.getFsID());
            }
        });
    }

    private void DownloadLodgeFunds(){

        mViewModel.DownloadLodgeFunds(loSelectLodge == null ? "" : loSelectLodge.getSLodgeIDx(), new VM_Receipts.OnDownload() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Downloading lodge funds. Please wait . .");
            }

            @Override
            public void OnFinished(String fsMessage) {
                poLoad.DismissDialog();
                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();

                mViewModel.SelectType(loSelectType.getFsID());
            }
        });
    }

    private void DownloadSources(){

        if (loSelectType == null){
            Toast.makeText(requireActivity(), "Please select source type", Toast.LENGTH_SHORT).show();
            return;
        } else if (loSelectLodge == null) {
            Toast.makeText(requireActivity(), "Please select a lodge first", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (loSelectType.getFsID()){

            case "FND":
                DownloadLodgeFunds();
                break;
            case "PROJ":
                DownloadProjects();
                break;

        }
    }

    private void ShowAttachment(boolean fIsshow) {

        Animation slide = AnimationUtils.loadAnimation(requireActivity(), fIsshow ? org.gag.appdriver.R.anim.anim_slide_up : org.gag.appdriver.R.anim.anim_slide_down);
        view_image.startAnimation(slide);
        view_image.setVisibility(fIsshow ? View.VISIBLE : View.GONE);

        //show attachment info, if layout is showed
        if (view_image.getVisibility() == View.VISIBLE){

            //initialize image to file
            File loImg = new File(loSelectAttachment.getSFinFleNm());

            mtv_filenme.setText(loSelectAttachment.getSFileName());
            mtv_filepth.setText(loSelectAttachment.getSFinFleNm());

            //scale image if exist
            img_file.setScaleType(ImageView.ScaleType.CENTER_CROP);

            //display button icons based on status
            if (loSelectAttachment.getCRecdStat().equalsIgnoreCase("0")){
                btn_transaction.setImageResource(org.gag.appdriver.R.drawable.upload_cloud_svgrepo_com);
            }else {
                btn_transaction.setImageResource(!loImg.exists() ? org.gag.appdriver.R.drawable.baseline_download : org.gag.appdriver.R.drawable.baseline_confirm_receipt);
            }

            // Load from File
            Glide.with(requireActivity())
                    .load(loImg)
                    .placeholder(org.gag.appdriver.R.drawable.no_image_avaialbel)
                    .error(org.gag.appdriver.R.drawable.no_image_avaialbel)
                    .skipMemoryCache(true)   // disable memory cache
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // disable disk cache
                    .into(img_file);

        }
    }

    private void UploadAttachment(){

        mViewModel.SaveAttachment(loSelectAttachment, new VM_Disbursement.OnTransaction() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Uploading attachment. Please wait . .");
            }

            @Override
            public void OnSuccess() {
                poLoad.DismissDialog();
                Toast.makeText(requireActivity(), "File uploaded successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnFailed(String fsMessage) {
                poLoad.DismissDialog();

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

    private void DownloadAttachment(){

        mViewModel.DownloadAttachment(loSelectAttachment.getSTransNox(), new VM_Disbursement.OnTransaction() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Downloading attachment. Please wait . .");
            }

            @Override
            public void OnSuccess() {
                poLoad.DismissDialog();
                Toast.makeText(requireActivity(), "File downloaded successfully", Toast.LENGTH_SHORT).show();

                //close attachment
                ShowAttachment(false);
            }

            @Override
            public void OnFailed(String fsMessage) {
                poLoad.DismissDialog();

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

    private void InitListener(){

        auto_lodge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (auto_lodge.getAdapter() == null) return;
                loSelectLodge = (LodgeInfo) auto_lodge.getAdapter().getItem(i);

                auto_lodge.setText(loSelectLodge.getSLodgeNme(), false);
            }
        });

        auto_source_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (auto_source_type.getAdapter() == null) return;
                loSelectType = (DISBURSEMENT_CONSTANTS) auto_source_type.getAdapter().getItem(i);

                auto_source_type.setText(loSelectType.getFsDescript(), false);

                //clear voucher fields
                ClearFields(List.of(auto_source_no, tie_amount), true);

                //re initialize transaction list
                mViewModel.SelectType(loSelectType.getFsID());
            }
        });

        auto_source_no.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (auto_source_no.getAdapter() == null) return;
                loSelectTransaction = (DisbursementDetail) auto_source_no.getAdapter().getItem(i);

                auto_source_no.setText(loSelectTransaction.getSSourceNme(), false);
                tie_amount.setText(loSelectTransaction.getNDisbAmt());
            }
        });

        auto_attachment_transaction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (loAdapterAttachment == null) return;

                //load selected attachment
                loSelectAttachment = (EDisbursementAttachment) auto_attachment_transaction.getAdapter().getItem(i);
                lnSelectAttached = loAdapterAttachment.laAttachments.indexOf(loSelectAttachment);

                auto_attachment_transaction.setText(loSelectAttachment.getSFileName(), false);
                tie_doctitle.setText(loSelectAttachment.getSDocTitle());
                mtv_fileloc.setText(loSelectAttachment.getSFinFleNm());
                tie_remarks.setText(loSelectAttachment.getSRemarksx());
                mtv_status_attachment.setText(loSelectAttachment.getCRecdStat().equalsIgnoreCase("0") ? "Pending" : "Uploaded");
            }
        });

        tie_doctitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (loSelectAttachment == null || charSequence.length() < 1){
                    btn_save_attachment.setVisibility(View.GONE);
                }else if (loSelectAttachment.getSDocTitle().equalsIgnoreCase(charSequence.toString())){
                    btn_save_attachment.setVisibility(View.GONE);
                }else {
                    btn_save_attachment.setVisibility(View.VISIBLE);
                }
            }
        });

        tie_remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (loSelectAttachment == null){
                    btn_save_attachment.setVisibility(View.GONE);
                }else if (loSelectAttachment.getSRemarksx().equalsIgnoreCase(charSequence.toString())){
                    btn_save_attachment.setVisibility(View.GONE);
                }else {
                    btn_save_attachment.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_download_sources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DownloadSources();
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DownloadDisbursement();
            }
        });

        btn_view_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loSelectAttachment == null){
                    Toast.makeText(requireActivity(), "Please select attachment", Toast.LENGTH_SHORT).show();
                    return;
                }

                ShowAttachment(true);
            }
        });

        view_image.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        btn_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loSelectAttachment == null){
                    Toast.makeText(requireActivity(), "Please select attachment", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (loSelectAttachment.getCRecdStat().equalsIgnoreCase("0")){
                    UploadAttachment();
                }else if (loSelectAttachment.getCRecdStat().equalsIgnoreCase("1") && !(new File(loSelectAttachment.getSFinFleNm()).exists()) ){
                    DownloadAttachment();
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                poMessage.ShowMessage(2, "Is your information complete?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        if (tie_payee.getText() == null || tie_payee.getText().toString().isEmpty()){
                            Toast.makeText(requireActivity(), "Please enter payee name", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (tie_particulars.getText() == null || tie_particulars.getText().toString().isEmpty()){
                            Toast.makeText(requireActivity(), "Please enter particulars", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (tie_voucher.getText() == null || tie_voucher.getText().toString().isEmpty()){
                            Toast.makeText(requireActivity(), "Please enter voucher", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (loDisbursement == null){
                            Toast.makeText(requireActivity(), "Disbursement is not initialized properly", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (loSelectType == null){
                            Toast.makeText(requireActivity(), "Please select source type", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (loSelectTransaction == null){
                            Toast.makeText(requireActivity(), "Please select source transaction", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //initialize voucher information
                        loDisbursement.setSPayeeNme(tie_payee.getText() == null ? "" : tie_payee.getText().toString());
                        loDisbursement.setSParticlr(tie_particulars.getText() == null ? "" : tie_particulars.getText().toString());
                        loDisbursement.setSVouchrNo(tie_voucher.getText() == null ? "" : tie_voucher.getText().toString());
                        loDisbursement.setSSourceCD(loSelectType.getFsID());
                        loDisbursement.setSSourceNo(loSelectTransaction.getSSourceNo());
                        loDisbursement.setNAmountxx(tie_amount.getText() == null ? "" : tie_amount.getText().toString());

                        mViewModel.SaveDisbursement(loDisbursement, laAttachments, new VM_Disbursement.OnTransaction() {
                            @Override
                            public void OnLoad() {
                                poLoad.ShowDialog("Saving disbursement. Please wait . .");
                            }

                            @Override
                            public void OnSuccess() {
                                poLoad.DismissDialog();
                                Toast.makeText(requireActivity(), "Disbursement saved successfully", Toast.LENGTH_SHORT).show();

                                requireActivity().getSupportFragmentManager().popBackStack(lsStackIDxx, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }

                            @Override
                            public void OnFailed(String fsMessage) {
                                poLoad.DismissDialog();

                                poMessage.ShowMessage(1, fsMessage, "Okay", "", new Message_Dialog.OnDialogClick() {
                                    @Override
                                    public void OnPositive(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();
                                    }

                                    @Override
                                    public void OnNegative(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        btn_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mViewModel.GenerateFileImage(new VM_Receipts.OnShareFile() {
                    @Override
                    public void OnLoad() {
                        poLoad.ShowDialog("Saving image. Please wait. .");
                    }

                    @Override
                    public void OnSuccess(File file) {
                        poLoad.DismissDialog();

                        //create file provider of captured image to save
                        Toast.makeText(requireActivity(), "Successfully generated file", Toast.LENGTH_SHORT).show();
                        Uri photoUri = FileProvider.getUriForFile(
                                requireContext(),
                                requireContext().getPackageName() + ".provider",
                                file
                        );
                        loAttached = file;

                        //launch camera intent to save as attachement
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                        cameraLauncher.launch(cameraIntent);

                    }

                    @Override
                    public void OnFailed(String fsMessage) {
                        poLoad.DismissDialog();
                        Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btn_save_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loSelectAttachment == null || lnSelectAttached < 0){
                    Toast.makeText(requireActivity(), "Please select an attachment", Toast.LENGTH_SHORT).show();
                    return;
                }
                mViewModel.ReplaceAttachment(lnSelectAttached, tie_doctitle.getText() == null ? "" : tie_doctitle.getText().toString(), tie_remarks.getText() == null ? "" : tie_remarks.getText().toString());
            }
        });

        btn_view_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction loTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                Fragment_Attachment_List loFragment = new Fragment_Attachment_List();
                Bundle loBundle = new Bundle();

                loBundle.putString("source", loDisbursement.getSTransNox());
                loFragment.setArguments(loBundle);

                loTransaction.replace(org.gag.appdriver.R.id.frame_child, loFragment);
                loTransaction.addToBackStack("disbursement");
                loTransaction.commit();
            }
        });
    }

    @Override
    public void OnSwipeUp() {}

    @Override
    public void OnSwipeDown() {
        ShowAttachment(false);
    }

    @Override
    public void OnSwipeLeft() {}

    @Override
    public void OnSwipeRight() {}
}