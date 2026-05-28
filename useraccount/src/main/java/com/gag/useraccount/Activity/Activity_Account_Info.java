package com.gag.useraccount.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gag.useraccount.R;
import com.gag.useraccount.ViewModel.VM_Account;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Room.Entities.EUserInfo;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class Activity_Account_Info extends AppCompatActivity {

    private VM_Account mviewModel;
    private LoadDialog poLoading;
    private Message_Dialog poMessage;

    private MaterialToolbar toolbar;
    private MaterialTextView mtv_passwordError, mtv_verifyError;
    private TextInputEditText tie_lastname, tie_birthdate, tie_glpid, tie_username, tie_password, tie_verifypassword;
    private MaterialButton btn_create_acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_info);

        mviewModel = new ViewModelProvider(Activity_Account_Info.this).get(VM_Account.class);
        poLoading = new LoadDialog(Activity_Account_Info.this);
        poMessage = new Message_Dialog(Activity_Account_Info.this);

        poLoading.InitDialog();
        poMessage.InitDialog();

        //this intent is for account update
        if (getIntent().hasExtra("user_id")){

            if (getIntent().getStringExtra("user_id") == null || getIntent().getStringExtra("user_id").isEmpty()){

                poMessage.ShowMessage(1, "User ID not found", "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        finish();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
                return;
            }
        }

        InitWidgets(getIntent().hasExtra("user_id"));
        InitListener(getIntent().hasExtra("user_id"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void InitWidgets(boolean isUpdate){

        toolbar = findViewById(R.id.toolbar);
        tie_lastname = findViewById(R.id.tie_lastname);
        tie_birthdate = findViewById(R.id.tie_birthdate);
        tie_glpid = findViewById(R.id.tie_id);
        tie_username = findViewById(R.id.tie_username);
        tie_password = findViewById(R.id.tie_password);
        tie_verifypassword = findViewById(R.id.tie_verifypassword);
        btn_create_acct = findViewById(R.id.btn_create_acct);

        mtv_passwordError = findViewById(R.id.mtv_passwordError);
        mtv_verifyError = findViewById(R.id.mtv_verifyError);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Account");

        tie_username.setEnabled(true);
        btn_create_acct.setText("Create Account");

        //update mode
        if (isUpdate){
            getSupportActionBar().setTitle("Update Account");

            tie_username.setEnabled(false);
            btn_create_acct.setText("Update Account");
        }

    }

    private void InitListener(boolean isUpdate){

        tie_birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar loCalendar = Calendar.getInstance();

                new DatePickerDialog(Activity_Account_Info.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        tie_birthdate.setText(i + "-" + (i1 + 1) + "-" + i2);
                    }
                }, loCalendar.get(Calendar.YEAR),
                        loCalendar.get(Calendar.MONTH),
                        loCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        tie_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() < 1){
                    mtv_passwordError.setVisibility(View.GONE);
                    return;
                }

                if (!IsPasswordOkay(mtv_passwordError, charSequence.toString())){
                    return;
                }
                mtv_passwordError.setVisibility(View.GONE);
            }
        });

        tie_verifypassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() < 1){
                    mtv_verifyError.setVisibility(View.GONE);
                    return;
                }

                if (!IsPasswordOkay(mtv_verifyError, charSequence.toString())){
                    return;
                }
                mtv_verifyError.setVisibility(View.GONE);
            }
        });

        btn_create_acct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<TextInputEditText> faFields = new ArrayList<>();
                Collections.addAll(faFields, tie_lastname, tie_birthdate, tie_glpid, tie_username, tie_password, tie_verifypassword  );

                if (!IsEntryOkay(faFields)){
                    return;
                }

                //initialize user id, if update account
                String lsUserID = "";
                if (isUpdate){
                    lsUserID = getIntent().getStringExtra("user_id");
                }

                EUserInfo poUser =  new EUserInfo(
                        lsUserID,
                        tie_username.getText()== null ? "" : tie_username.getText().toString(),
                        tie_password.getText()== null ? "" : tie_password.getText().toString(),
                        "",
                        tie_glpid.getText()== null ? "" : tie_glpid.getText().toString(),
                        tie_lastname.getText()== null ? "" : tie_lastname.getText().toString(),
                        tie_birthdate.getText()== null ? "" : tie_birthdate.getText().toString(),
                        0,
                        "0",
                        "",
                        ""
                );

                poMessage.ShowMessage(2, "Are you sure your information is complete?", "Yes", "No", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {

                        poDialog.dismiss();

                        mviewModel.CreateUser(poUser, new VM_Account.OnLogin() {
                            @Override
                            public void onLoad() {
                                poLoading.ShowDialog("Creating account. Please wait . . .");
                            }

                            @Override
                            public void onSuccess() {
                                poLoading.DismissDialog();

                                poMessage.ShowMessage(0, "Account submitted successfully", "Okay", "", new Message_Dialog.OnDialogClick() {
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
                                poLoading.DismissDialog();

                                poMessage.ShowMessage(1, fsError, "Okay", "", new Message_Dialog.OnDialogClick() {
                                    @Override
                                    public void OnPositive(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();
                                    }

                                    @Override
                                    public void OnNegative(@NotNull AlertDialog poDialog) {

                                    }
                                });
                            }
                        });

                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }
                });
            }
        });
    }

    private boolean IsEntryOkay(ArrayList<TextInputEditText> faFields){

        boolean isFieldsOkay = true;

        String lsPassword = "";
        String lsVerifyPassword = "";

        for (TextInputEditText field : faFields){

            // extract (tie_lastname => lastname)
            String fID = getResources().getResourceEntryName(field.getId()).substring(4);

            //check empty fields, and notify message
            if (field.getText() == null || field.getText().toString().isEmpty()){

                field.requestFocus();

                if (fID.equalsIgnoreCase("verifypassword")){
                    Toast.makeText(Activity_Account_Info.this, "Please verify password", Toast.LENGTH_SHORT).show();
                    isFieldsOkay = false;
                    break;
                }
                Toast.makeText(Activity_Account_Info.this, fID.substring(0, 1).toUpperCase() + fID.substring(1) + " is empty", Toast.LENGTH_SHORT).show();
                isFieldsOkay = false;
                break;
            }

            //get password and verify password
            if (fID.equalsIgnoreCase("password")){

                //if password is not standard, notify message
                if (!IsPasswordOkay(mtv_passwordError, field.getText().toString())){
                    field.requestFocus();
                    isFieldsOkay = false;
                    break;
                }
                lsPassword = field.getText().toString();
            }
            if (fID.equalsIgnoreCase("verifypassword")) {

                //if password is not standard, notify message
                if (!IsPasswordOkay(mtv_verifyError, field.getText().toString())){
                    field.requestFocus();
                    isFieldsOkay = false;
                    break;
                }
                lsVerifyPassword = field.getText().toString();
            }

        }

        //notify mismatched passwords
        if (!lsPassword.isEmpty() && !lsVerifyPassword.isEmpty() && !lsPassword.equalsIgnoreCase(lsVerifyPassword)){
            Toast.makeText(Activity_Account_Info.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return isFieldsOkay;

    }

    @SuppressLint("SetTextI18n")
    public boolean IsPasswordOkay(MaterialTextView errorObj, String fsPassword){

        //display message, if password is not standardize
        if (fsPassword.length() < 5){
            errorObj.setText("Please enter atleast more than 5 characters");
            errorObj.setVisibility(View.VISIBLE);
            return false;
        }
        if (!fsPassword.matches("^[a-zA-Z0-9]+$")) {
            errorObj.setVisibility(View.VISIBLE);
            errorObj.setText("Please remove any special characters");
            return false;
        }
        return true;
    }

}