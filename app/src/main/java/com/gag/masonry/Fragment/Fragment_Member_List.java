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

import com.gag.accounting.Fragments.Receipt.Fragment_Issued_Receipts;
import com.gag.accounting.Fragments.Receipt.Fragment_Receipt_Entry;
import com.gag.masonry.R;
import com.gag.useraccount.ViewModel.VM_Member;
import com.google.android.material.textfield.TextInputEditText;

import org.gag.appdriver.App.Adapters.Adapter_Member_List;
import org.gag.appdriver.App.Fragments.Fragment_Child_Container;
import org.gag.appdriver.Room.Entities.EMemberInfo;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Member_List extends Fragment {

    private VM_Member mviewModel;
    private Adapter_Member_List loMemberAdaoter;
    private String lsLodgeIDxx, lsStackIDxx;

    private TextInputEditText tie_search;
    private ConstraintLayout layout_no_record;
    private RecyclerView rcv_lodge_members;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment__member__list, container, false);

        mviewModel = new ViewModelProvider(requireActivity()).get(VM_Member.class);
        lsLodgeIDxx = getArguments() == null ? "" : getArguments().getString("lodge_id");

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackIDxx = entry.getName() == null ? "" : entry.getName();
        }

        InitViews(view);
        InitDataReceiver();
        InitListener();

        return view;
    }

    private void InitViews(View view){

        tie_search = view.findViewById(R.id.tie_search);
        layout_no_record = view.findViewById(R.id.layout_no_record);
        rcv_lodge_members = view.findViewById(R.id.rcv_lodge_members);
    }

    private void InitDataReceiver(){

        mviewModel.ObserverMemberList().observe(getViewLifecycleOwner(), new Observer<List<EMemberInfo>>() {
            @Override
            public void onChanged(List<EMemberInfo> eMemberInfos) {

                if (eMemberInfos == null || eMemberInfos.size() < 1){
                    layout_no_record.setVisibility(View.VISIBLE);
                    rcv_lodge_members.setVisibility(View.GONE);
                    return;
                }
                layout_no_record.setVisibility(View.GONE);
                rcv_lodge_members.setVisibility(View.VISIBLE);

                //filter members for the lodge only
                List<EMemberInfo> laLodgeMembers = new ArrayList<>();
                for (EMemberInfo loMember : eMemberInfos){

                    if (loMember.getSLodgeIDx().equalsIgnoreCase(lsLodgeIDxx)) laLodgeMembers.add(loMember);
                }

                loMemberAdaoter = new Adapter_Member_List(requireActivity(), laLodgeMembers, new Adapter_Member_List.OnSelect() {
                    @Override
                    public void Selected(EMemberInfo foMember) {

                        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        Bundle loBundle = new Bundle();

                        switch (lsStackIDxx){

                            case "receipt_entry":

                                Fragment_Receipt_Entry loReceiptMembers = new Fragment_Receipt_Entry();
                                loBundle.putString("member_id", foMember.getSMemberID());

                                loReceiptMembers.setArguments(loBundle);

                                //add to child container, as bridge to initiate another fragment after call
                                fragmentTransaction.replace(R.id.layout_container, new Fragment_Child_Container().newInstance("receipt_entry", loReceiptMembers));
                                fragmentTransaction.addToBackStack("receipt_entry");
                                fragmentTransaction.commit();

                                break;
                        }
                    }
                });

                rcv_lodge_members.setAdapter(loMemberAdaoter);
                rcv_lodge_members.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));


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

                loMemberAdaoter.GetFilter().filter(charSequence.toString());
            }
        });
    }

}