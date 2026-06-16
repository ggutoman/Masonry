package com.gag.masonry.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gag.masonry.R;

public class Fragment_Child_Container extends Fragment {

    private static final String ARG_FRAGMENT_ID = "fragment_id";

    public static Fragment_Child_Container newInstance(String fragmentId) {

        Fragment_Child_Container fragment = new Fragment_Child_Container();

        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_ID, fragmentId);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_child_container, container, false);

        // Retrieve parameter
        String lsFragmentID = getArguments() != null ? getArguments().getString(ARG_FRAGMENT_ID) : "";
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

        //calendar will be used to call another fragment
        switch (lsFragmentID) {

            case "lodge_calendar_list":
            case "fund_history":
                transaction.replace(R.id.frame_child, new Fragment_Lodge_Calendar_List());
                transaction.addToBackStack(lsFragmentID);
                break;
        }
        transaction.commit();

        return view;
    }
}

