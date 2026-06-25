package com.gag.masonry.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gag.masonry.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.gag.appdriver.App.Adapters.LodgeAdapter;
import org.gag.appdriver.App.ViewModels.VM_Lodge;
import org.gag.appdriver.Room.Entities.ELodgeCalendar;
import org.gag.appdriver.Room.Entities.ELodgeInfo;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

public class Fragment_Lodge_Calendar_Entry extends Fragment {

    private VM_Lodge mviewModel;
    private LoadDialog poLoading;
    private Message_Dialog poMessage;
    private Calendar loCalendar;

    private String lsSelectCalendar;
    private ELodgeCalendar lodgeCalendar;
    private int lnYearPicked = 1;

    private MaterialAutoCompleteTextView auto_lodge;
    private TextInputLayout til_lodge;
    private TextInputEditText tie_year, tie_valid_until;
    private MaterialButton btn_save;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.fragment_lodge_calendar_entry, container, false);

        mviewModel = new ViewModelProvider(requireActivity()).get(VM_Lodge.class);
        poLoading = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());
        loCalendar = Calendar.getInstance();

        poLoading.InitDialog();
        poMessage.InitDialog();

        InitViews(view);
        InitDataReceiver();
        InitListener();

        return view;
    }

    private void InitViews(View view){

        til_lodge = view.findViewById(R.id.til_lodge);
        auto_lodge = view.findViewById(R.id.auto_lodge);
        tie_year = view.findViewById(R.id.tie_year);
        tie_valid_until = view.findViewById(R.id.tie_valid_until);
        btn_save = view.findViewById(R.id.btn_save);

    }

    private void InitDataReceiver(){

        if (getArguments() == null){
            lsSelectCalendar = "";

            til_lodge.setEnabled(true);
            btn_save.setText("Create Lodge Calendar");
        }else {

            if (getArguments().getString("year_id") == null || getArguments().getString("year_id").isEmpty()){

                poMessage.ShowMessage(1, "Could not load calendar info", "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull androidx.appcompat.app.AlertDialog poDialog) {
                        poDialog.dismiss();

                        requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .remove(Fragment_Lodge_Calendar_Entry.this)
                                .commit();
                    }

                    @Override
                    public void OnNegative(@NotNull androidx.appcompat.app.AlertDialog poDialog) {}
                });
                return;
            }
            til_lodge.setEnabled(false);
            btn_save.setText("Update Lodge Calendar");

            lsSelectCalendar = getArguments().getString("year_id") == null ? "" : getArguments().getString("year_id");
        }

        //Load the lodge calendar
        mviewModel.GetLodgeCalendarInfo(lsSelectCalendar).observe(getViewLifecycleOwner(), new Observer<ELodgeCalendar>() {
            @Override
            public void onChanged(ELodgeCalendar eLodgeCalendar) {

                if (eLodgeCalendar == null){

                    //initialize default values
                    lodgeCalendar = new ELodgeCalendar(
                            "",
                            "",
                            "",
                            mviewModel.GetCurrentDate(),
                            "1900-00-00",
                            mviewModel.GetCurrentDate(),
                            mviewModel.GetCurrentDateTime()
                    );
                }else {

                    //initialize object with loaded information
                    lodgeCalendar = eLodgeCalendar;
                    lnYearPicked = Integer.parseInt(eLodgeCalendar.getNYearxxxx());

                    tie_year.setText(lodgeCalendar.getNYearxxxx());
                    tie_valid_until.setText(lodgeCalendar.getDThruDate());
                }

                //get lodge list
                mviewModel.GetLodgeList().observe(getViewLifecycleOwner(), new Observer<List<ELodgeInfo>>() {
                    @Override
                    public void onChanged(List<ELodgeInfo> eLodgeInfos) {

                        if (eLodgeInfos == null) return;

                        auto_lodge.setAdapter(new LodgeAdapter(
                                requireActivity(),
                                android.R.layout.simple_spinner_dropdown_item,
                                eLodgeInfos)
                        );

                        eLodgeInfos.stream()
                                .filter(loCalendar -> loCalendar.getSLodgeIDx()
                                        .equalsIgnoreCase(lodgeCalendar.getSLodgeIDx()))
                                .findFirst()
                                .ifPresent(loCalendar ->{

                                    auto_lodge.setText(loCalendar.getSLodgeNme(), false);
                                });
                    }
                });
            }
        });

    }

    private void InitListener(){

        auto_lodge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ELodgeInfo poSelectedLodge = (ELodgeInfo) adapterView.getItemAtPosition(i);

                auto_lodge.setText(poSelectedLodge.getSLodgeNme(), false);
                lodgeCalendar.setSLodgeIDx(poSelectedLodge.getSLodgeIDx());
            }
        });

        tie_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NumberPicker yearPicker = new NumberPicker(requireContext());
                int currentYear = loCalendar.get(Calendar.YEAR);

                yearPicker.setMinValue(currentYear);
                yearPicker.setValue(currentYear);

                new AlertDialog.Builder(requireContext())
                        .setTitle("Select Year")
                        .setView(yearPicker)
                        .setPositiveButton("OK", (dialog, which) -> {

                            lnYearPicked = yearPicker.getValue();

                            tie_year.setText(String.valueOf(yearPicker.getValue()));
                            lodgeCalendar.setNYearxxxx(String.valueOf(yearPicker.getValue()));
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

            }
        });

        tie_valid_until.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog loValidPicker = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        String formatted = String.format("%04d-%02d-%02d", i, i1 + 1, i2);

                        tie_valid_until.setText(formatted);
                        lodgeCalendar.setDThruDate(formatted);
                    }
                }, lnYearPicked,
                        loCalendar.get(Calendar.MONTH),
                        loCalendar.get(Calendar.DAY_OF_MONTH)
                );

                // Restrict to chosen year
                Calendar minDate = Calendar.getInstance();
                minDate.set(lnYearPicked, Calendar.JANUARY, 1);

                Calendar maxDate = Calendar.getInstance();
                maxDate.set(lnYearPicked, Calendar.DECEMBER, 31);

                loValidPicker.getDatePicker().setMinDate(minDate.getTimeInMillis());
                loValidPicker.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

                loValidPicker.show();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                if (lodgeCalendar.getSLodgeIDx().isEmpty()){
                    Toast.makeText(requireActivity(), "Please select a lodge first", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (lodgeCalendar.getNYearxxxx().isEmpty()){
                    Toast.makeText(requireActivity(), "Please select preferred year", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (lodgeCalendar.getDThruDate().isEmpty()){
                    Toast.makeText(requireActivity(), "Please select validity date", Toast.LENGTH_SHORT).show();
                    return;
                }

                //if date behind the current, do not proceed
                if (mviewModel.IsDateCompared(lodgeCalendar.getDThruDate(), mviewModel.GetCurrentDate())){
                    Toast.makeText(requireActivity(), "Validity date should not be earlier than the current date", Toast.LENGTH_SHORT).show();
                    return;
                }

                poMessage.ShowMessage(2, "Is your lodge information complete?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(androidx.appcompat.app.@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(androidx.appcompat.app.@NotNull AlertDialog poMessage1) {
                        poMessage1.dismiss();

                        mviewModel.CreateLodgeCalendar(lodgeCalendar, new VM_Lodge.OnDownload() {

                            @Override
                            public void OnLoad() {
                                poLoading.ShowDialog("Creating lodge calendar. Please wait . .");
                            }

                            @Override
                            public void OnSuccess() {
                                poLoading.DismissDialog();

                                poMessage.InitDialog();
                                poMessage.ShowMessage(0, "Lodge calendar saved successfully", "Okay", "", new Message_Dialog.OnDialogClick() {
                                    @Override
                                    public void OnPositive(androidx.appcompat.app.@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();

                                        requireActivity()
                                                .getSupportFragmentManager()
                                                .popBackStack("lodge_calendar_list", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    }

                                    @Override
                                    public void OnNegative(androidx.appcompat.app.@NotNull AlertDialog poDialog) {}
                                });
                            }

                            @Override
                            public void OnError(String fsError) {
                                poLoading.DismissDialog();

                                poMessage.ShowMessage(0, fsError, "Okay", "", new Message_Dialog.OnDialogClick() {
                                    @Override
                                    public void OnPositive(androidx.appcompat.app.@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();
                                    }

                                    @Override
                                    public void OnNegative(androidx.appcompat.app.@NotNull AlertDialog poDialog) {}
                                });
                            }
                        });
                    }
                });
            }
        });

    }

}