package com.gag.useraccount.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gag.useraccount.R;
import com.gag.useraccount.ViewModel.VM_Account;
import com.gag.useraccount.ViewModel.VM_Member;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.gag.appdriver.Room.Entities.ELodge;
import org.gag.appdriver.Room.Entities.ETitle;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

import org.gag.appdriver.Room.Entities.EMemberAddress;
import org.gag.appdriver.Room.Entities.EMemberContact;
import org.gag.appdriver.Room.Entities.EMemberEmail;
import org.gag.appdriver.Room.Entities.EMemberMaster;

public class Activity_Create_Member extends AppCompatActivity {

    private Message_Dialog poMessage;
    private LoadDialog poDialog;
    private VM_Member mviewModel;
    private MaterialToolbar toolbar;


    private LinearLayout layout_member,
            layout_personal;

    private MaterialAutoCompleteTextView auto_lodge,
            auto_account,
            auto_status,
            auto_title,
            auto_sponosr,
            auto_address,
            auto_contact,
            auto_email,
            auto_civil;

    private TextInputLayout til_sponsor,
            til_address,
            til_contact,
            til_email,
            til_midname,
            til_suffix,
            til_civil;

    private MaterialButton btn_add_sponsor,
            btn_add_address,
            btn_add_contact,
            btn_add_email,
            btn_create,
            btn_cancel;

