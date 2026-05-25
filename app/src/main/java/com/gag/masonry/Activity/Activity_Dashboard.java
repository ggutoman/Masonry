package com.gag.masonry.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.gag.masonry.Adapter.Adapter_Drawer;
import com.gag.masonry.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Activity_Dashboard extends AppCompatActivity {

    private NavigationView nav_view;
    private DrawerLayout main_drawer;
    private FrameLayout layout_container;
    private MaterialToolbar toolbar;
    private ExpandableListView list_menus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        nav_view = findViewById(R.id.nav_view);
        main_drawer = findViewById(R.id.main_drawer);
        layout_container = findViewById(R.id.layout_container);
        toolbar = findViewById(R.id.toolbar);
        list_menus = findViewById(R.id.list_menus);

        ActionBarDrawerToggle toggleDrawer = new ActionBarDrawerToggle(
                this, main_drawer, toolbar,
                org.gag.appdriver.R.string.open_drawer, org.gag.appdriver.R.string.close_drawer);
        main_drawer.addDrawerListener(toggleDrawer);
        toggleDrawer.syncState();

        //list_menus.setAdapter(new Adapter_Drawer(Activity_Dashboard.this, loMenuParent));

    }
}