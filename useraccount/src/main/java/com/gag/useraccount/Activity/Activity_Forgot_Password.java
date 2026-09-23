package com.gag.useraccount.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gag.useraccount.R;
import com.gag.useraccount.ViewModel.VM_Account;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Core.SMSApp;
import org.gag.appdriver.App.Interface.OnSMSStatus;
import org.gag.appdriver.Room.Entities.EUserInfo;
import org.gag.appdriver.Utilities.Disclosure_Dialog;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;

public class Activity_Forgot_Password extends AppCompatActivity {

    private VM_Account mviewModel;
    private LoadDialog poLoading;
    private Message_Dialog poMessage;

    private String lsOTP;

    private MaterialToolbar toolbar;
    private TextInputEditText tie_number, tie_password, tie_verifypassword;
    private MaterialButton btn_forgot, btn_verify;
    private ImageButton btn_close;
    private MaterialCardView mcv_otp;
    private MaterialTextView mtv_passwordError, mtv_verifyError, mtv_resend;
    private TextInputEditText tie_cd1, tie_cd2, tie_cd3, tie_cd4, tie_cd5, tie_cd6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);

        mviewModel = new ViewModelProvider(Activity_Forgot_Password.this).get(VM_Account.class);
        poLoading = new LoadDialog(Activity_Forgot_Password.this);
        poMessage = new Message_Dialog(Activity_Forgot_Password.this);

        poLoading.InitDialog();
        poMessage.InitDialog();

        InitWidgets();
        InitListener();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Forgot Password");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void InitWidgets(){

        toolbar = findViewById(R.id.toolbar);

        tie_number = findViewById(R.id.tie_number);
        tie_password = findViewById(R.id.tie_password);
        mtv_passwordError = findViewById(R.id.mtv_passwordError);
        mtv_verifyError = findViewById(R.id.mtv_verifyError);
        tie_verifypassword = findViewById(R.id.tie_verifypassword);

        tie_cd1 = findViewById(R.id.tie_cd1);
        tie_cd2 = findViewById(R.id.tie_cd2);
        tie_cd3 = findViewById(R.id.tie_cd3);
        tie_cd4 = findViewById(R.id.tie_cd4);
        tie_cd5 = findViewById(R.id.tie_cd5);
        tie_cd6 = findViewById(R.id.tie_cd6);

        btn_forgot = findViewById(R.id.btn_forgot);
        btn_close  = findViewById(R.id.btn_close);
        btn_verify = findViewById(R.id.btn_verify);

        mcv_otp = findViewById(R.id.mcv_otp);
        mtv_resend = findViewById(R.id.mtv_resend);

    }

    private void ClearOTP(){

        //re order clear text from last field to first field, so it would re focus on field back to start
        tie_cd6.setText("");
        tie_cd5.setText("");
        tie_cd4.setText("");
        tie_cd3.setText("");
        tie_cd2.setText("");
        tie_cd1.setText("");
    }

    private void InitListener(){

        SetupOtpFields();

        btn_forgot.setOnClickListener(view -> {

            //if permission is not granted, redirect user to app settings and manually allow the permission before submitting
            if (Activity_Forgot_Password.this.checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){

                Disclosure_Dialog poDisclosure = new Disclosure_Dialog(Activity_Forgot_Password.this);
                poDisclosure.InitDialog();

                poDisclosure.ShowDisclosure(org.gag.appdriver.R.drawable.masonry_logo,
                        "SMS permission is required. Please allow before saving your entry", "Deny", "Accept", new Disclosure_Dialog.OnDialogClick() {
                            @Override
                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();
                            }

                            @Override
                            public void OnNegative(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();

                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + Activity_Forgot_Password.this.getPackageName()));
                                startActivity(intent);

                            }
                        });
                return;
            }
            VerifyAccount();
        });

        btn_verify.setOnClickListener(view -> {

            String lscd1 = tie_cd1.getText() == null ? "" : tie_cd1.getText().toString();
            String lscd2 = tie_cd2.getText() == null ? "" : tie_cd2.getText().toString();
            String lscd3 = tie_cd3.getText() == null ? "" : tie_cd3.getText().toString();
            String lscd4 = tie_cd4.getText() == null ? "" : tie_cd4.getText().toString();
            String lscd5 = tie_cd5.getText() == null ? "" : tie_cd5.getText().toString();
            String lscd6 = tie_cd6.getText() == null ? "" : tie_cd6.getText().toString();

            String lsVerifyCd = lscd1 + lscd2 + lscd3 + lscd4 + lscd5 + lscd6;
            if (!lsOTP.equalsIgnoreCase(lsVerifyCd)){
                ClearOTP();
                return;
            }

            //apply password change
            mviewModel.ChangePassword(tie_number.getText() == null ? "" : tie_number.getText().toString(), tie_password.getText() == null ? "" : tie_password.getText().toString(), new VM_Account.OnSubmit() {
                @Override
                public void onLoad() {
                    poLoading.ShowDialog("Saving new password. Please wait . . .");
                }

                @Override
                public void onSuccess() {
                    poLoading.DismissDialog();

                    poMessage.ShowMessage(0, "Password has been changed successfully", "Okay", "", new Message_Dialog.OnDialogClick() {
                        @Override
                        public void OnPositive(@NotNull AlertDialog poDialog) {
                            poDialog.dismiss();
                            finish();
                        }

                        @Override
                        public void OnNegative(@NotNull AlertDialog poDialog) {
                            poDialog.dismiss();
                        }
                    });
                }

                @Override
                public void onError(String fsError) {
                    poLoading.DismissDialog();
                    finish();
                }
            });
        });

        mtv_resend.setOnClickListener(view -> {

            poMessage.ShowMessage(2, "Do you want to resend OTP?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                @Override
                public void OnPositive(@NotNull AlertDialog poDialog) {
                    poDialog.dismiss();
                }

                @Override
                public void OnNegative(@NotNull AlertDialog poDialog) {
                    poDialog.dismiss();

                    ClearOTP();
                    SendOTP(lsOTP, tie_number.getText() == null ? "" : tie_number.getText().toString());
                }
            });
        });

        btn_close.setOnClickListener(view -> {

            if (mcv_otp.getVisibility() == View.VISIBLE){
                mcv_otp.setVisibility(View.GONE);

                Animation slideDown = AnimationUtils.loadAnimation(Activity_Forgot_Password.this, org.gag.appdriver.R.anim.anim_slide_down);
                mcv_otp.startAnimation(slideDown);
            }
        });
    }

    private void SetupOtpFields() {

        //list of otp fields
        TextInputEditText[] otpFields = {
                tie_cd1, tie_cd2, tie_cd3, tie_cd4, tie_cd5, tie_cd6
        };

        for (int i = 0; i < otpFields.length; i++) {

            final int index = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpFields.length - 1) {
                        otpFields[index + 1].requestFocus(); // move to next field
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0 &&  index > 0) {
                        otpFields[index - 1].requestFocus(); //move to previous field
                    }

                }
            });
        }
    }

    private boolean IsEntryOkay(){

        if (tie_number.getText() == null || tie_number.getText().toString().isEmpty()){
            Toast.makeText(Activity_Forgot_Password.this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tie_password.getText() == null || tie_password.getText().toString().isEmpty()) {
            Toast.makeText(Activity_Forgot_Password.this, "Password is empty", Toast.LENGTH_SHORT).show();
            return false;
        }else if (tie_password.getText() == null || tie_password.getText().toString().isEmpty()){
            Toast.makeText(Activity_Forgot_Password.this,  "Please verify password", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!IsPasswordOkay(mtv_passwordError, tie_password.getText().toString())){
            return false;
        }else if (!IsPasswordOkay(mtv_verifyError, tie_verifypassword.getText().toString())){
            return false;
        }

        if (!tie_password.getText().toString().equalsIgnoreCase(tie_verifypassword.getText().toString())){
            Toast.makeText(Activity_Forgot_Password.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        mtv_passwordError.setVisibility(View.GONE);
        mtv_verifyError.setVisibility(View.GONE);
        return true;
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

    private void VerifyAccount(){

        if (tie_number.getText() == null || tie_number.getText().toString().isEmpty()){
            Toast.makeText(Activity_Forgot_Password.this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
            return;
        } else if (!IsEntryOkay()) {
            return;
        }

        mviewModel.VerifyMobile(tie_number.getText().toString(), new VM_Account.OnVerifyMobile() {
            @Override
            public void onLoad() {
                poLoading.ShowDialog("Checking mobile account. Please wait . . .");
            }

            @Override
            public void onSuccess(String fsOTP) {
                poLoading.DismissDialog();

                lsOTP = fsOTP;
                poMessage.ShowMessage(0, "OTP will be sent to your mobile number. Please enter verification code to apply the changes", "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                        SendOTP(lsOTP, tie_number.getText().toString());
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
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
            }
        });

    }

    private void SendOTP(String fsOTP, String fsMobile){

        if (mcv_otp.getVisibility() != View.VISIBLE){

            mcv_otp.setVisibility(View.VISIBLE);

            Animation slideUp = AnimationUtils.loadAnimation(Activity_Forgot_Password.this, org.gag.appdriver.R.anim.anim_slide_up);
            mcv_otp.startAnimation(slideUp);

        }

        //send generated random number
        String lsMessage = "Kay Gandang Araw!\n\n" +
                "Here is your OTP " + fsOTP + ".\n" +
                "Please enter this code on Masonry application to verify changes on your account";

        mviewModel.SendSms(fsMobile, lsMessage, new VM_Account.OnSubmit() {
            @Override
            public void onLoad() {
                Toast.makeText(Activity_Forgot_Password.this, "Sending OTP. Please wait . . .", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(Activity_Forgot_Password.this, "OTP has been sent successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String fsError) {

                poMessage.ShowMessage(1, fsError, "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
            }
        });

//        new SMSApp(Activity_Forgot_Password.this, new OnSMSStatus() {
//            @Override
//            public void OnResult(String fsResult) {
//                Toast.makeText(Activity_Forgot_Password.this, fsResult, Toast.LENGTH_LONG).show();
//            }
//        }).SendSms(fsMobile, lsMessage);
    }

}