    private TextInputEditText tie_lastname,
            tie_firstname,
            tie_middlename,
            tie_suffix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_member);

        mviewModel = new ViewModelProvider(Activity_Create_Member.this).get(VM_Member.class);
        poMessage = new Message_Dialog(Activity_Create_Member.this);
        poDialog = new LoadDialog(Activity_Create_Member.this);
        poMessage.InitDialog();
        poDialog.InitDialog();


        initViews();
        initToolbar();
        initDropdowns();
        initButtons();
    }

    private void initViews() {

        toolbar = findViewById(R.id.toolbar);

        layout_member = findViewById(R.id.layout_member);
        layout_personal = findViewById(R.id.layout_personal);

        auto_lodge = findViewById(R.id.auto_lodge);
        auto_account = findViewById(R.id.auto_account);
        auto_status = findViewById(R.id.auto_status);
        auto_title = findViewById(R.id.auto_title);
        auto_sponosr = findViewById(R.id.auto_sponosr);
        auto_address = findViewById(R.id.auto_address);
        auto_contact = findViewById(R.id.auto_contact);
        auto_email = findViewById(R.id.auto_email);
        auto_civil = findViewById(R.id.auto_civil);

        til_sponsor = findViewById(R.id.til_sponsor);
        til_address = findViewById(R.id.til_address);
        til_contact = findViewById(R.id.til_contact);
        til_email = findViewById(R.id.til_email);
        til_midname = findViewById(R.id.til_midname);
        til_suffix = findViewById(R.id.til_suffix);
        til_civil = findViewById(R.id.til_civil);

        btn_add_sponsor = findViewById(R.id.btn_add_sponsor);
        btn_add_address = findViewById(R.id.btn_add_address);
        btn_add_contact = findViewById(R.id.btn_add_contact);
        btn_add_email = findViewById(R.id.btn_add_email);
        btn_create = findViewById(R.id.btn_create);
        btn_cancel = findViewById(R.id.btn_cancel);

        tie_lastname = findViewById(R.id.tie_lastname);
        tie_firstname = findViewById(R.id.tie_firstname);
        tie_middlename = findViewById(R.id.tie_middlename);
        tie_suffix = findViewById(R.id.tie_suffix);

        btn_create.setText("Create Member");

        //update mode

    }

    private void initToolbar() {

        toolbar.setTitle("Create Member");
        toolbar.setNavigationIcon(org.gag.appdriver.R.drawable.ic_nav_dropdown);

        toolbar.setNavigationOnClickListener(view -> showCancelDialog());
    }

    private void initDropdowns() {


        String[] account = {
                "Regular",
                "Premium",
                "VIP"
        };

        String[] status = {
                "Active",
                "Inactive",
                "Pending"
        };



        String[] civil = {
                "Single",
                "Married",
                "Widowed",
                "Separated"
        };

        ArrayAdapter<String> accountAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        account);

        ArrayAdapter<String> statusAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        status);



        ArrayAdapter<String> civilAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        civil);


        auto_account.setAdapter(accountAdapter);
        auto_status.setAdapter(statusAdapter);
        auto_civil.setAdapter(civilAdapter);
        mviewModel.GetLodges().observe(
                this,
                lodges -> {

                    ArrayList<String> laLodge =
                            new ArrayList<>();

                    for (ELodge lodge : lodges){

                        laLodge.add(
                                lodge.getSLodgeNme()
                        );
                    }

                    ArrayAdapter<String> lodgeAdapter =
                            new ArrayAdapter<>(
                                    Activity_Create_Member.this,
                                    android.R.layout.simple_list_item_1,
                                    laLodge
                            );

                    auto_lodge.setAdapter(lodgeAdapter);
                });
        mviewModel.GetTitles().observe(
                this,
                titles -> {
                    ArrayList<String> laTitles =
                            new ArrayList<>();

                    for (ETitle title : titles){
                        laTitles.add(
                                title.getSTitleDsc()
                        );
                    }

                    ArrayAdapter<String> titleAdapter =
                            new ArrayAdapter<>(
                                    Activity_Create_Member.this,
                                    android.R.layout.simple_list_item_1,
                                    laTitles
                            );

                    auto_title.setAdapter(titleAdapter);
                });
    }

    private void initButtons() {

        btn_add_sponsor.setOnClickListener(view -> {

//            if (TextUtils.isEmpty(auto_sponosr.getText().toString())) {
//
//                auto_sponosr.setError("Sponsor is required");
//                auto_sponosr.requestFocus();
//                return;
//            }

            poMessage.InitDialog();

            poMessage.ShowMessage(
                    1,
                    "Sponsor added successfully.",
                    "Okay",
                    "",
                    new Message_Dialog.OnDialogClick() {
                        @Override
                        public void OnPositive(AlertDialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void OnNegative(AlertDialog dialog) {
                            dialog.dismiss();
                        }
                    });
        });

        btn_add_address.setOnClickListener(view -> {

            if (TextUtils.isEmpty(auto_address.getText().toString())) {

                auto_address.setError("Address is required");
                auto_address.requestFocus();
                return;
            }

            poMessage.InitDialog();

            poMessage.ShowMessage(
                    1,
                    "Address added successfully.",
                    "Okay",
                    "",
                    new Message_Dialog.OnDialogClick() {
                        @Override
                        public void OnPositive(AlertDialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void OnNegative(AlertDialog dialog) {
                            dialog.dismiss();
                        }
                    });
        });

        btn_add_contact.setOnClickListener(view -> {

            if (TextUtils.isEmpty(auto_contact.getText().toString())) {

                auto_contact.setError("Contact number is required");
                auto_contact.requestFocus();
                return;
            }

            poMessage.InitDialog();

            poMessage.ShowMessage(
                    1,
                    "Contact added successfully.",
                    "Okay",
                    "",
                    new Message_Dialog.OnDialogClick() {
                        @Override
                        public void OnPositive(AlertDialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void OnNegative(AlertDialog dialog) {
                            dialog.dismiss();
                        }
                    });
        });

        btn_add_email.setOnClickListener(view -> {

            if (TextUtils.isEmpty(auto_email.getText().toString())) {

                auto_email.setError("Email is required");
                auto_email.requestFocus();
                return;
            }

            poMessage.InitDialog();

            poMessage.ShowMessage(
                    1,
                    "Email added successfully.",
                    "OK",
                    "",
                    new Message_Dialog.OnDialogClick() {
                        @Override
                        public void OnPositive(AlertDialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void OnNegative(AlertDialog dialog) {
                            dialog.dismiss();
                        }
                    });
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 * MEMBER MASTER
                 */
                EMemberMaster poMember = new EMemberMaster(
                                "",                             // sMemberID
                                getText(auto_lodge),           // sLodgeIDx
                                getText(auto_account),         // sGLPIDNoX

                                getText(tie_lastname),         // sLastName
                                getText(tie_firstname),        // sFrstName
                                getText(tie_suffix),           // sSuffixNm
                                getText(tie_middlename),       // sMiddName

                                getText(auto_civil),           // cCivilStat
                                "",                            // dBirthDte

                                getText(auto_status),          // cMmbrStat
                                "",                            // dMembrshp
                                "",                            // dSuspendx

                                getText(auto_title),           // sTitleIDx

                                "", "", "", "",                // dates
                                getText(auto_sponosr),         // sSponsor1
                                "",                            // sSponsor2
                                "",                            // sSponsor3

                                0.0,                           // nDueBalxx
                                0.0,                           // nPrjBalxx

                                "1",                           // cRecdStat
                                "",                            // sModified
                                ""                             // dModified
                        );

                /**
                 * ADDRESS
                 */
                ArrayList<EMemberAddress> laAddress = new ArrayList<>();

                laAddress.add(new EMemberAddress(
                                "",                            // sAddressID
                                "",                            // sMemberID
                                getText(auto_address),         // sAddressx
                                "",                            // sTownIDxx
                                "1",                           // cIsHomeAd
                                "1",                           // cRecdStat
                                "",                            // sModified
                                ""                             // dModified
                        )
                );

                /**
                 * CONTACT
                 */
                ArrayList<EMemberContact> laContact =new ArrayList<>();

                laContact.add(new EMemberContact(
                                "",                            // sContctID
                                "",                            // sMemberID
                                getText(auto_contact),         // sContctNo
                                "Primary Contact",             // sRemarksx
                                "1"                            // cRecdStat
                        )
                );

                /**
                 * EMAIL
                 */
                ArrayList<EMemberEmail> laEmail = new ArrayList<>();
                laEmail.add(new EMemberEmail(
                                "",                            // sMailIDxx
                                         "",                            // sMemberID
                                         getText(auto_email),           // sEmailAdd
                                         "1"                            // cRecdStat
                        )
                );

                /**
                 * CREATE MEMBER
                 */
                mviewModel.CreateMember(
                        poMember,
                        laAddress,
                        laContact,
                        laEmail,
                        new VM_Member.OnLogin() {
                            @Override
                            public void onLoad() {
                                poDialog.ShowDialog("Creating member...");
                            }
                            @Override
                            public void onSuccess() {
                                poDialog.DismissDialog();
                                poMessage.ShowMessage(
                                        0,
                                        "Member created successfully",
                                        "Okay",
                                        "",
                                        new Message_Dialog.OnDialogClick() {

                                            @Override
                                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                                poDialog.dismiss();
                                                finish();
                                            }
                                            @Override
                                            public void OnNegative(@NotNull AlertDialog poDialog) {}
                                        });
                            }

                            @Override
                            public void onError(String fsError) {
                                poDialog.DismissDialog();
                                poMessage.ShowMessage(
                                        1,
                                        fsError,
                                        "Okay",
                                        "",
                                        new Message_Dialog.OnDialogClick() {

                                            @Override
                                            public void OnPositive(@NotNull AlertDialog poDialog) { poDialog.dismiss(); }

                                            @Override
                                            public void OnNegative(@NotNull AlertDialog poDialog) {}
                                        });
                            }

                        });
            }
        });

        btn_cancel.setOnClickListener(view -> showCancelDialog());
    }
    private String getText(TextInputEditText field) {

        return field.getText() == null
                ? ""
                : field.getText().toString().trim();
    }

    private String getText(MaterialAutoCompleteTextView field) {

        return field.getText() == null
                ? ""
                : field.getText().toString().trim();
    }
    private void showCancelDialog() {

        poMessage.InitDialog();

        poMessage.ShowMessage(
                3,
                "Are you sure you want to cancel?",
                "YES",
                "NO",
                new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(AlertDialog dialog) {
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void OnNegative(AlertDialog dialog) {
                        dialog.dismiss();
                    }
                });
    }

    private boolean isDataValid() {

        if (TextUtils.isEmpty(auto_lodge.getText().toString())) {

            auto_lodge.setError("Lodge is required");
            auto_lodge.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(auto_account.getText().toString())) {

            auto_account.setError("Account type is required");
            auto_account.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(auto_status.getText().toString())) {

            auto_status.setError("Status is required");
            auto_status.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(auto_title.getText().toString())) {

            auto_title.setError("Title is required");
            auto_title.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(tie_lastname.getText().toString())) {

            tie_lastname.setError("Lastname is required");
            tie_lastname.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(tie_firstname.getText().toString())) {

            tie_firstname.setError("Firstname is required");
            tie_firstname.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(auto_civil.getText().toString())) {

            auto_civil.setError("Civil status is required");
            auto_civil.requestFocus();
            return false;
        }

        return true;
    }


    public void onBackPressed() {
        super.onBackPressed();
        showCancelDialog();
    }
}