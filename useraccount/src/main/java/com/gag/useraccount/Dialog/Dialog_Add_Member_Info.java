package com.gag.useraccount.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.gag.useraccount.R;
import com.gag.useraccount.ViewModel.VM_Member;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.gag.appdriver.Room.DataObject.DTownInfo;

import java.util.ArrayList;
import java.util.List;

public class Dialog_Add_Member_Info{

    public enum SectionType {
        ADDRESS, CONTACT, EMAIL
    }

    public interface OnAddress{
        void OnAddress(DTownInfo.TownProvince loProvince);
    }

    public interface OnContact{
        void OnContact(String lsContactNo, String lsRemarks, String lsActive);
    }

    public interface OnEmail{
        void OnEmail(String lsEmail, String lsActive);
    }

    private final Context loInstance;
    private final View view;
    private final AlertDialog poDialog;

    private LinearLayout layoutAddress, layoutContact, layoutEmail;
    private MaterialAutoCompleteTextView auto_town;
    private TextInputEditText tie_address, tie_contact, tie_remarks, tie_email;
    private CheckBox chkHome, chkActive, chkcontact_active, chkemail_active;
    private MaterialButton btn_add, btn_cancel;

    private final VM_Member loViewModel;
    private final LifecycleOwner loOwner;
    private VM_Member.TownCityAdapter TownProvAdapter;

    private String lsTwnIDx, lsProvIDx;

    public Dialog_Add_Member_Info(Context context, VM_Member foViewModel, LifecycleOwner foOwner) {

        loInstance = context;

        view = LayoutInflater.from(context).inflate(R.layout.activity_dialog_add_member_info, null);

        // Build and show dialog
        poDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
                .create();

        poDialog.setCancelable(false);
        if (poDialog.getWindow() != null) {
            poDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


        loViewModel = foViewModel;
        loOwner = foOwner;

        InitViews();
    }

    private void InitViews(){

        layoutAddress = view.findViewById(R.id.layout_address);
        auto_town = view.findViewById(R.id.auto_town);
        tie_address = view.findViewById(R.id.tie_address);
        chkHome = view.findViewById(R.id.chkHome);
        chkActive = view.findViewById(R.id.chkActive);

        layoutContact = view.findViewById(R.id.layout_contact);
        tie_contact = view.findViewById(R.id.tie_contact);
        tie_remarks = view.findViewById(R.id.tie_remarks);
        chkcontact_active = view.findViewById(R.id.chkcontact_active);

        layoutEmail = view.findViewById(R.id.layout_email);
        tie_email = view.findViewById(R.id.tie_email);
        tie_email = view.findViewById(R.id.tie_email);
        chkemail_active = view.findViewById(R.id.chkemail_active);

        btn_add = view.findViewById(R.id.btn_add);
        btn_cancel = view.findViewById(R.id.btn_cancel);
    }

    public void ShowAddress(OnAddress foCallback){

        ClearFields(new ArrayList<>(List.of(auto_town, tie_address, chkHome, chkActive)), true);

        layoutAddress.setVisibility(View.VISIBLE);
        layoutContact.setVisibility(View.GONE);
        layoutEmail.setVisibility(View.GONE);

        auto_town.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() < 1) return;

                //display the properties from selected town
                loViewModel.SearchTown(charSequence.toString()).observe(loOwner, new Observer<List<DTownInfo.TownProvince>>() {
                    @Override
                    public void onChanged(List<DTownInfo.TownProvince> townProvinces) {

                        TownProvAdapter = new VM_Member.TownCityAdapter(
                                loInstance,
                                android.R.layout.simple_spinner_dropdown_item,
                                townProvinces
                        );
                        auto_town.setAdapter(TownProvAdapter);

                        TownProvAdapter.getFilter().filter(charSequence.toString());
                    }
                });;
            }
        });

        auto_town.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //get properties to be passed later on
                lsTwnIDx = ((DTownInfo.TownProvince) adapterView.getItemAtPosition(i)).getPsTownIDxx();
                lsProvIDx = ((DTownInfo.TownProvince) adapterView.getItemAtPosition(i)).getPsProvIDxx();

                auto_town.setText(((DTownInfo.TownProvince) adapterView.getItemAtPosition(i)).getPsTownProvNme(), false);
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (auto_town.getAdapter() == null){
                    Toast.makeText(loInstance, "Selected town is invalid", Toast.LENGTH_SHORT).show();
                    return;
                } else if (lsTwnIDx == null || lsTwnIDx.isEmpty()) {
                    Toast.makeText(loInstance, "Town is invalid", Toast.LENGTH_SHORT).show();
                    return;
                } else if (lsProvIDx == null || lsProvIDx.isEmpty()) {
                    Toast.makeText(loInstance, "Province is invalid", Toast.LENGTH_SHORT).show();
                    return;
                }

                foCallback.OnAddress(
                        InitAddress(
                                lsTwnIDx,
                                lsProvIDx,
                                auto_town.getText() == null ? "" : auto_town.getText().toString(),
                                tie_address.getText() == null ? "" : tie_address.getText().toString(),
                                chkHome.isChecked() ? "1" : "0",
                                chkActive.isChecked() ? "1" : "0"
                        )
                );

                poDialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(view1 -> poDialog.dismiss());
        poDialog.show();
    }

    public void ShowContact(OnContact foCallback){

        ClearFields(new ArrayList<>(List.of(tie_contact, tie_remarks, chkcontact_active)), false);

        layoutAddress.setVisibility(View.GONE);
        layoutContact.setVisibility(View.VISIBLE);
        layoutEmail.setVisibility(View.GONE);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tie_contact.getText() == null || tie_contact.getText().toString().isEmpty()){
                    Toast.makeText(loInstance, "Please enter contact no", Toast.LENGTH_SHORT).show();
                    return;
                }

                foCallback.OnContact(
                        tie_contact.getText() == null ? "" : tie_contact.getText().toString(),
                        tie_remarks.getText() == null ? "" : tie_remarks.getText().toString(),
                        chkcontact_active.isChecked() ? "1" : "0"
                );
                poDialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(view1 -> poDialog.dismiss());
        poDialog.show();
    }

    public void ShowEmail(OnEmail foCallback){

        ClearFields(new ArrayList<>(List.of(tie_email, chkemail_active)), false);

        layoutAddress.setVisibility(View.GONE);
        layoutContact.setVisibility(View.GONE);
        layoutEmail.setVisibility(View.VISIBLE);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tie_email.getText() == null || tie_email.getText().toString().isEmpty()){
                    Toast.makeText(loInstance, "Please enter email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                foCallback.OnEmail(
                        tie_email.getText() == null ? "" : tie_email.getText().toString(),
                        chkemail_active.isChecked() ? "1" : "0"
                );
                poDialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(view1 -> poDialog.dismiss());
        poDialog.show();
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
            }
        }
    }

    private DTownInfo.TownProvince InitAddress(String fsTownID, String fsProvID, String fsTownProvNme, String fsAddress, String isHome, String isActive){

        return new DTownInfo.TownProvince(
                "",
                fsTownID,
                fsProvID,
                fsTownProvNme,
                fsAddress,
                isHome,
                isActive
        );
    }

}