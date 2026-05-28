package com.gag.useraccount.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gag.useraccount.R;
import com.gag.useraccount.ViewModel.VM_Account;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

public class Activity_Login extends AppCompatActivity {

    private VM_Account mviewModel;
    private Message_Dialog poMessage;
    private LoadDialog poDialog;

    private MaterialButton btn_login;
    private MaterialTextView mtv_signup;
    private TextInputEditText tie_password, tie_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mviewModel = new ViewModelProvider(this).get(VM_Account.class);
        poMessage = new Message_Dialog(Activity_Login.this);
        poDialog = new LoadDialog(Activity_Login.this);

        poMessage.InitDialog();
        poDialog.InitDialog();

        InitWidgets();
        InitListener();
        
    }

    private void InitWidgets(){
        btn_login = findViewById(R.id.btn_login);
        tie_username = findViewById(R.id.tie_username);
        tie_password = findViewById(R.id.tie_password);
        mtv_signup = findViewById(R.id.mtv_signup);
    }

    private void InitListener(){

        mtv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(
            Activity_Login.this, Activity_Account.class
                         ));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mviewModel.LoginUser(tie_username.getText().toString(), tie_password.getText().toString(), new VM_Account.OnSubmit() {

                    @Override
                    public void onLoad() {
                        poDialog.ShowDialog("Logging in...");
                    }

                    @Override
                    public void onSuccess() {
                        poDialog.DismissDialog();

                        Intent loIntent = new Intent();
                        loIntent.putExtra("result_token", mviewModel.GetSession().getokenID());
                        loIntent.putExtra("log_date", mviewModel.GetSession().getLogDate());
                        setResult(Activity_Login.RESULT_OK, loIntent);

                        finish();
                    }

                    @Override
                    public void onError(String fsError) {
                        poDialog.DismissDialog();
                        poMessage.ShowMessage(1, fsError, "Okay", "", new Message_Dialog.OnDialogClick() {
                            @Override
                            public void OnPositive(@NotNull AlertDialog poDialog) { poDialog.dismiss(); }
                            @Override
                            public void OnNegative(@NotNull AlertDialog poDialog) {}
                        });
                    }
                });
            }
        });

    }
}