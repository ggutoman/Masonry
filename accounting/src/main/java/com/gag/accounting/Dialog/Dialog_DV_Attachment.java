package com.gag.accounting.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Annual;
import com.gag.accounting.ViewModel.VM_Disbursement;
import com.gag.accounting.ViewModel.VM_Receipts;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.gag.appdriver.App.Adapters.MemberAdapter;
import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.Room.Entities.EDisbursementAttachment;
import org.gag.appdriver.Room.Entities.EMemberInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Dialog_DV_Attachment {

    private final Context loInstance;
    private final View view;
    private final AlertDialog poDialog;
    private final OnAttach loCallback;

    private final File loFile;

    private TextInputEditText tie_doctitle, tie_filepath, tie_remarks;
    private MaterialButton btn_attach;

    public interface OnAttach{
        void OnSuccess(String fsDocTitle, String fsRemarks);
        void OnFailed(String fsMessage);
    }


    public Dialog_DV_Attachment(Context context, File foFile, OnAttach foCallback) {

        view = LayoutInflater.from(context).inflate(R.layout.dialog_attachment_dv, null);

        loInstance = context;
        loFile = foFile;
        loCallback = foCallback;

        // Build and show dialog
        poDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create();

        poDialog.setCancelable(true);
        if (poDialog.getWindow() != null) {
            poDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        InitViews();
        InitListeners();

    }

    private void InitViews(){

        tie_doctitle = view.findViewById(R.id.tie_doctitle);
        tie_filepath = view.findViewById(R.id.tie_filepath);
        tie_remarks = view.findViewById(R.id.tie_remarks);
        btn_attach = view.findViewById(R.id.btn_attach);

        if (loFile == null || !loFile.exists()){
            btn_attach.setEnabled(false);
        }else {
            tie_filepath.setText(loFile.getAbsolutePath());
            btn_attach.setEnabled(true);
        }
    }

    private void InitListeners(){

        btn_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tie_doctitle.getText() == null || tie_doctitle.getText().toString().isEmpty()){
                    Toast.makeText(loInstance, "Please enter document title", Toast.LENGTH_SHORT).show();
                    return;
                }

               if (loFile.exists()){
                   loCallback.OnSuccess(tie_doctitle.getText().toString(), tie_remarks.getText() == null || tie_remarks.getText().toString().isEmpty() ? "" : tie_remarks.getText().toString());
               }else {
                   loCallback.OnFailed("File not found");
               }

                if (poDialog.isShowing()){
                    poDialog.dismiss();
                }
            }
        });
    }

    public void Show(){
        if (!poDialog.isShowing()){
            poDialog.show();
        }
    }

}
