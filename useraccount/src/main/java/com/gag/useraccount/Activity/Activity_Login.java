package com.gag.useraccount.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gag.useraccount.R;
import com.gag.useraccount.ViewModel.VM_Account;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class Activity_Login extends AppCompatActivity {

    private VM_Account mviewModel;

    private MaterialButton btn_login;
    private TextInputEditText tie_memberid;
    private TextInputEditText tie_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mviewModel = new ViewModelProvider(this).get(VM_Account.class);

        btn_login = findViewById(R.id.btn_login);
        tie_memberid = findViewById(R.id.tie_memberid);
        tie_password = findViewById(R.id.tie_password);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mviewModel.LoginUser(tie_memberid.getText().toString(), tie_password.getText().toString(), new VM_Account.OnLogin() {
                    @Override
                    public void onSuccess() {
                        System.out.print("Login Successful");
                    }

                    @Override
                    public void onError(String fsError) {
                        System.out.print(fsError);
                    }
                });
            }
        });
        
    }
}