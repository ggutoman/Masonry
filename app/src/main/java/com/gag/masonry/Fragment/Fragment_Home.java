package com.gag.masonry.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.EUserInfo;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

public class Fragment_Home extends Fragment {

    private VM_Main mviewModel;
    private Message_Dialog poMessage;

    private MaterialTextView mtv_username, mtv_position, mtv_lodge;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.activity_fragment_home, container, false);

        mviewModel = new ViewModelProvider(requireActivity()).get(VM_Main.class);
        poMessage = new Message_Dialog(requireActivity());

        poMessage.InitDialog();

        mtv_username = view.findViewById(R.id.mtv_username);
        mtv_position = view.findViewById(R.id.mtv_position);
        mtv_lodge = view.findViewById(R.id.mtv_lodge);

        mviewModel.GetMemberInfo().observe(getViewLifecycleOwner(), new Observer<EMemberInfo>() {
            @Override
            public void onChanged(EMemberInfo eMemberInfo) {

                if (eMemberInfo == null){

                    poMessage.ShowMessage(1, "Member information not found", "Okay", "", new Message_Dialog.OnDialogClick() {
                        @Override
                        public void OnPositive(@NotNull AlertDialog poDialog) {
                            poDialog.dismiss();
                        }

                        @Override
                        public void OnNegative(@NotNull AlertDialog poDialog) {}
                    });
                    return;
                }

                mtv_username.setText(eMemberInfo.getSMemberNm());
                mtv_position.setText(eMemberInfo.getSPositnCd());
                mtv_lodge.setText(eMemberInfo.getSLodgeIDx());
            }
        });

        return view;
    }
}