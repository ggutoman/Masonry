package com.gag.masonry.Fragment;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gag.accounting.Fragments.Fund.Fragment_Lodge_Fund;import com.gag.masonry.R;
import com.google.android.material.textfield.TextInputEditText;

import org.gag.appdriver.App.Adapters.LodgeAdapterList;
import org.gag.appdriver.App.Fragments.Fragment_Child_Container;
import org.gag.appdriver.App.ViewModels.VM_Lodge;
import org.gag.appdriver.Room.Entities.ELodgeInfo;

import java.util.List;

public class Fragment_Lodge_List extends Fragment {

    private VM_Lodge mViewModel;
    private LodgeAdapterList loAdapter;

    private ConstraintLayout layout_no_record;
    private TextInputEditText tie_search;
    private RecyclerView rcv_lodge_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lodge_list, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(VM_Lodge.class);

        InitViews(view);
        InitDataReceiver();
        InitListener();

        return view;
    }

    private void InitViews(View view){

        layout_no_record = view.findViewById(R.id.layout_no_record);
        tie_search = view.findViewById(R.id.tie_search);
        rcv_lodge_list = view.findViewById(R.id.rcv_lodge_list);
    }

    private void InitDataReceiver(){

        mViewModel.GetLodgeList().observe(getViewLifecycleOwner(), new Observer<List<ELodgeInfo>>() {
            @Override
            public void onChanged(List<ELodgeInfo> eLodgeInfos) {

                if (eLodgeInfos == null){
                    layout_no_record.setVisibility(View.VISIBLE);
                    rcv_lodge_list.setVisibility(View.GONE);
                    return;
                }
                layout_no_record.setVisibility(View.GONE);
                rcv_lodge_list.setVisibility(View.VISIBLE);

                loAdapter = new LodgeAdapterList(requireActivity(), eLodgeInfos, new LodgeAdapterList.SelectItem() {
                    @Override
                    public void OnSelect(ELodgeInfo lodge) {

                        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();

                        if (count > 0) {

                            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
                            String name = entry.getName() == null ? "" : entry.getName();

                            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                            Bundle loBundle = new Bundle();

                            switch (name) {

                                //view lodge info
                                case "lodge_list":
                                    //should open the lodge information
                                    Fragment_Lodge loLodge = new Fragment_Lodge();

                                    loBundle = new Bundle();
                                    loBundle.putString("lodge_id", lodge.getSLodgeIDx());

                                    loLodge.setArguments(loBundle);

                                    ///add to child container, as bridge to initiate another fragment after call
                                    fragmentTransaction.replace(R.id.layout_container, new Fragment_Child_Container().newInstance("lodge_info", loLodge));
                                    fragmentTransaction.addToBackStack("lodge_info");

                                    break;

                                //view lodge fund summary
                                case "lodge_fund_information":

                                    Fragment_Lodge_Fund loFund = new Fragment_Lodge_Fund();

                                    loBundle = new Bundle();
                                    loBundle.putString("lodge_id", lodge.getSLodgeIDx());

                                    loFund.setArguments(loBundle);

                                    //add to child container, as bridge to initiate another fragment after call
                                    fragmentTransaction.replace(R.id.layout_container, new Fragment_Child_Container().newInstance("lodge_fund_info", loFund));
                                    fragmentTransaction.addToBackStack("lodge_fund_info");
                                    break;

                                //view lodge calendar list
                                case "lodge_calendar_list":
                                    //should open the lodge information
                                    Fragment_Lodge_Calendar_List loCalendarlist = new Fragment_Lodge_Calendar_List();

                                    loBundle = new Bundle();
                                    loBundle.putString("lodge_id", lodge.getSLodgeIDx());

                                    loCalendarlist.setArguments(loBundle);

                                    ///add to child container, as bridge to initiate another fragment after call
                                    fragmentTransaction.replace(R.id.layout_container, new Fragment_Child_Container().newInstance("lodge_calendars", loCalendarlist));
                                    fragmentTransaction.addToBackStack("lodge_calendars");

                                    break;

                                //view lodge fund entries
                                case "fund_history":
                                    Fragment_Lodge_Calendar_List loCalendars = new Fragment_Lodge_Calendar_List();

                                    loBundle = new Bundle();
                                    loBundle.putString("lodge_id", lodge.getSLodgeIDx());

                                    loCalendars.setArguments(loBundle);

                                    //add to child container, as bridge to initiate another fragment after call
                                    fragmentTransaction.replace(R.id.layout_container, new Fragment_Child_Container().newInstance("lodge_funds", loCalendars));
                                    fragmentTransaction.addToBackStack("lodge_funds");

                                //view lodge year annual due entries
                                case "lodge_annual_dues":

                                    Fragment_Lodge_Calendar_List loAnnuals = new Fragment_Lodge_Calendar_List();

                                    loBundle = new Bundle();
                                    loBundle.putString("lodge_id", lodge.getSLodgeIDx());

                                    loAnnuals.setArguments(loBundle);

                                    //add to child container, as bridge to initiate another fragment after call
                                    fragmentTransaction.replace(R.id.layout_container, new Fragment_Child_Container().newInstance("lodge_annual_dues_info", loAnnuals));
                                    fragmentTransaction.addToBackStack("lodge_annual_dues_info");
                                    break;

                            }
                            fragmentTransaction.commit();
                        }
                    }
                });
                rcv_lodge_list.setAdapter(loAdapter);
                rcv_lodge_list.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
            }
        });
    }

    private void InitListener(){

        tie_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (loAdapter == null) return;

                loAdapter.GetFilter().filter(charSequence.toString());
            }
        });
    }
}