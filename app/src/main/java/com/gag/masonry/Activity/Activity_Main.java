package com.gag.masonry.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;
import com.gag.useraccount.Activity.Activity_Login;

import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

public class Activity_Main extends AppCompatActivity {

    private VM_Main mviewModel;
    private Message_Dialog poMessage;
    private LoadDialog poLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mviewModel = new ViewModelProvider(this).get(VM_Main.class);
        poMessage = new Message_Dialog(Activity_Main.this);
        poLoad = new LoadDialog(Activity_Main.this);

        poMessage.InitDialog();
        poLoad.InitDialog();

        //initialze transaction
        mviewModel.InitData(new VM_Main.InitData() {
            @Override
            public void isLoading() {
                poLoad.ShowDialog("Initializing Data. Please wait . .");
            }

            @Override
            public void hasLoggedIn() {

            }

            @Override
            public void isLoginNeeded() {
                poLoad.DismissDialog();

                Intent loIntent = new Intent(Activity_Main.this, Activity_Login.class);
                startActivity(loIntent);
            }

            @Override
            public void isSessionExpired() {
                poLoad.DismissDialog();

                poMessage.ShowMessage(
                        1,
                        "Session has expired. Please login you account",
                        "Okay",
                        "",
                        new Message_Dialog.OnDialogClick() {
                            @Override
                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();

                                Intent loIntent = new Intent(Activity_Main.this, Activity_Login.class);
                                startActivity(loIntent);
                            }

                            @Override
                            public void OnNegative(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();
                            }
                        }
                );
            }
        });

    }
}