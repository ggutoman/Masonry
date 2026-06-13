package com.gag.masonry.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.gag.accounting.Disbursement.Fragments.Fragment_Turnover_Funds;
import com.gag.masonry.Adapter.Adapter_Drawer;
import com.gag.masonry.Fragment.Fragment_Home;
import com.gag.masonry.Fragment.Fragment_UserInfo;
import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;
import com.gag.useraccount.Activity.Activity_Account;
import com.gag.useraccount.Fragments.Fragment_Assign_Officer;
import com.gag.useraccount.Fragments.Fragment_Lodge;
import com.gag.useraccount.Fragments.Fragment_Member;
import com.google.android.material.appbar.MaterialToolbar;

import org.gag.appdriver.App.Models.MemberDashboardInfo;
import org.gag.appdriver.Constants.MENU_ITEM_CONSTANTS;
import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS;
import org.gag.appdriver.Room.DataObject.DMemberInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.EUserInfo;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_Dashboard extends AppCompatActivity {

    private Message_Dialog poMessage;
    private LoadDialog poLoading;
    private VM_Main mViewmodel;

    private DrawerLayout main_drawer;
    private MaterialToolbar toolbar;
    private ExpandableListView list_menus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        poMessage = new Message_Dialog(this);
        poLoading = new LoadDialog(this);
        mViewmodel = new ViewModelProvider(Activity_Dashboard.this).get(VM_Main.class);

        poMessage.InitDialog();
        poLoading.InitDialog();

        toolbar = findViewById(R.id.toolbar);
        main_drawer = findViewById(R.id.main_drawer);
        list_menus = findViewById(R.id.list_menus);

        InitAdapter();
        InitListener();
        InitView("HME"); //default to home

    }

    public void InitAdapter(){

        ArrayList<MENU_PARENT_CONSTANTS> parentMenu =
                (ArrayList<MENU_PARENT_CONSTANTS>) getIntent().getSerializableExtra("parent_key");

        HashMap<String, ArrayList<MENU_ITEM_CONSTANTS>> parentItem =
                (HashMap<String, ArrayList<MENU_ITEM_CONSTANTS>>) getIntent().getSerializableExtra("child_items");

        HashMap<String, ArrayList<MENU_ITEM_CONSTANTS>> loChildMap = new HashMap<>();
        for (Map.Entry<String, ArrayList<MENU_ITEM_CONSTANTS>> entry : parentItem.entrySet()) {
            loChildMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }


        Adapter_Drawer loAdapter =  new Adapter_Drawer(
                Activity_Dashboard.this,
                parentMenu,
                loChildMap);

        list_menus.setAdapter(loAdapter);
        main_drawer.openDrawer(GravityCompat.START);

    }

    public void InitListener(){

        ActionBarDrawerToggle toggleDrawer = new ActionBarDrawerToggle(
                this, main_drawer, toolbar,
                org.gag.appdriver.R.string.open_drawer, org.gag.appdriver.R.string.close_drawer);
        main_drawer.addDrawerListener(toggleDrawer);
        toggleDrawer.syncState();

        list_menus.setOnGroupClickListener((parent, v, groupPosition, id) -> {

            MENU_PARENT_CONSTANTS groupMenu = (MENU_PARENT_CONSTANTS) list_menus.getExpandableListAdapter()
                                                .getGroup(groupPosition);

            String lsParentId = groupMenu.getFsIDxx();
            if (lsParentId.equalsIgnoreCase("HME")){
                main_drawer.closeDrawer(GravityCompat.START);

                InitView(lsParentId);
                return false;
            }else if (lsParentId.equalsIgnoreCase("LGT")) {


                return false;
            }

            if (list_menus.isGroupExpanded(groupPosition)) {
                list_menus.collapseGroup(groupPosition);
            } else {
                list_menus.expandGroup(groupPosition);
            }

            // return true so the default expand/collapse doesn’t also fire
            return true;
        });

        list_menus.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {

            MENU_ITEM_CONSTANTS childItem = (MENU_ITEM_CONSTANTS) list_menus.getExpandableListAdapter()
                    .getChild(groupPosition, childPosition);

            main_drawer.closeDrawer(GravityCompat.START);

            String itemID = childItem.getFsIDxx();
            switch (itemID){

                case "ACC001": //download data initialized upon login

                    poMessage.ShowMessage(2, "Do you want to re-download data for the app?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                        @Override
                        public void OnPositive(@NotNull AlertDialog poDialog) {
                            poDialog.dismiss();
                        }

                        @Override
                        public void OnNegative(@NotNull AlertDialog poDialog) {
                            poDialog.dismiss();

                            //re download parameters
                            mViewmodel.DownloadParameters(new VM_Main.OnDownloadData() {
                                @Override
                                public void OnDownload() {
                                    poLoading.ShowDialog("Downloading data. Please wait . . .");
                                }

                                @Override
                                public void OnFinished(String fsMessage) {
                                    poLoading.DismissDialog();

                                    poMessage.ShowMessage(0, fsMessage, "Okay", "", new Message_Dialog.OnDialogClick() {
                                        @Override
                                        public void OnPositive(@NotNull AlertDialog poDialog) {
                                            poDialog.dismiss();
                                        }

                                        @Override
                                        public void OnNegative(@NotNull AlertDialog poDialog) {
                                            poDialog.dismiss();
                                        }
                                    });
                                }
                            });
                        }
                    });
                    break;
                case "ACC002": //update view account
                    InitView("ACC002");
                    break;
                case "ACC003": //logout account

                    poMessage.ShowMessage(2, "Confirm Logout?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                        @Override
                        public void OnPositive(@NotNull AlertDialog poDialog) {
                            poDialog.dismiss();
                        }

                        @Override
                        public void OnNegative(@NotNull AlertDialog poDialog) {
                            poDialog.dismiss();

                            Fragment current = getSupportFragmentManager().findFragmentById(R.id.layout_container);
                            if (current != null) {
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .remove(current)
                                        .commitNow(); // ensures fragment is destroyed immediately
                            }

                            Intent loFinish = new Intent();
                            loFinish.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            setResult(Activity_Dashboard.RESULT_OK, loFinish);
                            finish();

                        }
                    });
                    break;
                case "DISB001":
                    InitView("DISB001");
                    break;
                case "MEM001":
                    InitView("MEM001");
                    break;
                case "MEM002":
                    InitView("MEM002");
                    break;
                case "MEM003":
                    InitView("MEM003");
                    break;
            }

            return true;
        });

    }

    public void InitView(String fsItemID){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //get changes from user account, to get the accurate details passed for updating account
        EUserInfo loUser = mViewmodel.GetUserInfo();
        MemberDashboardInfo poMember = mViewmodel.GetMemberInfo(loUser.getSUserIDxx());

        //paramter object for arguments
        Bundle loArgs;
        switch (fsItemID){

            case "HME":
                Fragment_Home loHome = new Fragment_Home();

                loArgs = new Bundle();
                loArgs.putInt("user_level", loUser.getNUserLevl());
                loHome.setArguments(loArgs);

                fragmentTransaction.replace(R.id.layout_container, loHome);
                fragmentTransaction.addToBackStack("home");
                break;
            case "MEM001":
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge());
                fragmentTransaction.addToBackStack("lodge_calendar_entry");
                break;
            case "MEM002":
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Member());
                fragmentTransaction.addToBackStack("create_member");
                break;
            case "MEM003":
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Assign_Officer());
                fragmentTransaction.addToBackStack("assign_officer");
                break;
            case "ACC002":
                Fragment_UserInfo loFragUser = new Fragment_UserInfo();

                fragmentTransaction.replace(R.id.layout_container, loFragUser);
                fragmentTransaction.addToBackStack("view_account");
                break;
            case "DISB001":
                Fragment_Turnover_Funds loTurnOver = new Fragment_Turnover_Funds();

                fragmentTransaction.replace(R.id.layout_container, loTurnOver);
                fragmentTransaction.addToBackStack("turnover_funds");
                break;

        }
        fragmentTransaction.commit();

    }
}