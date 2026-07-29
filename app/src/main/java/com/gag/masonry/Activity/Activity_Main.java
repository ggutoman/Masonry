package com.gag.masonry.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

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

import org.gag.appdriver.App.Models.MemberDashboardInfo;
import org.gag.appdriver.Constants.MENU_ITEM_CONSTANTS;
import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS;
import org.gag.appdriver.Room.DataObject.DMemberInfo;
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

    // Initialize the launcher
    private final ActivityResultLauncher<String[]> poPermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {

            boolean isAllGranted = true;
            for (Map.Entry<String, Boolean> entry : result.entrySet()) {

                Boolean isGranted = entry.getValue();
                if (!isGranted) {
                    isAllGranted = false;
                    break;
                }
            }

            if (!isAllGranted){
                System.exit(0);
            }else {
                InitData();
            }
        }
    );

    private final ActivityResultLauncher<Intent> poLogin = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult loIntent) {

                    //this is to ensure that the token return from server is fully initialzed after successful login
                    if (loIntent.getResultCode() == Activity.RESULT_OK) {
                        InitData();
                    }
                }
    });

    private final ActivityResultLauncher<Intent> poLogout = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult loIntent) {

                    //this is logout receiver from dashboard
                    if (loIntent.getResultCode() == Activity.RESULT_OK) {
                        mviewModel.EndSession();
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

        poMessage.ShowMessage(2, "This application requires permissions for internet usage, access camera and would read and write to device storage." +
                        "\nGranting these permissions ensures the app functions properly and provides the intended experience.",
                "Decline", "Accept",
                new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                        System.exit(0);
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                        InitPermissions();
                    }
                }
        );
    }

    private void InitPermissions(){

        List<String> laPermissions = new ArrayList<>();

        if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            laPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }else if (checkSelfPermission(Manifest.permission.CHANGE_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            laPermissions.add(Manifest.permission.CHANGE_NETWORK_STATE);
        }else if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            laPermissions.add(Manifest.permission.INTERNET);
        }else if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            laPermissions.add(Manifest.permission.CAMERA);
        }else if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            laPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }else if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            laPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        poPermissions.launch(laPermissions.toArray(new String[0]));
    }

    private void InitData(){

        mviewModel.InitData(new VM_Main.InitData() {
            @Override
            public void isLoading() { poLoad.ShowDialog("Initializing data. Please wait . . ."); }

            @Override
            public void hasLoggedIn() {

                mviewModel.ObserveUserInfo().observe(Activity_Main.this, new Observer<EUserInfo>() {
                    @Override
                    public void onChanged(EUserInfo eUserInfo) {

                        poLoad.DismissDialog();

                        if (mviewModel.ObserveUserInfo() == null || mviewModel.GetLodgeInfo() == null){
                            Intent loIntent = new Intent(Activity_Main.this, Activity_Login.class);
                            loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            poLogin.launch(loIntent);
                            return;
                        }

                        //start syncing user data
                        Intent loIntent = new Intent("LOGIN_ACTION");
                        loIntent.putExtra("user_id", eUserInfo.getSUserIDxx());
                        sendBroadcast(loIntent);

                        //verify member information, it would be the basis of displaying menus
                        mviewModel.ObserveMemberInfo().observe(Activity_Main.this, new Observer<MemberDashboardInfo>() {
                            @Override
                            public void onChanged(MemberDashboardInfo memberDashboardInfo) {

                                if (memberDashboardInfo == null) return;

                                if (eUserInfo == null) return;

                                /**DOUBLE CHECK CREDENTIALS. ALTHOUGH IT IS ALREADY VALIDATED ON SERVER SIDE**/

                                //initialize user level based on matching credentials, if mismatch then hide menus
                                int fnUserLevel;
                                if (!eUserInfo.getSGLPIDNoX().equalsIgnoreCase(memberDashboardInfo.getSGLPIDNoX()) ||
                                        !eUserInfo.getSLastName().equalsIgnoreCase(memberDashboardInfo.getSLastName()) ||
                                        !eUserInfo.getDBirthDte().equalsIgnoreCase(memberDashboardInfo.getDBirthDte()) ){

                                    //shows only update account and logout
                                    fnUserLevel = 0;

                                }else {
                                    fnUserLevel = eUserInfo.getNUserLevl();
                                }

                                //initialze drawer menus
                                List<MENU_PARENT_CONSTANTS> faParentMenu = mviewModel.GetParentMenu(fnUserLevel);

                                HashMap<String, List<MENU_ITEM_CONSTANTS>> faParentItems = new HashMap<>();
                                for (MENU_PARENT_CONSTANTS entries : faParentMenu){

                                    faParentItems.put(entries.getFsIDxx(), mviewModel.GetMenuItem(fnUserLevel, entries.getFsIDxx()));
                                }

                                // Ensure data is passed as ArrayList to match expected types in Activity_Dashboard/Adapter_Drawer
                                ArrayList<MENU_PARENT_CONSTANTS> laParentList = new ArrayList<>(faParentMenu);

                                HashMap<String, ArrayList<MENU_ITEM_CONSTANTS>> loChildMap = new HashMap<>();
                                for (Map.Entry<String, List<MENU_ITEM_CONSTANTS>> entry : faParentItems.entrySet()) {
                                    loChildMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
                                }

                                Intent loIntent = new Intent(Activity_Main.this, Activity_Dashboard.class);
                                loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //clear existing activities
                                loIntent.putExtra("parent_key", laParentList); //parent menus
                                loIntent.putExtra("child_items", loChildMap); //parent items

                                poLogout.launch(loIntent);
                            }
                        });
                    }
                });
            }

            @Override
            public void isLoginNeeded() {
                poLoad.DismissDialog();

                Intent loIntent = new Intent(Activity_Main.this, Activity_Login.class);
                loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

                                //clear session before login
                                mviewModel.EndSession();

                                Intent loIntent = new Intent(Activity_Main.this, Activity_Login.class);
                                loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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