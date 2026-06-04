package com.gag.masonry.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.gag.masonry.Adapter.Adapter_Home_Tab;
import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Room.DataObject.DMemberInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Utilities.Message_Dialog;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Home extends Fragment {

    private VM_Main mviewModel;
    private Message_Dialog poMessage;

    private MaterialTextView mtv_username, mtv_position, mtv_lodge;
    private TabLayout tab_layout;
    private ViewPager2 vpage_tab_data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_home, container, false);

        mviewModel = new ViewModelProvider(requireActivity()).get(VM_Main.class);
        poMessage = new Message_Dialog(requireActivity());

        poMessage.InitDialog();

        InitViews(view);
        InitDataReceiver();

        return view;
    }

    private void InitViews(View view){

        mtv_username = view.findViewById(R.id.mtv_username);
        mtv_position = view.findViewById(R.id.mtv_position);
        mtv_lodge = view.findViewById(R.id.mtv_lodge);

        tab_layout = view.findViewById(R.id.tab_layout);
        vpage_tab_data = view.findViewById(R.id.vpage_tab_data);
    }

    private void InitDataReceiver(){

        mviewModel.GetMemberInfo().observe(getViewLifecycleOwner(), new Observer<DMemberInfo.MemberDashboardInfo>() {
            @Override
            public void onChanged(DMemberInfo.MemberDashboardInfo eMemberInfo) {

                if (eMemberInfo == null) return;

                mtv_username.setText(eMemberInfo.getSMemberNm());
                mtv_lodge.setText(eMemberInfo.getSLodgeNme());

                Fragment loFragmentMembers = new Fragment_Members_Officers();
                Fragment loFragmentOfficers = new Fragment_Members_Officers();

                //fragment members bundle
                Bundle loBundle1 = new Bundle();
                loBundle1.putInt("nTab", 0);
                loBundle1.putString("sMemberIDx", eMemberInfo.getSMemberID());
                loFragmentMembers.setArguments(loBundle1);

                //fragment officers bundle
                Bundle loBundle2 = new Bundle();
                loBundle2.putInt("nTab", 1);
                loBundle2.putString("sMemberIDx", eMemberInfo.getSMemberID());
                loFragmentOfficers.setArguments(loBundle2);

                List<Fragment> laFragments = new ArrayList<>();
                laFragments.add(loFragmentMembers);
                laFragments.add(loFragmentOfficers);

                vpage_tab_data.setAdapter(
                        new Adapter_Home_Tab(
                                requireActivity(),
                                laFragments
                        )
                );
            }
        });
    }
}