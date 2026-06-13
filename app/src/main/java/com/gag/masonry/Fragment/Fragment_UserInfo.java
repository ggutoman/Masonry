package com.gag.masonry.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.gag.masonry.Activity.Activity_Dashboard;
import com.gag.masonry.Adapter.Adapter_MemberInfoList;
import com.gag.masonry.Adapter.Adapter_Member_List;
import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;
import com.gag.useraccount.Activity.Activity_Account;
import com.gag.useraccount.Fragments.Fragment_Member;
import com.gag.useraccount.ViewModel.VM_Member;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.MemberDashboardInfo;
import org.gag.appdriver.App.Models.OfficerInfo;
import org.gag.appdriver.App.Models.TownProvince;
import org.gag.appdriver.Constants.MEMBER_CONSTANTS;
import org.gag.appdriver.Constants.MENU_ITEM_CONSTANTS;
import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS;
import org.gag.appdriver.Libraries.DateUtil.DateRepository;
import org.gag.appdriver.Room.DataObject.DMemberInfo;
import org.gag.appdriver.Room.Entities.EMemberContactInfo;
import org.gag.appdriver.Room.Entities.EMemberEmailInfo;
import org.gag.appdriver.Room.Entities.EOfficer;
import org.gag.appdriver.Room.Entities.EUserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragment_UserInfo extends Fragment {

    private VM_Main mViewModel;
    private HashMap<String, ArrayList<String>> laMemberInfoOthers;
    private String lsMemberID;

    //User Information
    private ShapeableImageView siv_status;
    private MaterialTextView mtv_name, mtv_user_level, mtv_glpid;

    // Member Information card
    private MaterialTextView mtv_lodge, mtv_title, mtv_status, mtv_membership, mtv_sponsors;

    //Officer Information
    private MaterialCardView card_officer;
    private MaterialTextView mtv_term, mtv_position, mtv_type, mtv_off_status, mtv_label_officer;
    private MaterialButton btn_view_officer;

    // Personal Information card
    private MaterialTextView mtv_firstname, mtv_lastname, mtv_middlename, mtv_suffix, mtv_birthdate, mtv_civilstatus;

    // Other controls
    private MaterialButton btn_edit_account, btn_edit_member;
    private ExpandableListView rcv_list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__user_info, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(VM_Main.class);

        InitViews(view);
        InitDataReceivers();
        InitListener();

        return view;
    }

    private void InitViews(View view){

        //User information
        mtv_name = view.findViewById(R.id.mtv_name);
        siv_status = view.findViewById(R.id.siv_status);
        mtv_user_level = view.findViewById(R.id.mtv_user_level);
        mtv_glpid = view.findViewById(R.id.mtv_glpid);

        // Member Information card
        mtv_lodge = view.findViewById(R.id.mtv_lodge);
        mtv_title = view.findViewById(R.id.mtv_title);
        mtv_status = view.findViewById(R.id.mtv_status);
        mtv_membership = view.findViewById(R.id.mtv_membership);
        mtv_sponsors = view.findViewById(R.id.mtv_sponsors);

        //Officer Information
        mtv_label_officer = view.findViewById(R.id.mtv_label_officer);
        card_officer = view.findViewById(R.id.card_officer);
        mtv_term = view.findViewById(R.id.mtv_term);
        mtv_position = view.findViewById(R.id.mtv_position);
        mtv_type = view.findViewById(R.id.mtv_type);
        mtv_off_status = view.findViewById(R.id.mtv_off_status);
        btn_view_officer = view.findViewById(R.id.btn_view_officer);

        // Personal Information card
        mtv_firstname = view.findViewById(R.id.mtv_firstname);
        mtv_lastname = view.findViewById(R.id.mtv_lastname);
        mtv_middlename = view.findViewById(R.id.mtv_middlename);
        mtv_suffix = view.findViewById(R.id.mtv_suffix);
        mtv_birthdate = view.findViewById(R.id.mtv_birthdate);
        mtv_civilstatus = view.findViewById(R.id.mtv_civilstatus);

        // Other controls
        btn_edit_account = view.findViewById(R.id.btn_edit_account);
        btn_edit_member = view.findViewById(R.id.btn_edit_member);
        rcv_list = view.findViewById(R.id.rcv_list);

    }

    private void InitDataReceivers(){

        //observe user information
        mViewModel.ObserveUserInfo().observe(getViewLifecycleOwner(), new Observer<EUserInfo>() {
            @Override
            public void onChanged(EUserInfo eUserInfo) {

                if (eUserInfo == null) return;

                mtv_name.setText(eUserInfo.getSUserName().toUpperCase());
                mtv_glpid.setText(eUserInfo.getSGLPIDNoX());

                switch (eUserInfo.getCRecdStat()){

                    case "0":
                        siv_status.setImageResource(org.gag.appdriver.R.drawable.baseline_inactive);
                        break;
                    case "1":
                        siv_status.setImageResource(org.gag.appdriver.R.drawable.baseline_active);
                        break;
                    case "2":
                        siv_status.setImageResource(org.gag.appdriver.R.drawable.baseline_suspended);
                        break;
                }

                switch (eUserInfo.getNUserLevl()){

                    case 1:
                        mtv_user_level.setText("User Account");
                        break;
                    case 2:
                        mtv_user_level.setText("Admin Account");
                        break;
                    case 4:
                        mtv_user_level.setText("Owner Account");
                        break;
                }

                //hide edit of membership if user level
                if (eUserInfo.getNUserLevl() < 2) btn_edit_member.setVisibility(View.GONE);
            }
        });

        //observe member information
        mViewModel.ObserveMemberInfo().observe(getViewLifecycleOwner(), new Observer<MemberDashboardInfo>() {
            @Override
            public void onChanged(MemberDashboardInfo memberDashboardInfo) {

                if (memberDashboardInfo == null) return;

                List<MEMBER_CONSTANTS> GetMemberStatus = List.of(MEMBER_CONSTANTS.STATUS_INACTIVE, MEMBER_CONSTANTS.STATUS_ACTIVE, MEMBER_CONSTANTS.STATUS_SUSPENDED);
                List<MEMBER_CONSTANTS> GetMemberCivil = List.of(MEMBER_CONSTANTS.STATUS_SINGLE, MEMBER_CONSTANTS.STATUS_MARRIED, MEMBER_CONSTANTS.STATUS_WIDOWED, MEMBER_CONSTANTS.STATUS_SEPARATED);

                String lsSponsors = (memberDashboardInfo.getSSponsor1() == null ? "" : memberDashboardInfo.getSSponsor1()) +
                                        (memberDashboardInfo.getSSponsor2() == null ? "" : "\n" + memberDashboardInfo.getSSponsor2()) +
                                        (memberDashboardInfo.getSSponsor3() == null ? "" : "\n" + memberDashboardInfo.getSSponsor3());

                mtv_lodge.setText(memberDashboardInfo.getSLodgeNme());
                mtv_title.setText(memberDashboardInfo.getSTitleDsc());
                mtv_status.setText(GetMemberStatus.get(Integer.parseInt(memberDashboardInfo.getCMmbrStat())).getFsDescr());
                mtv_membership.setText(mViewModel.GetFormattedDate(memberDashboardInfo.getDMembrshp(), "MMMM d, yyyy"));
                mtv_sponsors.setText(lsSponsors);

                mtv_firstname.setText(memberDashboardInfo.getSFrstName());
                mtv_lastname.setText(memberDashboardInfo.getSLastName());
                mtv_middlename.setText((memberDashboardInfo.getSMiddName() == null || memberDashboardInfo.getSMiddName().isEmpty()) ? "N/A" : memberDashboardInfo.getSMiddName());
                mtv_suffix.setText((memberDashboardInfo.getSSuffixNm()  == null || memberDashboardInfo.getSSuffixNm().isEmpty()) ? "N/A" : memberDashboardInfo.getSSuffixNm());
                mtv_birthdate.setText(mViewModel.GetFormattedDate(memberDashboardInfo.getDBirthDte(), "MMMM d, yyyy"));
                mtv_civilstatus.setText(GetMemberCivil.get(Integer.parseInt(memberDashboardInfo.getCCvilStat())).getFsDescr());

                laMemberInfoOthers = new HashMap<>();

                mViewModel.DownloadMemberInfo(memberDashboardInfo.getSMemberID(), new VM_Member.OnDownload() {
                    @Override
                    public void Loading() {
                        Toast.makeText(requireActivity(), "Downloading member information. Please wait . . .", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void Finished(String fsMessage) {
                        Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();

                        mViewModel.ObserveMemberAddress(memberDashboardInfo.getSMemberID()).observe(getViewLifecycleOwner(), new Observer<List<TownProvince>>() {
                            @Override
                            public void onChanged(List<TownProvince> townProvinces) {

                                if (townProvinces == null) return;

                                List<String> laAddress = new ArrayList<>();
                                for (TownProvince loTown : townProvinces) {
                                    String lsHome = loTown.isHomeAddr().equalsIgnoreCase("1") ? "Primary" : "";
                                    String lsActive = loTown.isActive().equalsIgnoreCase("1") ? "Active" : "";

                                    // Base address: street + town/province
                                    String lsAddress = loTown.getPsAddressx();

                                    // Build suffix part
                                    StringBuilder suffix = new StringBuilder();
                                    if (!lsHome.isEmpty()) suffix.append(lsHome);
                                    if (!lsActive.isEmpty()) {
                                        if (suffix.length() > 0) suffix.append(" & ");
                                        suffix.append(lsActive);
                                    }

                                    // Append suffix if present
                                    if (suffix.length() > 0) {
                                        lsAddress = lsAddress + " (" + suffix.toString() + ")";
                                    }

                                    laAddress.add(lsAddress);
                                }


                                laMemberInfoOthers.put("Address List", new ArrayList<>(laAddress));
                                mViewModel.AddInfoList(laMemberInfoOthers);
                            }
                        });

                        mViewModel.ObserveMemberContact(memberDashboardInfo.getSMemberID()).observe(getViewLifecycleOwner(), new Observer<List<EMemberContactInfo>>() {
                            @Override
                            public void onChanged(List<EMemberContactInfo> eMemberContactInfos) {

                                if (eMemberContactInfos == null) return;

                                List<String> laContact = new ArrayList<>();
                                for (EMemberContactInfo loContact : eMemberContactInfos){

                                    String lsContact = loContact.getSContctNo();
                                    if (loContact.getCRecdStat().equalsIgnoreCase("1")) {
                                        lsContact = lsContact + " (Active)";
                                    }

                                    laContact.add(lsContact);
                                }

                                laMemberInfoOthers.put("Contact List", new ArrayList<>(laContact));
                                mViewModel.AddInfoList(laMemberInfoOthers);
                            }
                        });

                        mViewModel.ObserveMemberEmail(memberDashboardInfo.getSMemberID()).observe(getViewLifecycleOwner(), new Observer<List<EMemberEmailInfo>>() {
                            @Override
                            public void onChanged(List<EMemberEmailInfo> eMemberContactInfos) {

                                if (eMemberContactInfos == null) return;

                                List<String> laEmail = new ArrayList<>();
                                for (EMemberEmailInfo loEmail : eMemberContactInfos){

                                    String lsEmail = loEmail.getSEmailAdd();
                                    if (loEmail.getCRecdStat().equalsIgnoreCase("1")) {
                                        lsEmail = lsEmail + " (Active)";
                                    }

                                    laEmail.add(lsEmail);
                                }

                                laMemberInfoOthers.put("Email List", new ArrayList<>(laEmail));
                                mViewModel.AddInfoList(laMemberInfoOthers);
                            }
                        });

                        mViewModel.ObserveMemberInfoList().observe(getViewLifecycleOwner(), new Observer<HashMap<String, ArrayList<String>>>() {
                            @Override
                            public void onChanged(HashMap<String, ArrayList<String>> stringArrayListHashMap) {

                                if (stringArrayListHashMap == null) return;

                                List<String> parentList = new ArrayList<>(stringArrayListHashMap.keySet());

                                HashMap<String, ArrayList<String>> loChildMap = new HashMap<>();
                                for (Map.Entry<String, ArrayList<String>> loMap : stringArrayListHashMap.entrySet()){
                                    loChildMap.put(loMap.getKey(), new ArrayList<>(loMap.getValue()));
                                }

                                rcv_list.setAdapter(new Adapter_MemberInfoList(requireActivity(), new ArrayList<>(parentList), loChildMap));
                            }
                        });

                        mViewModel.ObserveCurrentRole(memberDashboardInfo.getSMemberID()).observe(getViewLifecycleOwner(), new Observer<OfficerInfo>() {
                            @Override
                            public void onChanged(OfficerInfo eOfficer) {

                                if (eOfficer == null){
                                    mtv_label_officer.setVisibility(View.GONE);
                                    card_officer.setVisibility(View.GONE);
                                    btn_view_officer.setVisibility(View.GONE);
                                    return;
                                }
                                mtv_label_officer.setVisibility(View.VISIBLE);
                                card_officer.setVisibility(View.VISIBLE);
                                btn_view_officer.setVisibility(View.VISIBLE);

                                List<MEMBER_CONSTANTS> laType = List.of(
                                        MEMBER_CONSTANTS.STATUS_ELECTED,
                                        MEMBER_CONSTANTS.STATUS_APPOINTED
                                );

                                List<MEMBER_CONSTANTS> laOfficerStatus = List.of(
                                        MEMBER_CONSTANTS.STATUS_OFFICER_SUSPENDED,
                                        MEMBER_CONSTANTS.STATUS_OFFICER_ACTIVE,
                                        MEMBER_CONSTANTS.STATUS_OFFICER_REASSIGN,
                                        MEMBER_CONSTANTS.STATUS_OFFICER_REMOVED,
                                        MEMBER_CONSTANTS.STATUS_OFFICER_RESIGNED,
                                        MEMBER_CONSTANTS.STATUS_OFFICER_DECEASE
                                );


                                mtv_term.setText(String.valueOf(eOfficer.getNYearxxxx()));
                                mtv_position.setText(eOfficer.getSPositionNme());
                                mtv_type.setText(laType.get(Integer.parseInt(eOfficer.getCAppointx())).getFsDescr());
                                mtv_off_status.setText(laOfficerStatus.get(Integer.parseInt(eOfficer.getCStatusxx())).getFsDescr());
                            }
                        });
                    }
                });
            }
        });
    }

    private void InitListener(){

        btn_edit_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent loIntent = new Intent(requireActivity(), Activity_Account.class);
                loIntent.putExtra("update", true);
                startActivity(loIntent);

            }
        });

        btn_edit_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment_Member loFragMem = new Fragment_Member();

                Bundle loArgs = new Bundle();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                loArgs.putString("fsGLPIDxx", mtv_glpid.getText().toString());
                loFragMem.setArguments(loArgs);

                fragmentTransaction.replace(R.id.layout_container, loFragMem);
                fragmentTransaction.addToBackStack("create_member");
                fragmentTransaction.commit();

            }
        });

        btn_view_officer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment_Officer_history loFragHistory = new Fragment_Officer_history();

                Bundle loArgs = new Bundle();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                loArgs.putString("fsGLPIDxx", mtv_glpid.getText().toString());
                loFragHistory.setArguments(loArgs);

                fragmentTransaction.replace(R.id.layout_container, loFragHistory);
                fragmentTransaction.addToBackStack("view_officer_history");
                fragmentTransaction.commit();
            }
        });

        rcv_list.setOnGroupClickListener((parent, v, groupPosition, id) -> {

            if (rcv_list.isGroupExpanded(groupPosition)) {
                rcv_list.collapseGroup(groupPosition);
            } else {
                rcv_list.expandGroup(groupPosition);
            }
            return true;
        });
    }

}