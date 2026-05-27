package com.gag.masonry.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;
import com.gag.useraccount.Activity.Activity_Login;

import org.gag.appdriver.Constants.MENU_ITEM_CONSTANTS;
import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS;
import org.gag.appdriver.Libraries.Preferences.AppConfig;
import org.gag.appdriver.Room.Entities.EUserInfo;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Main extends AppCompatActivity {

    private VM_Main mviewModel;
    private Message_Dialog poMessage;
    private LoadDialog poLoad;
    private final ActivityResultLauncher<Intent> poLogin = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult loIntent) {

                    //this is to ensure that the token return from server is fully initialzed after successful login
                    if (loIntent.getResultCode() == Activity.RESULT_OK) {
                        Intent data = loIntent.getData();

                        if (!data.hasExtra("result_token") || data.getStringExtra("result_token") == null || data.getStringExtra("result_token").isEmpty()){

                            poMessage.ShowMessage(1, "Invalid access token", "Okay", "", new Message_Dialog.OnDialogClick() {
                                @Override
                                public void OnPositive(@NotNull AlertDialog poDialog) {
                                    System.exit(0);
                                }

                                @Override
                                public void OnNegative(@NotNull AlertDialog poDialog) {}
                            });
                            return;
                        }
                        mviewModel.GetSession().isLogIn("1");
                        mviewModel.GetSession().setLogDate(data.getStringExtra("log_date"));
                        mviewModel.GetSession().setTokenID(data.getStringExtra("result_token"));

                        InitData();
                    }
                }
    });

    private final ActivityResultLauncher<Intent> poLogout = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult loIntent) {

                    //this is to ensure that the token return from server is fully initialzed after successful login
                    if (loIntent.getResultCode() == Activity.RESULT_OK) {
                        mviewModel.EndSession();
                        InitData();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mviewModel = new ViewModelProvider(this).get(VM_Main.class);
        poMessage = new Message_Dialog(Activity_Main.this);
        poLoad = new LoadDialog(Activity_Main.this);

        poMessage.InitDialog();
        poLoad.InitDialog();

    }

    @Override
    protected void onStart() {
        super.onStart();

        InitData();
    }

    private void InitData(){

        //initialze transaction
        mviewModel.InitData(new VM_Main.InitData() {
            @Override
            public void isLoading() {}

            @Override
            public void hasLoggedIn() {

                mviewModel.GetUserInfo().observe(Activity_Main.this, new Observer<EUserInfo>() {
                    @Override
                    public void onChanged(EUserInfo eUserInfo) {

                        if (eUserInfo == null){

                            mviewModel.DownloadUserInfo(new VM_Main.OnDownloadUser() {
                                @Override
                                public void OnLoad() {
                                    poLoad.ShowDialog("Downloading user information");
                                }

                                @Override
                                public void OnSuccess() {
                                    poLoad.DismissDialog();
                                }

                                @Override
                                public void OnError(String fsMEssage) {
                                    poLoad.DismissDialog();

                                    poMessage.ShowMessage(1, fsMEssage, "Okay", "", new Message_Dialog.OnDialogClick() {
                                        @Override
                                        public void OnPositive(@NotNull AlertDialog poDialog) {
                                            poDialog.dismiss();
                                        }

                                        @Override
                                        public void OnNegative(@NotNull AlertDialog poDialog) {}
                                    });
                                }
                            });
                            return;
                        }

                        //initialze drawer menus
                        List<MENU_PARENT_CONSTANTS> faParentMenu = mviewModel.GetParentMenu(eUserInfo.getNUserLevl());

                        HashMap<String, List<MENU_ITEM_CONSTANTS>> faParentItems = new HashMap<>();
                        for (MENU_PARENT_CONSTANTS entries : faParentMenu){

                            faParentItems.put(entries.getFsIDxx(), mviewModel.GetMenuItem(eUserInfo.getNUserLevl(), entries.getFsIDxx()));
                        }

                        // Ensure data is passed as ArrayList to match expected types in Activity_Dashboard/Adapter_Drawer
                        ArrayList<MENU_PARENT_CONSTANTS> laParentList = new ArrayList<>(faParentMenu);

                        HashMap<String, ArrayList<MENU_ITEM_CONSTANTS>> loChildMap = new HashMap<>();
                        for (Map.Entry<String, List<MENU_ITEM_CONSTANTS>> entry : faParentItems.entrySet()) {
                            loChildMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
                        }

                        Intent loIntent = new Intent(Activity_Main.this, Activity_Dashboard.class);
                        loIntent.putExtra("parent_key", laParentList);
                        loIntent.putExtra("child_items", loChildMap);

                        poLogout.launch(loIntent);
                    }
                });
            }

            @Override
            public void isLoginNeeded() {
                poLoad.DismissDialog();

                Intent loIntent = new Intent(Activity_Main.this, Activity_Login.class);
                poLogin.launch(loIntent);
            }

            @Override
            public void isSessionEnded() {
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