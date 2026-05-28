package com.gag.masonry.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.gag.masonry.Adapter.Adapter_Drawer;
import com.gag.masonry.Fragment.Fragment_Home;
import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;
import com.gag.useraccount.Activity.Activity_Account;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import org.gag.appdriver.Constants.MENU_ITEM_CONSTANTS;
import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_Dashboard extends AppCompatActivity {

    private Message_Dialog poMessage;
    private VM_Main mviewModel;

    private DrawerLayout main_drawer;
    private MaterialToolbar toolbar;
    private ExpandableListView list_menus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        poMessage = new Message_Dialog(this);
        mviewModel = new ViewModelProvider(Activity_Dashboard.this).get(VM_Main.class);

        poMessage.InitDialog();

        toolbar = findViewById(R.id.toolbar);
        main_drawer = findViewById(R.id.main_drawer);
        list_menus = findViewById(R.id.list_menus);

        if (!getIntent().hasExtra("parent_key") || !getIntent().hasExtra("child_items")){

            poMessage.ShowMessage(1, "Sorry! An error occured while loading data. Application exits.", "Okay", "", new Message_Dialog.OnDialogClick() {
                @Override
                public void OnPositive(@NotNull AlertDialog poDialog) {
                    System.exit(0);
                }

                @Override
                public void OnNegative(@NotNull AlertDialog poDialog) {}
            });
            return;
        }

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
                InitView(lsParentId);
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

            String itemID = childItem.getFsIDxx();
            switch (itemID){

                case "ACC001": //update account
                    Intent loIntent = new Intent(Activity_Dashboard.this, Activity_Account.class);
                    loIntent.putExtra("update", true);
                    startActivity(loIntent);

                    break;
                case "ACC003": //logout account

                    poMessage.ShowMessage(2, "Confirm Logout?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                        @Override
                        public void OnPositive(@NotNull AlertDialog poDialog) { poDialog.dismiss(); }

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

                            mviewModel.EndSession();

                            Intent loFinish = new Intent();
                            setResult(Activity_Dashboard.RESULT_OK, loFinish);
                            finish();

                        }
                    });
                    break;
            }

            return true;
        });

    }

    public void InitView(String fsItemID){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (fsItemID){

            case "HME":
                fragmentTransaction.replace(R.id.layout_container, new Fragment_Home());
                break;
        }
        fragmentTransaction.commitNow();

    }
}