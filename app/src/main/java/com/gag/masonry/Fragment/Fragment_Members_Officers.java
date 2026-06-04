package com.gag.masonry.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.masonry.Adapter.Adapter_Member_List;
import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;
import com.google.android.material.textfield.TextInputEditText;

import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Utilities.Message_Dialog;

import java.util.List;

public class Fragment_Members_Officers extends Fragment {

    private VM_Main mviewModel;

    private TextInputEditText tie_search;
    private RecyclerView rcv_members;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_members_officers, container, false);

        mviewModel = new ViewModelProvider(requireActivity()).get(VM_Main.class);

        InitViews(view);
        InitDataReceiver();

        return view;
    }

    private void InitViews(View view){
        tie_search = view.findViewById(R.id.tie_search);
        rcv_members = view.findViewById(R.id.rcv_members);
    }

    private void InitDataReceiver(){

        Bundle args = getArguments();

        if (args == null) return;

        if (args.getString("sMemberIDx") == null || args.getString("sMemberIDx").isEmpty()) return;

        //member list tab
        if (args.getInt("nTab") < 1){

            mviewModel.GetMemberList(args.getString("sMemberIDx")).observe(getViewLifecycleOwner(), new Observer<List<EMemberInfo>>() {
                @Override
                public void onChanged(List<EMemberInfo> eMemberInfos) {

                    if (eMemberInfos == null || eMemberInfos.size() < 1){

                        mviewModel.DownloadMembers(new VM_Main.OnDownloadData() {
                            @Override
                            public void OnDownload() { Toast.makeText(requireActivity(), "Downloading members . . .", Toast.LENGTH_SHORT).show(); }

                            @Override
                            public void OnFinished(String fsMessage) {Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();}
                        });
                        return;
                    }

                    rcv_members.setAdapter(new Adapter_Member_List(requireActivity(), eMemberInfos));
                    rcv_members.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));

                }
            });

        }

    }
}