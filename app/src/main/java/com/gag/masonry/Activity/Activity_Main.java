package com.gag.masonry.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import org.gag.appdriver.App.Service.DataSyncService;
import org.gag.appdriver.App.Models.MemberDashboardInfo;
import org.gag.appdriver.Room.Entities.EUserInfo;
import org.gag.appdriver.Room.Entities.EUserObjects;
import org.gag.appdriver.Utilities.Disclosure_Dialog;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Activity_Main extends AppCompatActivity {

    private VM_Main mviewModel;
    private Disclosure_Dialog poDisclousre;
    private Message_Dialog poMessage;
    private LoadDialog poLoad;
    private Intent loService;

    // Initialize the launcher
    private final ActivityResultLauncher<String[]> poPermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {

            for (Map.Entry<String, Boolean> entry : result.entrySet()) {

                Boolean isGranted = entry.getValue();
                if (!isGranted) {
                    Log.d("Permission", entry.getKey() + " has been denied");
                    break;
                }
            }
            InitData();
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

                        if (loService != null){
                            stopService(loService);
                        }
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
        poDisclousre = new Disclosure_Dialog(Activity_Main.this);

        poMessage.InitDialog();
        poLoad.InitDialog();
        poDisclousre.InitDialog();

        poDisclousre.ShowDisclosure(org.gag.appdriver.R.drawable.masonry_logo, "This application requires permissions for internet usage, access camera and would read and write to device storage." +
                        "\n\nGranting these permissions ensures the app functions properly and provides the intended experience.",
                "Decline", "Accept",
                new Disclosure_Dialog.OnDialogClick() {
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
        }
        if (checkSelfPermission(Manifest.permission.CHANGE_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            laPermissions.add(Manifest.permission.CHANGE_NETWORK_STATE);
        }

        if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            laPermissions.add(Manifest.permission.INTERNET);
        }

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            laPermissions.add(Manifest.permission.CAMERA);
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            laPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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

                        if (eUserInfo == null){
                            Intent loIntent = new Intent(Activity_Main.this, Activity_Login.class);
                            loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            poLogin.launch(loIntent);
                            return;
                        }

                        //verify member information, it would be the basis of displaying menus
                        mviewModel.ObserveMemberInfo().observe(Activity_Main.this, new Observer<MemberDashboardInfo>() {
                            @Override
                            public void onChanged(MemberDashboardInfo memberDashboardInfo) {

                                try {

                                    if (memberDashboardInfo == null) return;

                                    //execute if sync data is not started
                                    if (!DataSyncService.Companion.isRunning()){

                                        //start syncing user data
                                        loService = new Intent(Activity_Main.this, DataSyncService.class);
                                        loService.setAction("LOGIN_ACTION");
                                        loService.putExtra("user_id", eUserInfo.getSUserIDxx());
                                        loService.putExtra("member_id", memberDashboardInfo.getSMemberID());

                                        startService(loService);
                                    }

                                    mviewModel.GetUserObjects().observe(Activity_Main.this, new Observer<List<EUserObjects>>() {
                                        @Override
                                        public void onChanged(List<EUserObjects> eUserObjects) {

                                            if (eUserObjects == null || eUserObjects.size() < 1) return;

                                            //proceed to account dashboard
                                            Intent loIntent = new Intent(Activity_Main.this, Activity_Dashboard.class);
                                            loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            loIntent.putParcelableArrayListExtra("parent_key", new ArrayList<>(eUserObjects));
                                            loIntent.putExtra("user_id", eUserInfo.getSUserIDxx());
                                            loIntent.putExtra("member_id", memberDashboardInfo.getSMemberID());
                                            loIntent.putExtra("user_level", eUserInfo.getNUserLevl());

                                            poLogout.launch(loIntent);
                                        }
                                    });

                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }
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

                                if (loService != null){
                                    stopService(loService);
                                }
                                mviewModel.EndSession();

                                Intent loIntent = new Intent(Activity_Main.this, Activity_Login.class);
                                loIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                poLogin.launch(loIntent);
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