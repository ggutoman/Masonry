package com.gag.useraccount.Fragments;

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

import com.gag.useraccount.R;
import com.gag.useraccount.ViewModel.VM_Account;
import com.gag.useraccount.ViewModel.VM_Member;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.gag.appdriver.App.Adapters.LodgeAdapter;
import org.gag.appdriver.Room.Entities.ELodgeCalendar;
import org.gag.appdriver.Room.Entities.ELodgeInfo;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

public class Fragment_Lodge extends Fragment {

    private VM_Member mviewModel;
    private ELodgeInfo poSelectedLodge;
    private LoadDialog poLoading;
    private Message_Dialog poMessage;
    private Calendar loCalendar;

    private int lnYearPicked = 1;

    private MaterialAutoCompleteTextView auto_lodge;
    private TextInputEditText tie_year, tie_valid_until;
    private MaterialButton btn_save;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.fragment_lodge, container, false);

        mviewModel = new ViewModelProvider(requireActivity()).get(VM_Member.class);
        poLoading = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());
        loCalendar = Calendar.getInstance();

        poLoading.InitDialog();
        poMessage.InitDialog();

        auto_lodge = view.findViewById(R.id.auto_lodge);
        tie_year = view.findViewById(R.id.tie_year);
        tie_valid_until = view.findViewById(R.id.tie_valid_until);
        btn_save = view.findViewById(R.id.btn_save);

        mviewModel.GetLodgeList().observe(getViewLifecycleOwner(), new Observer<List<ELodgeInfo>>() {
            @Override
            public void onChanged(List<ELodgeInfo> eLodgeInfos) {

                if (eLodgeInfos == null) return;

                auto_lodge.setAdapter(new LodgeAdapter(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        eLodgeInfos)
                );
            }
        });

        auto_lodge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                poSelectedLodge = (ELodgeInfo) adapterView.getItemAtPosition(i);

                auto_lodge.setText(poSelectedLodge.getSLodgeNme(), false);
            }
        });

        tie_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NumberPicker yearPicker = new NumberPicker(requireContext());
                int currentYear = loCalendar.get(Calendar.YEAR);

                yearPicker.setMinValue(currentYear);
                yearPicker.setMaxValue(currentYear + 10);

                yearPicker.setValue(currentYear);

                new AlertDialog.Builder(requireContext())
                        .setTitle("Select Year")
                        .setView(yearPicker)
                        .setPositiveButton("OK", (dialog, which) -> {
                            tie_year.setText(String.valueOf(yearPicker.getValue()));
                            lnYearPicked = yearPicker.getValue();
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
            public void onClick(View view) {

                if (poSelectedLodge == null){
                    Toast.makeText(requireActivity(), "Please select a lodge first", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (tie_year.getText() == null || tie_year.getText().toString().isEmpty()){
                    Toast.makeText(requireActivity(), "Please select preferred year", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (tie_valid_until.getText() == null || tie_valid_until.getText().toString().isEmpty()){
                    Toast.makeText(requireActivity(), "Please select validity date", Toast.LENGTH_SHORT).show();
                    return;
                }

                //if date behind the current, do not proceed
                if (mviewModel.IsDateCompared(tie_valid_until.getText().toString(), mviewModel.GetCurrentDate())){
                    Toast.makeText(requireActivity(), "Validity date should not be earlier than the current date", Toast.LENGTH_SHORT).show();
                    return;
                }

                ELodgeCalendar lodgeCalendar = new ELodgeCalendar(
                        "",
                        poSelectedLodge.getSLodgeIDx(),
                        tie_year.getText() == null ? "" : tie_year.getText().toString(),
                        mviewModel.GetCurrentDate(),
                        tie_valid_until.getText() == null ? "1900-00-00" : tie_valid_until.getText().toString(),
                        mviewModel.GetCurrentDate(),
                        mviewModel.GetCurrentDateTime()
                );

                poMessage.ShowMessage(2, "Is your lodge information complete?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(androidx.appcompat.app.@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(androidx.appcompat.app.@NotNull AlertDialog poMessage1) {
                        poMessage1.dismiss();

                        mviewModel.CreateLodgeCalendar(lodgeCalendar, new VM_Account.OnSubmit() {
                            @Override
                            public void onLoad() {
                                poLoading.ShowDialog("Creating lodge calendar. Please wait . .");
                            }

                            @Override
                            public void onSuccess() {
                                poLoading.DismissDialog();

                                poMessage.InitDialog();
                                poMessage.ShowMessage(0, "Lodge calendar saved successfully", "Okay", "", new Message_Dialog.OnDialogClick() {
                                    @Override
                                    public void OnPositive(androidx.appcompat.app.@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();

                                        requireActivity()
                                                .getSupportFragmentManager()
                                                .popBackStack("lodge_calendar_entry", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    }

                                    @Override
                                    public void OnNegative(androidx.appcompat.app.@NotNull AlertDialog poDialog) {}
                                });
                            }

                            @Override
                            public void onError(String fsError) {
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


        return view;
    }
}