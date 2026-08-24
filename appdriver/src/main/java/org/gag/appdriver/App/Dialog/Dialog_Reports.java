package org.gag.appdriver.App.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.R;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Dialog_Reports {

    private final Context loInstance;
    private final View view;
    private final AlertDialog poDialog;

    private final OnSubmitEntry loCallback;

    private MaterialAutoCompleteTextView auto_parameter;
    private TextInputEditText tie_value;
    private MaterialTextView mtv_save;
    private MaterialButton btn_submit;

    private List<String> laParams = new ArrayList<>();
    private int lnSelectParam;

    private final JSONObject loParams;

    public interface OnSubmitEntry{
        void OnSubmit(JSONObject foParams);
    }

    public Dialog_Reports(Context context, JSONObject foParams, OnSubmitEntry foCallback) {

        view = LayoutInflater.from(context).inflate(R.layout.layout_report_dialog, null);
        loCallback = foCallback;
        loParams = foParams;
        loInstance = context;

        // Build and show dialog
        poDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create();

        poDialog.setCancelable(true);
        if (poDialog.getWindow() != null) {
            poDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        InitViews(view);
        InitDataReceiver();
        InitListeners();
    }

    private void InitViews(View view){

        mtv_save = view.findViewById(R.id.mtv_save);
        auto_parameter = view.findViewById(R.id.auto_parameter);
        tie_value = view.findViewById(R.id.tie_value);
        btn_submit = view.findViewById(R.id.btn_submit);

    }

    private void InitDataReceiver(){

        if (loParams.length() > 0){

            // Collect all keys and display on selection
            Iterator<String> keys = loParams.keys();
            while (keys.hasNext()) {
                laParams.add(keys.next());
            }

            auto_parameter.setAdapter(new ArrayAdapter<>(
                    loInstance,
                    android.R.layout.simple_spinner_dropdown_item,
                    laParams
            ));

        }
    }

    private void InitListeners(){

        auto_parameter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                try {
                    lnSelectParam = i;
                    tie_value.setText(loParams.get(laParams.get(lnSelectParam)).toString());
                }catch (Exception ex){
                    Toast.makeText(loInstance, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        tie_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try{
                    String lsSelectParam = laParams.get(lnSelectParam);
                    mtv_save.setVisibility(loParams.getString(lsSelectParam).equalsIgnoreCase(charSequence.toString()) ? View.GONE : View.VISIBLE);
                }catch (Exception ex){
                    Toast.makeText(loInstance, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mtv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{

                    if (lnSelectParam < 0){
                        Toast.makeText(loInstance, "Please select parameter", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String lsVal = tie_value.getText() == null ? "" : tie_value.getText().toString();
                    loParams.put(laParams.get(lnSelectParam), lsVal);

                    view.setVisibility(View.GONE);

                }catch (Exception ex){
                    Toast.makeText(loInstance, "Could not save parameter: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (poDialog.isShowing()){
                    loCallback.OnSubmit(loParams);
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
