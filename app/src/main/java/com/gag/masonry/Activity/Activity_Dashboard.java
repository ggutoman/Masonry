package com.gag.masonry.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.gag.accounting.Fragments.Disbursement.Fragment_Disbursement;
import com.gag.accounting.Fragments.Disbursement.Fragment_Disbursement_History;
import com.gag.accounting.Fragments.Fund.Fragment_Turnover_Funds;
import com.gag.accounting.Fragments.Receipt.Fragment_Issued_Receipts;
import com.gag.masonry.Adapter.Adapter_Drawer_Expandable;
import com.gag.masonry.Fragment.Fragment_Home;
import com.gag.masonry.Fragment.Fragment_Officer_history;
import com.gag.masonry.Fragment.Fragment_UserInfo;
import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;
import com.gag.useraccount.Fragments.Fragment_Assign_Officer;
import com.gag.useraccount.Fragments.Fragment_Member;
import com.google.android.material.appbar.MaterialToolbar;

import com.gag.masonry.Fragment.Fragment_Lodge_List;

import org.gag.appdriver.App.Fragments.Fragment_Child_Container;
import org.gag.appdriver.Room.Entities.EUserInfo;
import org.gag.appdriver.App.Fragments.Fragment_Attachment_List;
import org.gag.appdriver.Room.Entities.EUserObjects;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
        InitView("000"); //default to home

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

        Adapter_Drawer_Expandable loAdapter =  new Adapter_Drawer_Expandable(Activity_Dashboard.this, parentMenu, new Adapter_Drawer_Expandable.SelectMenu() {
            @Override
            public void OnSelect(EUserObjects foObject, int fnPosition) {

                switch (foObject.getIdxx()){

                    case "001", "002", "003", "004": //Member, Accounting, Projects, Settings
                        CollapseGroup(fnPosition);
                        break;
                    case "030": //Sync Data
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
                                        Toast.makeText(Activity_Dashboard.this, fsMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        break;
                    case "031": //Logout
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

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //get changes from user account, to get the accurate details passed for updating account
        EUserInfo loUser = mViewmodel.GetUserInfo();

        //close drawer
        main_drawer.closeDrawer(GravityCompat.START);

        //paramter object for arguments
        Bundle loArgs;
        switch (fsItemID){

            case "000":
                Fragment_Home loHome = new Fragment_Home();

                loArgs = new Bundle();
                loArgs.putInt("user_level", loUser.getNUserLevl());
                loHome.setArguments(loArgs);

                fragmentTransaction.replace(R.id.layout_container, loHome);
                fragmentTransaction.addToBackStack("home");
                break;
            case "029": //account info
                Fragment_UserInfo loFragUser = new Fragment_UserInfo();

                fragmentTransaction.replace(R.id.layout_container, loFragUser);
                fragmentTransaction.addToBackStack("view_account");
                break;
            case "005":
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Member());
                fragmentTransaction.addToBackStack("create_member");
                break;
            case "006":
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Assign_Officer());
                fragmentTransaction.addToBackStack("assign_officer");
                break;
            case "MEM004":
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Officer_history());
                fragmentTransaction.addToBackStack("view_officer_history");
                break;
            case "ACC002": //reports
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Child_Container().newInstance("generate_report", new Fragment_Attachment_List()));
                fragmentTransaction.addToBackStack("generate_report");
                break;
            case "FND001":
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Turnover_Funds());
                fragmentTransaction.addToBackStack("turnover_funds");
                break;
            case "FND002": //lodge fund entries information
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("fund_history");
                break;
            case "FND003": //lodge fund summary information
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("lodge_fund_information");
                break;
            case "FND004": //lodge fund approval
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("lodge_fund_approval");
                break;
            case "ANNL001": //annual due entry
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("annual_due_entry");
                break;
            case "ANNL002": //annual due information
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("lodge_annual_dues");
                break;
            case "ANNL003": //lodge annual summary information
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("lodge_annual_information");
                break;
            case "ANNL004": //lodge annual approval
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("lodge_annual_approval");
                break;
            case "PROJ001": //project information
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("project_entry");
                break;
            case "PROJ002": //project history
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("project_information");
                break;
            case "PROJ003": //project history
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("project_summary");
                break;
            case "PROJ004": //project status change
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("project_status");
                break;
            case "RCPT001": //receipt entry
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Lodge_List());
                fragmentTransaction.addToBackStack("receipt_entry");
                break;
            case "RCPT002": //receipt list
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Child_Container().newInstance("issued_receipt", new Fragment_Issued_Receipts()));
                fragmentTransaction.addToBackStack("issued_receipt");
                break;
            case "RCPT003": //confirm receipts
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Child_Container().newInstance("receipt_confirm", new Fragment_Issued_Receipts()));
                fragmentTransaction.addToBackStack("receipt_confirm");
                break;
            case "DISB001": //create disbursement
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Child_Container().newInstance("disbursement_entry", new Fragment_Disbursement()));
                fragmentTransaction.addToBackStack("disbursement_entry");
                break;
            case "DISB002": //view disbursements
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Child_Container().newInstance("disbursement_list", new Fragment_Disbursement_History()));
                fragmentTransaction.addToBackStack("disbursement_list");
                break;
            case "DISB003": //view disbursements
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Child_Container().newInstance("disbursement_confirm", new Fragment_Disbursement_History()));
                fragmentTransaction.addToBackStack("disbursement_confirm");
                break;
            case "DISB004": //view dv attachments
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Child_Container().newInstance("disbursement_attachments", new Fragment_Disbursement_History()));
                fragmentTransaction.addToBackStack("disbursement_attachments");
                break;
            default:
                poMessage.ShowMessage(1, "Feature under maintenance. Thankyou for understanding.", "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });

        }
        fragmentTransaction.commit();

    }
}