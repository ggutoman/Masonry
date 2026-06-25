package com.gag.masonry.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.gag.accounting.Fragments.Fund.Fragment_Turnover_Funds;
import com.gag.masonry.Adapter.Adapter_Drawer;
import com.gag.masonry.Fragment.Fragment_Home;
import com.gag.masonry.Fragment.Fragment_Lodge;
import com.gag.masonry.Fragment.Fragment_Officer_history;
import com.gag.masonry.Fragment.Fragment_UserInfo;
import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;
import com.gag.useraccount.Fragments.Fragment_Assign_Officer;

import com.gag.masonry.Fragment.Fragment_Lodge_Calendar_Entry;
import com.gag.useraccount.Fragments.Fragment_Member;
import com.google.android.material.appbar.MaterialToolbar;

import com.gag.masonry.Fragment.Fragment_Lodge_List;
import org.gag.appdriver.Constants.MENU_ITEM_CONSTANTS;
import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS;
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
                case "LDGE0001":
                    InitView("LDGE0001");
                    break;
                case "LDGE0002":
                    InitView("LDGE0002");
                    break;
                case "LDGE0003":
                    InitView("LDGE0003");
                    break;
                case "LDGE0004":
                    InitView("LDGE0004");
                    break;
                case "FND001":
                    InitView("FND001");
                    break;
                case "FND002":
                    InitView("FND002");
                    break;
                case "FND003":
                    InitView("FND003");
                    break;
                case "FND004":
                    InitView("FND004");
                    break;
                case "FND005":
                    InitView("FND005");
                    break;
                case "FND006":
                    InitView("FND006");
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
                case "MEM004":
                    InitView("MEM004");
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
            case "LDGE0001":
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge());
                fragmentTransaction.addToBackStack("lodge_entry");
                break;
            case "LDGE0002":
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_Calendar_Entry());
                fragmentTransaction.addToBackStack("lodge_calendar_entry");
                break;
            case "LDGE0003": //lodge calendars
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("lodge_list");
                break;
            case "LDGE0004": //lodge list
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("lodge_calendar_list");
                break;
            case "MEM002":
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Member());
                fragmentTransaction.addToBackStack("create_member");
                break;
            case "MEM003":
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Assign_Officer());
                fragmentTransaction.addToBackStack("assign_officer");
                break;
            case "MEM004":
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Officer_history());
                fragmentTransaction.addToBackStack("view_officer_history");
                break;
            case "ACC002":
                Fragment_UserInfo loFragUser = new Fragment_UserInfo();

                fragmentTransaction.replace(R.id.layout_container, loFragUser);
                fragmentTransaction.addToBackStack("view_account");
                break;
            case "FND001":
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Turnover_Funds());
                fragmentTransaction.addToBackStack("turnover_funds");
                break;
            case "FND002":
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("annual_due_entry");
                break;
            case "FND003": //lodge fund entries information
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("fund_history");
                break;
            case "FND004": //lodge fund summary information
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("lodge_fund_information");
                break;
            case "FND005":
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("lodge_annual_dues");
                break;
            case "FND006": //lodge annual summary information
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("lodge_annual_information");
                break;

        }
        fragmentTransaction.commit();

    }
}