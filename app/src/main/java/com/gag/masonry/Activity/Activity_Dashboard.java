package com.gag.masonry.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.gag.masonry.Adapter.Adapter_Drawer;
import com.gag.masonry.R;
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

    private DrawerLayout main_drawer;
    private FrameLayout layout_container;
    private MaterialToolbar toolbar;
    private ExpandableListView list_menus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        poMessage = new Message_Dialog(this);
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

    }

    public void InitListener(){

        ActionBarDrawerToggle toggleDrawer = new ActionBarDrawerToggle(
                this, main_drawer, toolbar,
                org.gag.appdriver.R.string.open_drawer, org.gag.appdriver.R.string.close_drawer);
        main_drawer.addDrawerListener(toggleDrawer);
        toggleDrawer.syncState();

        list_menus.setOnGroupClickListener((parent, v, groupPosition, id) -> {
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

            Log.d("TAG", "Clicked child: " + childItem.getFsIDxx() + " : " + childItem.getFsTitlex());

            String itemID = childItem.getFsIDxx();
            switch (itemID){

                case "ACC001": //update account
                    break;
                case "ACC003": //logout account
                    break;
            }

            return true; // consume click
        });

    }
}