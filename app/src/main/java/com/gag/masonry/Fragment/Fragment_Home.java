package com.gag.masonry.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Room.DataObject.DMemberInfo;
import org.gag.appdriver.Utilities.Message_Dialog;

public class Fragment_Home extends Fragment {

    private VM_Main mviewModel;
    private Message_Dialog poMessage;

    private MaterialTextView mtv_username, mtv_position, mtv_lodge;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_home, container, false);

        mviewModel = new ViewModelProvider(requireActivity()).get(VM_Main.class);
        poMessage = new Message_Dialog(requireActivity());

        poMessage.InitDialog();

        mtv_username = view.findViewById(R.id.mtv_username);
        mtv_position = view.findViewById(R.id.mtv_position);
        mtv_lodge = view.findViewById(R.id.mtv_lodge);

        mviewModel.GetMemberInfo().observe(getViewLifecycleOwner(), new Observer<DMemberInfo.MemberDashboardInfo>() {
            @Override
            public void onChanged(DMemberInfo.MemberDashboardInfo eMemberInfo) {

                if (eMemberInfo == null) return;

                mtv_username.setText(eMemberInfo.getSMemberNm());
                mtv_lodge.setText(eMemberInfo.getSLodgeNme());
            }
        });

        return view;
    }
}