package org.gag.appdriver.Utilities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.Adapter_Attachment_List;
import org.gag.appdriver.App.Core.GestureListener;
import org.gag.appdriver.App.Interface.OnSwipeListener;
import org.gag.appdriver.App.Models.Attachments;
import org.gag.appdriver.App.ViewModels.VM_Attachments;
import org.gag.appdriver.R;
import org.gag.appdriver.Room.Entities.EDisbursementAttachment;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Fragment_Attachment_List extends Fragment implements OnSwipeListener {

    private VM_Attachments mViewModel;
    private LoadDialog poLoad;
    private Message_Dialog poMessage;

    private String lsStackIDxx, lsSource, lsDfrom, lsDto;
    private Attachments loSelectAttachment;
    private Adapter_Attachment_List loAdapter;

    private MaterialTextView mtv_title;
    private TextInputEditText tie_search;
    private ImageButton btn_filter;
    private ConstraintLayout layout_no_record;
    private RecyclerView rcv_attachment_list;

    private View layout_image_viewer;
    private ShapeableImageView img_file;
    private MaterialTextView mtv_filenme, mtv_filepth;
    private ImageButton btn_transaction;

    private GestureDetector gestureDetector;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__attachment__list, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(VM_Attachments.class);
        poLoad = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        gestureDetector = new GestureDetector(requireContext(), new GestureListener(this));

        poLoad.InitDialog();
        poMessage.InitDialog();

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackIDxx = entry.getName() == null ? "" : entry.getName();
        }

        lsSource = getArguments() == null ? "" : getArguments().getString("source");

        InitViews(view);
        InitDataReceiver();
        InitListener();
        DownloadAttachments();

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
        mtv_filenme = layout_image_viewer.findViewById(R.id.mtv_filenme);
        mtv_filepth = layout_image_viewer.findViewById(R.id.mtv_filepth);
        btn_transaction = layout_image_viewer.findViewById(R.id.btn_transaction);
    }

    private void ShowAttachment(boolean fIsshow){

        if (loSelectAttachment == null){
            Toast.makeText(requireActivity(), "No attachment selected", Toast.LENGTH_SHORT).show();
            return;
        }

        Animation slide = AnimationUtils.loadAnimation(requireActivity(), fIsshow ? org.gag.appdriver.R.anim.anim_slide_up : org.gag.appdriver.R.anim.anim_slide_down);
        layout_image_viewer.startAnimation(slide);
        layout_image_viewer.setVisibility(fIsshow ? View.VISIBLE : View.GONE);

        //show attachment info, if layout is showed
        if (layout_image_viewer.getVisibility() == View.VISIBLE){

            //initialize image to file
            File loImg = new File(loSelectAttachment.getSFilePth());

            mtv_filenme.setText(loSelectAttachment.getSFileNme());
            mtv_filepth.setText(loSelectAttachment.getSFilePth());

            //scale image if exist
            img_file.setScaleType(ImageView.ScaleType.CENTER_CROP);

            //display button icons based on status
            if (loSelectAttachment.getCRecdStat().equalsIgnoreCase("0")){
                btn_transaction.setImageResource(R.drawable.baseline_download);
            }else {
                btn_transaction.setImageResource(R.drawable.baseline_confirm_receipt);
            }

            // Load from File
            Glide.with(requireActivity())
                    .load(loImg)
                    .placeholder(R.drawable.no_image_avaialbel)
                    .error(R.drawable.no_image_avaialbel)
                    .skipMemoryCache(true)   // disable memory cache
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // disable disk cache
                    .into(img_file);

        }
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

                        ShowAttachment(false);
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
                ShowAttachment(true);
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

                    //if date is not filtered, then load all list
                    if ((lsDfrom == null || lsDfrom.isEmpty()) || (lsDto == null || lsDto.isEmpty())){

                        laAttachments.add(new Attachments(
                                loDV.getSDocTitle(),
                                loDV.getDTransact(),
                                loDV.getSFileName(),
                                loDV.getSFinFleNm(),
                                new File(loDV.getSFinFleNm()).exists() ? "1" : "0"
                        ));

                    }else {

                        //add list if file date is in range from filtered date
                        if (!mViewModel.IsDateCompared(lsDfrom, loDV.getDTransact()) || lsDfrom.equalsIgnoreCase(loDV.getDTransact()) ||
                                !mViewModel.IsDateCompared(lsDto, loDV.getDTransact()) || lsDto.equalsIgnoreCase(loDV.getDTransact())){

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
                SetAdaoter(laAttachments);
            }
        });
    }

    private void GenerateSOAReports(String fsIDxx){

        if (fsIDxx.equalsIgnoreCase("all")) {

            mViewModel.GenerateSoa(fsIDxx, new VM_Attachments.OnGenerate() {
                @Override
                public void OnLoad() {
                    poLoad.ShowDialog("Generating SOA report. Please wait . .");
                }

                @Override
                public void OnSuccess(String lsURL) {
                    poLoad.DismissDialog();
                    Toast.makeText(requireActivity(), "SOA Report successfully generated", Toast.LENGTH_SHORT).show();

                    SetAdaoter(List.of(new Attachments("SOA Report", mViewModel.GetCurrentDate(),  lsURL, lsURL, "1")));
                }

                @Override
                public void OnFailed(String fsMessage) {
                    poLoad.DismissDialog();
                    Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void InitDataReceiver(){

        switch (lsStackIDxx){

            case "disbursement_attachments":
            case "disbursement":
                mtv_title.setText("DV Attachments");
                LoadDisbursements();
                break;
            case "generate_soa":
                mtv_title.setText("Statement of Accounts");
                GenerateSOAReports("all");
                break;
        }
    }

    private void InitListener(){

        layout_image_viewer.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

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

                loMenu.getMenu().findItem(R.id.action_item_filter_dv).setVisible(
                        lsStackIDxx.equalsIgnoreCase("disbursement") ||
                                lsStackIDxx.equalsIgnoreCase("disbursement_attachments")
                );

                //show only menus for generating report and downloading attachments
                loMenu.getMenu().findItem(R.id.action_by_generate).setVisible(lsStackIDxx.equalsIgnoreCase("generate_soa"));

                loMenu.show();

                loMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.action_item_filter_dv){

                            MaterialDatePicker.Builder<Pair<Long, Long>> loBuilder = MaterialDatePicker.Builder.dateRangePicker();
                            loBuilder.setTitleText("Select Date Range");

                            MaterialDatePicker<Pair<Long, Long>> loPicker = loBuilder.build();
                            loPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                                @Override
                                public void onPositiveButtonClick(Pair<Long, Long> selection) {

                                    //set date range parameters for downloading history
                                    lsDfrom = mViewModel.FormatLongDate(selection.first);
                                    lsDto = mViewModel.FormatLongDate(selection.second);

                                    //filter list by date
                                    InitDataReceiver();
                                }
                            });
                            loPicker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");

                            return true;

                        }else if (menuItem.getItemId() == R.id.action_item_download_dv){

                            MaterialDatePicker.Builder<Pair<Long, Long>> loBuilder = MaterialDatePicker.Builder.dateRangePicker();
                            loBuilder.setTitleText("Select Date Range");

                            MaterialDatePicker<Pair<Long, Long>> loPicker = loBuilder.build();
                            loPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                                @Override
                                public void onPositiveButtonClick(Pair<Long, Long> selection) {

                                    //set date range parameters for downloading history
                                    lsDfrom = mViewModel.FormatLongDate(selection.first);
                                    lsDto = mViewModel.FormatLongDate(selection.second);

                                    poMessage.ShowMessage(2,  "Do you want download list of fund entries within " + lsDfrom + " to " + lsDto + " ?", "No", "Yes", new Message_Dialog.OnDialogClick() {
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
                            loPicker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");

                            return true;

                        }else if (menuItem.getItemId() == R.id.action_item_all_soa){

                            poMessage.ShowMessage(2,  "Do you want to generate soa report for the year?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                                @Override
                                public void OnPositive(@NotNull AlertDialog poDialog) {
                                    poDialog.dismiss();
                                }

                                @Override
                                public void OnNegative(@NotNull AlertDialog poDialog) {
                                    poDialog.dismiss();

                                    //download data from server with date to get
                                    GenerateSOAReports("all");
                                }
                            });
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