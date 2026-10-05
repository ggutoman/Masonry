package org.gag.appdriver.App.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.Adapter_Attachment_List;
import org.gag.appdriver.App.Core.GestureListener;
import org.gag.appdriver.App.Dialog.Dialog_Reports;
import org.gag.appdriver.App.Models.Attachments;
import org.gag.appdriver.App.ViewModels.VM_Attachments;
import org.gag.appdriver.R;
import org.gag.appdriver.Room.Entities.EDisbursementAttachment;
import org.gag.appdriver.Room.Entities.EReports;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Fragment_Attachment_List extends Fragment{

    private VM_Attachments mViewModel;
    private LoadDialog poLoad;
    private Message_Dialog poMessage;

    private String lsID, lsParams;

    private String lsStackIDxx, lsSource, lsDfrom, lsDto;
    private Attachments loSelectAttachment;
    private Adapter_Attachment_List loAdapter;

    private MaterialTextView mtv_title;
    private TextInputEditText tie_search;
    private ImageButton btn_filter;
    private ConstraintLayout layout_no_record;
    private RecyclerView rcv_attachment_list;

    private View layout_image_viewer;
    private MaterialCardView mcv_info, card_close;
    private PhotoView img_file;
    private MaterialTextView mtv_filenme, mtv_filepth;
    private ImageButton btn_transaction;

    private interface OnDateRange{
        void OnDateRangeSelected(String fsDfrom, String fsDto);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__attachment__list, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(VM_Attachments.class);
        poLoad = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        poLoad.InitDialog();
        poMessage.InitDialog();

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackIDxx = entry.getName() == null ? "" : entry.getName();
        }

        lsID = getArguments() == null ? "" : getArguments().getString("idxx");
        lsParams = getArguments() == null ? "" : getArguments().getString("params");
        lsSource = getArguments() == null ? "" : getArguments().getString("source");

        InitViews(view);
        InitDataReceiver();
        InitListener();

        return view;
    }

    private void InitViews(View view){

        mtv_title = view.findViewById(R.id.mtv_title);
        tie_search = view.findViewById(R.id.tie_search);
        btn_filter = view.findViewById(R.id.btn_filter);
        layout_no_record = view.findViewById(R.id.layout_no_record);
        rcv_attachment_list = view.findViewById(R.id.rcv_attachment_list);

        layout_image_viewer = view.findViewById(R.id.layout_image_viewer);
        img_file = layout_image_viewer.findViewById(R.id.img_file);
        mcv_info = layout_image_viewer.findViewById(org.gag.appdriver.R.id.mcv_info);
        card_close = layout_image_viewer.findViewById(org.gag.appdriver.R.id.card_close);
        mtv_filenme = layout_image_viewer.findViewById(R.id.mtv_filenme);
        mtv_filepth = layout_image_viewer.findViewById(R.id.mtv_filepth);
        btn_transaction = layout_image_viewer.findViewById(R.id.btn_transaction);
    }

    @SuppressLint("CheckResult")
    private void ShowAttachment( boolean fIsshow){

        try {

            if (loSelectAttachment == null){
                Toast.makeText(requireActivity(), "No attachment selected", Toast.LENGTH_SHORT).show();
                return;
            }

            File loFile = new File(loSelectAttachment.getSFilePth());

            Animation slide = AnimationUtils.loadAnimation(requireActivity(), fIsshow ? org.gag.appdriver.R.anim.anim_slide_up : org.gag.appdriver.R.anim.anim_slide_down);
            layout_image_viewer.startAnimation(slide);
            layout_image_viewer.setVisibility(fIsshow ? View.VISIBLE : View.GONE);

            //show attachment info, if layout is showed
            if (layout_image_viewer.getVisibility() == View.VISIBLE){

                //display information
                mtv_filenme.setText(loFile.getName());
                mtv_filepth.setText(loFile.getAbsolutePath());

                //scale image if exist
                img_file.setScaleType(ImageView.ScaleType.CENTER_CROP);

                //display button icons based on status
                if (!loFile.exists() || !loFile.isFile() || !loFile.canRead()){
                    btn_transaction.setImageResource(R.drawable.baseline_download);
                }else {
                    btn_transaction.setImageResource(R.drawable.baseline_confirm_receipt);
                }

                // Use FileProvider to get a safe content URI
                Uri uri = FileProvider.getUriForFile(
                        requireActivity(),
                        requireActivity().getPackageName() + ".provider",
                        loFile
                );

                //display image
                Glide.with(requireActivity())
                        .asBitmap() // force bitmap decoding
                        .load(uri)  // use safe URI instead of raw path
                        .apply(new RequestOptions()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE))
                        .error(R.drawable.no_image_avaialbel)
                        .into(img_file);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void ShowReport(File loFile){

        if (!loFile.exists()){
            Toast.makeText(requireActivity(), "File not found", Toast.LENGTH_SHORT).show();
            return;
        }else if (!loFile.isFile()){
            Toast.makeText(requireActivity(), "File is invalid", Toast.LENGTH_SHORT).show();
            return;
        }else if(!loFile.canRead()){
            Toast.makeText(requireActivity(), "File is not readable", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = FileProvider.getUriForFile(requireActivity(), requireActivity().getPackageName() + ".provider", loFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "text/csv");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(intent, "Open CSV"));

    }

    private void DownloadAttachments(){

        switch (lsStackIDxx){

            case "disbursement_attachments":
            case "disbursement":
                mViewModel.DownloadDVAttachment(lsSource, new VM_Attachments.OnDownload() {
                    @Override
                    public void OnLoad() {
                        poLoad.ShowDialog("Downloading Attachments. Please wait . .");
                    }

                    @Override
                    public void OnFinished(String fsMessage) {
                        poLoad.DismissDialog();
                        Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();

                        LoadDisbursements();
                    }
                });
                break;
        }
    }

    private void SetAdaoter(List<Attachments> faAttachments){

        loAdapter = new Adapter_Attachment_List(requireActivity(), faAttachments, new Adapter_Attachment_List.OnSelectItem() {
            @Override
            public void OnSelec(Attachments loItem) {

                loSelectAttachment = loItem;
                File loAttachment = new File(loItem.getSFilePth());

                if (loAttachment.getName().contains(".jpg") || loAttachment.getName().contains(".png")){
                    ShowAttachment(true);
                }else {
                    ShowReport(loAttachment);
                }
            }
        });

        rcv_attachment_list.setAdapter(loAdapter);
        rcv_attachment_list.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
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

                    //check first if the file path is existing and is a file readabke
                    File loFile = new File(loDV.getSFinFleNm());
                    if (loFile.exists() && loFile.isFile() && loFile.canRead()){

                        //if date is not filtered, then load all list
                        if ((lsDfrom == null || lsDfrom.isEmpty()) || (lsDto == null || lsDto.isEmpty())){

                            laAttachments.add(new Attachments(
                                    loDV.getSDocTitle(),
                                    mViewModel.FormatLongDate(loFile.lastModified()),
                                    loDV.getSFileName(),
                                    loDV.getSFinFleNm(),
                                    new File(loDV.getSFinFleNm()).exists() ? "1" : "0"
                            ));

                        }else {

                            //add list if file date is in range from filtered date
                            if (!mViewModel.IsDateCompared(lsDfrom, mViewModel.FormatLongDate(loFile.lastModified())) || lsDfrom.equalsIgnoreCase(mViewModel.FormatLongDate(loFile.lastModified())) ||
                                    !mViewModel.IsDateCompared(lsDto, mViewModel.FormatLongDate(loFile.lastModified())) || lsDto.equalsIgnoreCase(mViewModel.FormatLongDate(loFile.lastModified()))){

                                laAttachments.add(new Attachments(
                                        loDV.getSDocTitle(),
                                        mViewModel.FormatLongDate(loFile.lastModified()),
                                        loDV.getSFileName(),
                                        loDV.getSFinFleNm(),
                                        loDV.getCRecdStat()
                                ));
                            }

                        }

                    }
                }
                SetAdaoter(laAttachments);
            }
        });
    }

    private void LoadReports(){

        mViewModel.GetReportList(lsID, new VM_Attachments.OnGetReports() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Loading reports. Please wait . .");
            }

            @Override
            public void OnSuccess(List<File> faFiles) {
                poLoad.DismissDialog();

                List<Attachments> laFiles = new ArrayList<>();
                for (File loFile : faFiles){

                    //if file exist and file is readable, add to list
                    if (loFile.exists() && loFile.isFile() && loFile.canRead()){

                        //if date is not filtered, then load all list
                        if ((lsDfrom == null || lsDfrom.isEmpty()) || (lsDto == null || lsDto.isEmpty())){
                            laFiles.add(new Attachments(loFile.getName().toUpperCase(), mViewModel.FormatLongDate(loFile.lastModified()),  loFile.getName(), loFile.getAbsolutePath(), loFile.exists() ? "1" : "0"));

                        }else if (!mViewModel.IsDateCompared(lsDfrom, mViewModel.FormatLongDate(loFile.lastModified())) || lsDfrom.equalsIgnoreCase(mViewModel.FormatLongDate(loFile.lastModified())) ||
                                    !mViewModel.IsDateCompared(lsDto, mViewModel.FormatLongDate(loFile.lastModified())) || lsDto.equalsIgnoreCase(mViewModel.FormatLongDate(loFile.lastModified()))){

                            laFiles.add(new Attachments(loFile.getName().toUpperCase(), mViewModel.FormatLongDate(loFile.lastModified()),  loFile.getName(), loFile.getAbsolutePath(), loFile.exists() ? "1" : "0"));
                        }
                    }
                }

                //show list if not empty
                if (laFiles.size() < 1){
                    rcv_attachment_list.setVisibility(View.GONE);
                    layout_no_record.setVisibility(View.VISIBLE);
                    return;
                }
                rcv_attachment_list.setVisibility(View.VISIBLE);
                layout_no_record.setVisibility(View.GONE);

                SetAdaoter(laFiles);
            }

            @Override
            public void OnFailed(String fsMessage) {
                poLoad.DismissDialog();
                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void InitDataReceiver(){

        switch (lsStackIDxx){

            case "disbursement_attachments":
            case "disbursement":
                mtv_title.setText("DV Attachments");
                LoadDisbursements();
                break;
            case "generate_report":
                mtv_title.setText("Lodge Attachments");
                LoadReports();
                break;
        }
    }

    private void SelectDateRange(OnDateRange foCallback){

        MaterialDatePicker.Builder<Pair<Long, Long>> loBuilder = MaterialDatePicker.Builder.dateRangePicker();
        loBuilder.setTitleText("Select Date Range");

        MaterialDatePicker<Pair<Long, Long>> loPicker = loBuilder.build();
        loPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {

                //set date range parameters for downloading history
                lsDfrom = mViewModel.FormatLongDate(selection.first);
                lsDto = mViewModel.FormatLongDate(selection.second);

                foCallback.OnDateRangeSelected(lsDfrom, lsDto);
            }
        });
        loPicker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");

    }

    private void InitListener(){

        tie_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() < 1) return;
                loAdapter.GetFilter().filter(charSequence);
            }
        });

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //initialize pop up object, menu object holder
                PopupMenu loMenu = new PopupMenu(requireContext(), view);
                loMenu.getMenuInflater().inflate(R.menu.menu_attachment_filter, loMenu.getMenu());

                //show only menus for generating report and downloading attachments
                loMenu.getMenu().findItem(R.id.action_item_download_dv).setVisible(
                        lsStackIDxx.equalsIgnoreCase("disbursement") ||
                                lsStackIDxx.equalsIgnoreCase("disbursement_attachments")
                );

                //show only menus for generating report and downloading attachments
                loMenu.getMenu().findItem(R.id.action_by_generate).setVisible(lsStackIDxx.equalsIgnoreCase("generate_report"));
                loMenu.show();

                loMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        try {

                            if (menuItem.getItemId() == R.id.action_item_filter){

                                SelectDateRange(new OnDateRange() {
                                    @Override
                                    public void OnDateRangeSelected(String fsDfrom, String fsDto) {

                                        //filter list by date
                                        InitDataReceiver();
                                    }
                                });
                                return true;

                            }else if (menuItem.getItemId() == R.id.action_item_download_dv){

                                SelectDateRange(new OnDateRange() {
                                    @Override
                                    public void OnDateRangeSelected(String fsDfrom, String fsDto) {

                                        poMessage.ShowMessage(2,  "Do you want download list of dv attachments within " + fsDfrom + " to " + fsDto + " ?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                                            @Override
                                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                                poDialog.dismiss();
                                            }

                                            @Override
                                            public void OnNegative(@NotNull AlertDialog poDialog) {
                                                poDialog.dismiss();

                                                //download data from server with date to get
                                                DownloadAttachments();
                                            }
                                        });

                                    }
                                });

                                return true;

                            }else if (menuItem.getItemId() == R.id.action_by_generate){

                                Dialog_Reports loReport = new Dialog_Reports(requireActivity(), new JSONObject(lsParams), new Dialog_Reports.OnSubmitEntry() {
                                    @Override
                                    public void OnSubmit(JSONObject foParams) {

                                        mViewModel.GetReportInfo(lsID).observe(getViewLifecycleOwner(), new Observer<EReports>() {
                                            @Override
                                            public void onChanged(EReports eReports) {

                                                if (eReports == null) return;

                                                mViewModel.GenerateReport(eReports.getIdxx(), eReports.getXxTemplateDir(), foParams, new VM_Attachments.OnGenerate() {
                                                    @Override
                                                    public void OnLoad() {
                                                        poLoad.ShowDialog("Generating report. Please wait . .");
                                                    }

                                                    @Override
                                                    public void OnSuccess(String lsURL) {
                                                        poLoad.DismissDialog();
                                                        InitDataReceiver();
                                                    }

                                                    @Override
                                                    public void OnFailed(String fsMessage) {
                                                        poLoad.DismissDialog();
                                                        Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();

                                                        InitDataReceiver();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                                loReport.Show();

                                return true;
                            }

                        }catch (Exception ex){
                            ex.printStackTrace();
                            return false;
                        }

                        return false;
                    }
                });

            }
        });

        btn_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loSelectAttachment.getCRecdStat().equalsIgnoreCase("0")){
                    DownloadAttachments();
                }
            }
        });

        img_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mcv_info.setVisibility(mcv_info.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                card_close.setVisibility(card_close.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });

        card_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowAttachment(false);
            }
        });
    }
}