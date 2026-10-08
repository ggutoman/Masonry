package com.gag.masonry.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.gag.accounting.Fragments.Disbursement.Fragment_Disbursement;
import com.gag.accounting.Fragments.Disbursement.Fragment_Disbursement_History;
import com.gag.accounting.Fragments.Project.Fragment_Project;
import com.gag.accounting.Fragments.Receipt.Fragment_Issued_Receipts;
import com.gag.masonry.Adapter.Adapter_Drawer_Expandable;
import com.gag.masonry.Fragment.Fragment_Home;
import com.gag.masonry.Fragment.Fragment_Lodge_Calendar_List;
import com.gag.masonry.Fragment.Fragment_Member_List;
import com.gag.masonry.Fragment.Fragment_Officer_history;
import com.gag.masonry.Fragment.Fragment_UserInfo;
import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;
import com.gag.useraccount.Fragments.Fragment_Assign_Officer;
import com.gag.useraccount.Fragments.Fragment_Member;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;

import org.gag.appdriver.App.Fragments.Fragment_Child_Container;
import org.gag.appdriver.App.Fragments.Fragment_Report_List;
import org.gag.appdriver.App.Service.DataSyncService;
import org.gag.appdriver.Room.Entities.EUserObjects;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Activity_Dashboard extends AppCompatActivity {

    private Message_Dialog poMessage;
    private LoadDialog poLoading;
    private VM_Main mViewmodel;

    private String lsGLPID, lsUserIDxx;
    private int lnUsrLvl;

    private DrawerLayout main_drawer;
    private ShapeableImageView siv_logo;
    private MaterialToolbar toolbar;
    private ExpandableListView list_menus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        poMessage = new Message_Dialog(this);
        poLoading = new LoadDialog(this);
        mViewmodel = new ViewModelProvider(Activity_Dashboard.this).get(VM_Main.class);

        lsUserIDxx = getIntent().hasExtra("user_id") ? getIntent().getStringExtra("user_id") : "";
        lsGLPID = getIntent().hasExtra("member_id") ? getIntent().getStringExtra("member_id") : "";
        lnUsrLvl = getIntent().hasExtra("user_level") ? getIntent().getIntExtra("user_level", 0) : 0;

        poMessage.InitDialog();
        poLoading.InitDialog();

        toolbar = findViewById(R.id.toolbar);
        siv_logo = findViewById(R.id.siv_logo);
        main_drawer = findViewById(R.id.main_drawer);
        list_menus = findViewById(R.id.list_menus);

        InitAdapter();

        //default home as main screen
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment loFragment = new Fragment_Home();
        Bundle loArgs = new Bundle();

        loArgs.putInt("user_level", lnUsrLvl);
        loFragment.setArguments(loArgs);

        fragmentTransaction.add(R.id.layout_container, new Fragment_Child_Container().newInstance("home", loFragment));
        fragmentTransaction.addToBackStack("home");
        fragmentTransaction.commit();

    }

    public void InitAdapter(){

        //initialize drawer action bar
        ActionBarDrawerToggle toggleDrawer = new ActionBarDrawerToggle(
                this, main_drawer, toolbar,
                org.gag.appdriver.R.string.open_drawer, org.gag.appdriver.R.string.close_drawer);

        main_drawer.addDrawerListener(toggleDrawer);
        toggleDrawer.syncState();

        ArrayList<EUserObjects> parentMenu =
                getIntent().getParcelableArrayListExtra("parent_key");

        //home navigator by default
        siv_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitView("000");
            }
        });

        Adapter_Drawer_Expandable loAdapter =  new Adapter_Drawer_Expandable(Activity_Dashboard.this, parentMenu, new Adapter_Drawer_Expandable.SelectMenu() {
            @Override
            public void OnSelect(EUserObjects foObject, int fnPosition) {

                switch (foObject.getIdxx()){

                    case "001", "002", "003", "004": //Member, Accounting, Projects, Settings
                        CollapseGroup(fnPosition);
                        break;
                    case "023": //reports

                        //close drawer
                        main_drawer.closeDrawer(GravityCompat.START);

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, new Fragment_Report_List());
                        fragmentTransaction.addToBackStack("generate_report");
                        fragmentTransaction.commit();
                        break;
                    case "025": //Sync Data

                        //close drawer
                        main_drawer.closeDrawer(GravityCompat.START);

                        poMessage.ShowMessage(2, "Syncing data will download important resources for your application. Continue?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                            @Override
                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();
                            }

                            @Override
                            public void OnNegative(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();

                                //re download parameters
                                //start syncing user data
                                Intent loService = new Intent(Activity_Dashboard.this, DataSyncService.class);
                                loService.setAction("LOGIN_ACTION");
                                loService.putExtra("user_id", lsUserIDxx);
                                loService.putExtra("member_id", lsGLPID);

                                //restart service
                                stopService(loService);

                                Toast.makeText(Activity_Dashboard.this, "Sync service started . . .", Toast.LENGTH_SHORT).show();
                                startService(loService);
                            }
                        });
                        break;

                    case "026": //Logout

                        //close drawer
                        main_drawer.closeDrawer(GravityCompat.START);

                        poMessage.ShowMessage(2, "Confirm Logout?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                            @Override
                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();
                            }

                            @Override
                            public void OnNegative(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();

                                Fragment current = getSupportFragmentManager().findFragmentById(org.gag.appdriver.R.id.frame_child);
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
                    default:
                        InitView(foObject.getIdxx());
                }
            }
        });

        list_menus.setAdapter(loAdapter);
        main_drawer.openDrawer(GravityCompat.START);

    }

    private void CollapseGroup(int fnPosition){

        if (list_menus.isGroupExpanded(fnPosition)) {
            list_menus.collapseGroup(fnPosition);
        } else {
            list_menus.expandGroup(fnPosition);
        }

    }

    public void InitView(String fsItemID){

        //do not proceed if user level is not initialized
        if (!getIntent().hasExtra("user_level")){
            Toast.makeText(Activity_Dashboard.this, "User level is not initialzed", Toast.LENGTH_SHORT).show();
            return;
        }

        //close drawer
        main_drawer.closeDrawer(GravityCompat.START);

        //paramter object for arguments
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment loFragment;
        Bundle loArgs = new Bundle();;

        String lsStackId = "";

        switch (fsItemID) {

            case "000":
                loFragment = new Fragment_Home();
                loArgs.putInt("user_level", lnUsrLvl);
                loFragment.setArguments(loArgs);

                lsStackId = "home";
                fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, loFragment);
                break;
            case "024": //account info
                loFragment = new Fragment_UserInfo();
                lsStackId = "view_account";
                fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, loFragment);
                break;
            case "005": //create member
                loFragment = new Fragment_Member();
                lsStackId = "create_member";
                fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, loFragment);
                break;
            case "006": //assign officer
                loFragment = new Fragment_Assign_Officer();
                lsStackId = "assign_officer";
                fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, loFragment);
                break;
            case "008":
                loFragment = new Fragment_Officer_history();
                lsStackId = "view_officer_history";
                fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, loFragment);
                break;
            case "009": //project entry
                loFragment = new Fragment_Project();
                loArgs.putString("lodge_id", mViewmodel.GetLodgeId());
                loFragment.setArguments(loArgs);

                lsStackId = "project_entry";
                fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, loFragment);
                break;
            case "010": //project history
                loFragment = new Fragment_Lodge_Calendar_List();
                loArgs.putString("lodge_id", mViewmodel.GetLodgeId());
                loFragment.setArguments(loArgs);

                lsStackId = "project_information";
                fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, loFragment);
                break;
            case "016", "007": //receipt entry, view members
                loFragment = new Fragment_Member_List();
                loArgs.putString("lodge_id", mViewmodel.GetLodgeId());
                loArgs.putString("user_id", lsGLPID);
                loArgs.putInt("user_level", lnUsrLvl);
                loFragment.setArguments(loArgs);

                if (fsItemID.equalsIgnoreCase("016")){
                    lsStackId = "receipt_entry";
                }else if (fsItemID.equalsIgnoreCase("007")){
                    lsStackId = "view_members";
                }
                fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, loFragment);

                break;
            case "018", "017": //confirm receipts or list of receipts
                loFragment = new Fragment_Issued_Receipts();
                loArgs.putString("lodge_id", mViewmodel.GetLodgeId());
                loFragment.setArguments(loArgs);

                lsStackId = fsItemID.equalsIgnoreCase("017") ? "receipt_confirm" : "issued_receipt";
                fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, loFragment);
                break;
            case "020": //create disbursement
                loFragment = new Fragment_Disbursement();
                loArgs.putString("lodge_id", mViewmodel.GetLodgeId());
                loFragment.setArguments(loArgs);

                lsStackId= "disbursement_entry";
                fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, loFragment);
                break;
            case "021", "022": //view & confirm disbursements
                loFragment = new Fragment_Disbursement_History();
                loArgs.putString("lodge_id", mViewmodel.GetLodgeId());
                loFragment.setArguments(loArgs);

                lsStackId = fsItemID.equalsIgnoreCase("021") ? "disbursement_confirm" : "disbursement_list";
                fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, loFragment);
                break;
            default:
                poMessage.ShowMessage(1, "Feature under maintenance. Thankyou for understanding.", "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {
                    }
                });

        }
        fragmentTransaction.addToBackStack(lsStackId);
        fragmentTransaction.commit();

    }
}