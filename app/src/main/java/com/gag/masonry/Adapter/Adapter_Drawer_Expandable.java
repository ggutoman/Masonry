package com.gag.masonry.Adapter;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.masonry.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Constants.MENU_ICONS;
import org.gag.appdriver.Room.Entities.EUserObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Adapter_Drawer_Expandable extends BaseExpandableListAdapter {

    private final Context loInstance;
    private final ArrayList<EUserObjects> paMenus;

    private final ArrayList<EUserObjects> paParentList = new ArrayList<>();
    private final HashMap<String, ArrayList<EUserObjects>> paChildList = new HashMap<>();
    private final SelectMenu poCallback;

    public interface SelectMenu{
        void OnSelectItem(EUserObjects foObject, int fnPosition);
    }

    public Adapter_Drawer_Expandable(Context foinstance, ArrayList<EUserObjects> faMenus, SelectMenu foCallback){

        this.loInstance = foinstance;
        this.paMenus = faMenus;
        this.poCallback = foCallback;

        for (EUserObjects loMenus : paMenus){

            if (loMenus.getHasParentxx().equalsIgnoreCase("0")){ //add as root menu
                paParentList.add(loMenus);
            }else { //map to root menu

                //map if the parent id matches any from the added child parents
                paParentList
                        .stream()
                        .filter(
                                eUserObjects -> eUserObjects.getIdxx().equalsIgnoreCase(loMenus.getSParentIDxx())
                        )
                        .findAny()
                        .ifPresent(
                                eUserObjects -> {

                                    //if found, then initialize w/ new list when not empty otherwise add to existing list from key
                                    List<EUserObjects> laChildItems = new ArrayList<>();
                                    if (paChildList.containsKey(loMenus.getSParentIDxx())) laChildItems = paChildList.get(loMenus.getSParentIDxx());

                                    laChildItems.add(loMenus);
                                    paChildList.put(loMenus.getSParentIDxx(), new ArrayList<>(laChildItems));
                                }
                        );
            }
        }
    }

    @Override
    public EUserObjects getGroup(int i) {
        return paParentList.get(i);
    }

    @Override
    public int getGroupCount() {
        return paParentList.size();
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public EUserObjects getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(paChildList.get(paParentList.get(groupPosition).getIdxx())).get(childPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
       return Objects.requireNonNull(paChildList.get(paParentList.get(groupPosition).getIdxx())).size();
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        if (view == null){

            view = LayoutInflater.from(loInstance).inflate(R.layout.list_menu_parent, viewGroup, false);
        }

        MaterialTextView mtv_title = view.findViewById(R.id.mtv_title);
        ShapeableImageView img_icon = view.findViewById(R.id.img_icon);

        mtv_title.setText(paParentList.get(i).getSDescriptxx());
        img_icon.setImageResource(MENU_ICONS.Companion.fromId(paParentList.get(i).getIdxx()) == null ? org.gag.appdriver.R.drawable.baseline_error_24 : MENU_ICONS.Companion.fromId(paParentList.get(i).getIdxx()).getFnIconxx());

        //capture selection
        view.setOnClickListener(view1 -> poCallback.OnSelectItem(paParentList.get(i), i));

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        if (view == null) view = LayoutInflater.from(loInstance).inflate(R.layout.list_menu_item, viewGroup, false);

        MaterialTextView mtv_item = view.findViewById(R.id.mtv_item);
        RecyclerView rcv_child_items = view.findViewById(R.id.rcv_child_items);

        mtv_item.setText(getChild(i, i1).getSDescriptxx());

        mtv_item.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {

                if (rcv_child_items.getVisibility() == View.GONE){

                    List<EUserObjects> childItems = new ArrayList<>();
                    for (EUserObjects loMenus : paMenus){
                        if (loMenus.getSParentIDxx().equalsIgnoreCase(getChild(i, i1).getIdxx())) childItems.add(loMenus);
                    }

                    Adapter_Drawer loAdapter = new Adapter_Drawer(childItems, new Adapter_Drawer.SelectMenu() {
                        @Override
                        public void OnSelectItem(EUserObjects loObject) {
                            //capture selection of child
                            poCallback.OnSelectItem(loObject, childItems.indexOf(loObject));
                        }
                    });

                    rcv_child_items.setAdapter(loAdapter);
                    rcv_child_items.setLayoutManager(new LinearLayoutManager(loInstance, LinearLayoutManager.VERTICAL, false));
                    rcv_child_items.setVisibility(View.VISIBLE);

                    return;
                }
                rcv_child_items.setVisibility(View.GONE);

            }
        });

        return view;
    }
}