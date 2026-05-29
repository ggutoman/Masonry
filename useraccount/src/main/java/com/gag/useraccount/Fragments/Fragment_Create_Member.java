package com.gag.useraccount.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gag.useraccount.R;
import com.gag.useraccount.ViewModel.VM_Member;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.gag.appdriver.Room.DataObject.DTownInfo;
import org.gag.appdriver.Room.Entities.ELodgeInfo;
import org.gag.appdriver.Room.Entities.ETitle;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Fragment_Create_Member extends Fragment {

    private Message_Dialog poMessage;
    private LoadDialog poDialog;
    private VM_Member mviewModel;


    private LinearLayout layout_member,
            layout_personal;

    private MaterialAutoCompleteTextView
            auto_status,
            auto_title,
            auto_sponosr,
            auto_town,
            auto_contact,
            auto_email,
            auto_civil,
            auto_lodge;

    private TextInputLayout til_sponsor,
            til_address,
            til_contact,
            til_email,
            til_midname,
            til_suffix,
            til_civil;

    private ImageButton
            btn_add_address,
            btn_add_contact,
            btn_add_email;

    private MaterialButton btn_add_sponsor, btn_create,btn_cancel;

    private TextInputEditText tie_lastname,
            tie_firstname,
            tie_middlename,
            tie_suffix,
            tie_glpid;

    private LodgeAdapter LodgeAdapter;
    private TitleAdapter TitleAdapter;
    private TownCityAdapter TownProvAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.fragment_create_member, container, false);

        mviewModel = new ViewModelProvider(this).get(VM_Member.class);
        poMessage = new Message_Dialog(requireActivity());
        poDialog = new LoadDialog(requireActivity());

        poMessage.InitDialog();
        poDialog.InitDialog();


        initViews(view);
        initDataReceiver();
        initListeners();

        return view;

    }

    private void initViews(View view) {

        layout_member = view.findViewById(R.id.layout_member);
        layout_personal = view.findViewById(R.id.layout_personal);

        auto_lodge = view.findViewById(R.id.auto_lodge);
        tie_glpid = view.findViewById(R.id.tie_glpid);
        auto_status = view.findViewById(R.id.auto_status);
        auto_title = view.findViewById(R.id.auto_title);
        auto_sponosr = view.findViewById(R.id.auto_sponosr);
        auto_town = view.findViewById(R.id.auto_town);
        auto_contact = view.findViewById(R.id.auto_contact);
        auto_email = view.findViewById(R.id.auto_email);
        auto_civil = view.findViewById(R.id.auto_civil);

        til_sponsor = view.findViewById(R.id.til_sponsor);
        til_address = view.findViewById(R.id.til_town);
        til_contact = view.findViewById(R.id.til_contact);
        til_email = view.findViewById(R.id.til_email);
        til_midname = view.findViewById(R.id.til_midname);
        til_suffix = view.findViewById(R.id.til_suffix);
        til_civil = view.findViewById(R.id.til_civil);

        btn_add_sponsor = view.findViewById(R.id.btn_add_sponsor);
        btn_add_address = view.findViewById(R.id.btn_add_address);
        btn_add_contact = view.findViewById(R.id.btn_add_contact);
        btn_add_email = view.findViewById(R.id.btn_add_email);
        btn_create = view.findViewById(R.id.btn_create);
        btn_cancel = view.findViewById(R.id.btn_cancel);

        tie_lastname = view.findViewById(R.id.tie_lastname);
        tie_firstname = view.findViewById(R.id.tie_firstname);
        tie_middlename = view.findViewById(R.id.tie_middlename);
        tie_suffix = view.findViewById(R.id.tie_suffix);
        tie_glpid = view.findViewById(R.id.tie_glpid);

        btn_create.setText("Create Member");
        tie_glpid.setText(mviewModel.GenerateGLPID());
    }

    private void initDataReceiver() {

        mviewModel.GetSponsorList().observe(requireActivity(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {

                //limit add to maximum of 3
                if (strings.size() >=3){
                    btn_add_contact.setEnabled(false);
                    return;
                }
                btn_add_contact.setEnabled(true);

                auto_sponosr.setAdapter(
                        new ArrayAdapter<>(
                                requireActivity(),
                                android.R.layout.simple_spinner_dropdown_item,
                                strings
                        )
                );
            }
        });

        mviewModel.GetLodgeList().observe(requireActivity(), new Observer<List<ELodgeInfo>>() {
            @Override
            public void onChanged(List<ELodgeInfo> eLodgeInfos) {

                LodgeAdapter = new LodgeAdapter(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        eLodgeInfos);

                auto_lodge.setAdapter(LodgeAdapter);
            }
        });

        mviewModel.GetTitleList().observe(requireActivity(), new Observer<List<ETitle>>() {
            @Override
            public void onChanged(List<ETitle> eTitles) {

                TitleAdapter = new TitleAdapter(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        eTitles
                );

                auto_title.setAdapter(TitleAdapter);
            }
        });

        mviewModel.TownSearch().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {

                mviewModel.GetTownList(s).observe(requireActivity(), new Observer<List<DTownInfo.TownProvince>>() {
                    @Override
                    public void onChanged(List<DTownInfo.TownProvince> townProvinces) {

                        TownProvAdapter = new TownCityAdapter(
                                requireActivity(),
                                android.R.layout.simple_spinner_dropdown_item,
                                townProvinces
                        );

                        auto_town.setAdapter(TownProvAdapter);

                    }
                });
            }
        });

        auto_status.setAdapter(
                new ArrayAdapter<>(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        mviewModel.GetAccountStatus()
                ));

        auto_civil.setAdapter(
                new ArrayAdapter<>(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        mviewModel.GetCivilStatus()
                ));
    }

    private void initListeners() {

        btn_add_sponsor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (auto_sponosr.getText() == null || auto_sponosr.getText().toString().isEmpty()){

                    poMessage.ShowMessage(1, "Please enter a sponsor name", "Okay", "", new Message_Dialog.OnDialogClick() {
                        @Override
                        public void OnPositive(@NotNull AlertDialog poDialog) { poDialog.dismiss(); }

                        @Override
                        public void OnNegative(@NotNull AlertDialog poDialog) {}
                    });
                    return;
                }
                mviewModel.AddSponsor(auto_sponosr.getText().toString());
                auto_sponosr.setText("");
            }
        });

        auto_lodge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                auto_lodge.setText(((ELodgeInfo) adapterView.getItemAtPosition(i)).getSLodgeNme(), false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        auto_title.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                auto_title.setText(((ETitle) adapterView.getItemAtPosition(i)).getSTitleDsc(), false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        auto_town.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mviewModel.SearchTownProvince(charSequence.toString());
            }
        });

        auto_town.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                auto_town.setText(((DTownInfo.TownProvince) adapterView.getItemAtPosition(i)).getPsTownProvNme(), false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private boolean isDataValid() {

        if (TextUtils.isEmpty(auto_lodge.getText().toString())) {

            auto_lodge.setError("Lodge is required");
            auto_lodge.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(tie_glpid.getText().toString())) {

            tie_glpid.setError("Account type is required");
            tie_glpid.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(auto_status.getText().toString())) {

            auto_status.setError("Status is required");
            auto_status.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(auto_title.getText().toString())) {

            auto_title.setError("Title is required");
            auto_title.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(tie_lastname.getText().toString())) {

            tie_lastname.setError("Lastname is required");
            tie_lastname.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(tie_firstname.getText().toString())) {

            tie_firstname.setError("Firstname is required");
            tie_firstname.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(auto_civil.getText().toString())) {

            auto_civil.setError("Civil status is required");
            auto_civil.requestFocus();
            return false;
        }

        return true;
    }

    public static class LodgeAdapter extends ArrayAdapter<ELodgeInfo>{

        private final Context loContext;
        private final List<ELodgeInfo> lodges;

        public LodgeAdapter(@NonNull Context context, int resource, @NonNull List<ELodgeInfo> objects) {
            super(context, resource, objects);

            loContext = context;
            lodges = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(loContext);
                view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            }

            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(lodges.get(position).getSLodgeNme());

            return view;

        }
    }

    public static class TitleAdapter extends ArrayAdapter<ETitle>{

        private final Context loContext;
        private final List<ETitle> titles;

        public TitleAdapter(@NonNull Context context, int resource, @NonNull List<ETitle> objects) {
            super(context, resource, objects);

            loContext = context;
            titles = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(loContext);
                view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            }

            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(titles.get(position).getSTitleDsc());

            return view;

        }
    }

    public static class TownCityAdapter extends ArrayAdapter<DTownInfo.TownProvince>{

        private final Context loContext;
        private final List<DTownInfo.TownProvince> towncity;

        public TownCityAdapter(@NonNull Context context, int resource, @NonNull List<DTownInfo.TownProvince> objects) {
            super(context, resource, objects);

            loContext = context;
            towncity = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(loContext);
                view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            }

            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(towncity.get(position).getPsTownProvNme());

            return view;

        }
    }

}