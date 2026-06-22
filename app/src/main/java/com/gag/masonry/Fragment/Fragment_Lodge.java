package com.gag.masonry.Fragment;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.gag.masonry.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.gag.appdriver.App.Adapters.TownCityAdapter;
import org.gag.appdriver.App.Models.TownProvince;
import org.gag.appdriver.App.ViewModels.VM_Lodge;
import org.gag.appdriver.Room.Entities.ELodgeInfo;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Fragment_Lodge extends Fragment {

    private VM_Lodge mViewmodel;
    private LoadDialog poLoad;
    private Message_Dialog poMessage;
    private ELodgeInfo poLodge;
    private TownCityAdapter TownProvAdapter;

    private String lsTwnIDx, lsProvIDx;

    private TextInputEditText tieLodgeName;
    private TextInputEditText tieAddress;
    private TextInputEditText tie_zip;
    private MaterialAutoCompleteTextView autoProv;
    private MaterialButton btnSave;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_lodge, container, false);

        mViewmodel = new ViewModelProvider(requireActivity()).get(VM_Lodge.class);
        poLoad = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        poLoad.InitDialog();
        poMessage.InitDialog();

        InitViews(view);
        InitDataReceiver();
        InitListener();

        return view;
    }

    private void InitViews(View view){

        tieLodgeName = view.findViewById(R.id.tie_lodge_name);
        tieAddress = view.findViewById(R.id.tie_address);
        autoProv = view.findViewById(R.id.auto_prov);
        tie_zip = view.findViewById(R.id.tie_zip);
        btnSave = view.findViewById(R.id.btn_save);

    }

    private void InitDataReceiver(){

        if (getArguments() == null){

            poLodge = new ELodgeInfo(
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
            );
        }else {

            mViewmodel.ObserveLodgeInfo(getArguments().getString("lodge_id")).observe(getViewLifecycleOwner(), new Observer<ELodgeInfo>() {
                @Override
                public void onChanged(ELodgeInfo eLodgeInfo) {

                    if (eLodgeInfo == null) return;

                    poLodge = eLodgeInfo;

                    tieLodgeName.setText(eLodgeInfo.getSLodgeNme());
                    tieAddress.setText(eLodgeInfo.getSAddressx());
                    tie_zip.setText(eLodgeInfo.getSZippCode());

                   lsProvIDx = eLodgeInfo.getSProvName();
                   lsTwnIDx = eLodgeInfo.getSTownName();

                   String lsTownInfo = mViewmodel.GetTownInfo(lsTwnIDx == null ? "" : lsTwnIDx) == null ? "" : mViewmodel.GetTownInfo(lsTwnIDx).getPsTownProvNme();
                   autoProv.setText(lsTownInfo);
                }
            });
        }
    }

    private boolean IsEntryOkay(){

        if (poLodge == null){
            Toast.makeText(requireActivity(), "Lodge not initialize", Toast.LENGTH_SHORT).show();
            return false;
        }else if (poLodge.getSLodgeNme().isEmpty()){
            Toast.makeText(requireActivity(), "Lodge name not initialize", Toast.LENGTH_SHORT).show();
            tieLodgeName.requestFocus();
            return false;
        }else if (poLodge.getSProvName().isEmpty()){
            Toast.makeText(requireActivity(), "Lodge Province not initialize", Toast.LENGTH_SHORT).show();
            autoProv.requestFocus();
            return false;
        }else if (poLodge.getSTownName().isEmpty()){
            Toast.makeText(requireActivity(), "Lodge Town not initialize", Toast.LENGTH_SHORT).show();
            autoProv.requestFocus();
            return false;
        }else if (poLodge.getSAddressx().isEmpty()){
            Toast.makeText(requireActivity(), "Lodge Address not initialize", Toast.LENGTH_SHORT).show();
            autoProv.requestFocus();
            return false;
        }else if (poLodge.getSZippCode().isEmpty()){
            Toast.makeText(requireActivity(), "Address Zip not initialize", Toast.LENGTH_SHORT).show();
            tie_zip.requestFocus();
            return false;
        }
        return true;
    }

    private void InitListener(){

        autoProv.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() < 1) return;

                //display the properties from selected town
                mViewmodel.SearchTown(charSequence.toString()).observe(getViewLifecycleOwner(), new Observer<List<TownProvince>>() {
                    @Override
                    public void onChanged(List<TownProvince> townProvinces) {

                        TownProvAdapter = new TownCityAdapter(
                                requireActivity(),
                                android.R.layout.simple_spinner_dropdown_item,
                                townProvinces
                        );
                        autoProv.setAdapter(TownProvAdapter);

                        TownProvAdapter.getFilter().filter(charSequence.toString());
                    }
                });;
            }
        });

        autoProv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //get properties to be passed later on
                lsTwnIDx = ((TownProvince) adapterView.getItemAtPosition(i)).getPsTownIDxx();
                lsProvIDx = ((TownProvince) adapterView.getItemAtPosition(i)).getPsProvIDxx();

                autoProv.setText(((TownProvince) adapterView.getItemAtPosition(i)).getPsTownProvNme(), false);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //initialize values
                poLodge.setSLodgeNme(tieLodgeName.getText() == null ? "" : tieLodgeName.getText().toString());
                poLodge.setSAddressx(tieAddress.getText() == null ? "" : tieAddress.getText().toString());
                poLodge.setSProvName(lsProvIDx);
                poLodge.setSTownName(lsTwnIDx);
                poLodge.setSZippCode(tie_zip.getText() == null ? "" : tie_zip.getText().toString());

                //validate entry
                if (!IsEntryOkay()) return;

                poMessage.ShowMessage(2, "Is your information complete?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        mViewmodel.CreateLodge(poLodge, new VM_Lodge.OnDownload() {
                            @Override
                            public void OnLoad() {
                                poLoad.ShowDialog("Creating Lodge. Please wait . . .");
                            }

                            @Override
                            public void OnSuccess() {
                                poLoad.DismissDialog();

                                poMessage.ShowMessage(0, "Lodge has been created successfully", "Okay", "", new Message_Dialog.OnDialogClick() {
                                    @Override
                                    public void OnPositive(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();

                                        tieLodgeName.setText("");
                                        tieAddress.setText("");
                                        tie_zip.setText("");
                                        autoProv.setText("");

                                        poLodge = new ELodgeInfo(
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                ""
                                        );
                                    }

                                    @Override
                                    public void OnNegative(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();
                                    }
                                });
                            }

                            @Override
                            public void OnError(String fsMessage) {
                                poLoad.DismissDialog();

                                poMessage.ShowMessage(0, fsMessage, "Okay", "", new Message_Dialog.OnDialogClick() {
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

    }
}