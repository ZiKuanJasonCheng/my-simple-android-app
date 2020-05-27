package com.jc.simpleapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jc.simpleapp.R;
import com.jc.simpleapp.constant.BroadcastActionConstant;
import com.jc.simpleapp.util.ConfigurationManager;


public class ConfigurationActivity extends Activity {

    public static final String TAG = "Configuration";
    private CheckBox cbIcon;
    private CheckBox cbPopup;
    private CheckBox cbNotificationBar;
    private CheckBox cbApp;
    private CheckBox cbBar;
    private CheckBox cbEmail;
    private EditText edtEmail;
    private RadioGroup rgDropPercentage;
    private TextView txtFalseInput;
    private Button btnOK;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);

        initialView();
        loadConfiguration();

        checkConfigurationInitialState();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG,"This is my onBackPressed()!!");
        if(ConfigurationManager.getInitialState(this)){
            startActivity(new Intent(Intent.ACTION_MAIN).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addCategory(Intent.CATEGORY_HOME));
        }
        else{
            super.onBackPressed();
        }
    }

    private void initialView(){
        cbNotificationBar = (CheckBox) findViewById(R.id.cbNotificationStatusBar);
        cbNotificationBar.setAlpha(0.4f);
        cbPopup = (CheckBox) findViewById(R.id.cbAutoPopup);
        cbPopup.setAlpha(0.4f);
        cbIcon = (CheckBox) findViewById(R.id.cbFloatingIcon);


        rgDropPercentage = (RadioGroup) findViewById(R.id.rgPercentage);


        cbApp = (CheckBox) findViewById(R.id.cbApp);
        cbApp.setAlpha(0.4f);
        cbBar = (CheckBox) findViewById(R.id.cbBar);
        cbBar.setAlpha(0.4f);
        cbEmail = (CheckBox) findViewById(R.id.cbEmail);

        edtEmail = (EditText) findViewById(R.id.edtMail);
        if (!cbEmail.isChecked()) {
            edtEmail.setEnabled(false);
        }

        cbEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    edtEmail.setEnabled(false);
                } else {
                    edtEmail.setEnabled(true);
                }
            }
        });


        txtFalseInput = (TextView) findViewById(R.id.txtFalseMail);

        btnOK = (Button) findViewById(R.id.btnConfigOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Check E-mail address if cbEmail is checked
                if (cbEmail.isChecked()) {
                    if (edtEmail.getText().toString().isEmpty() || (!edtEmail.getText().toString().contains("@") || !edtEmail.getText().toString().contains("."))) {
                        txtFalseInput.setVisibility(View.VISIBLE);
                    }
                    else {
                        txtFalseInput.setVisibility(View.INVISIBLE);
                    }
                }

                if (!cbEmail.isChecked() || txtFalseInput.getVisibility()==View.INVISIBLE) {
                    //Before finish(), check if it is initial state currently
                    if(ConfigurationManager.getInitialState(v.getContext())){
                        ConfigurationManager.setInitialState(v.getContext(), false);
                    }
                    saveConfiguration();
                    finish();
                }
            }
        });

        btnCancel = (Button) findViewById(R.id.btnConfigCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void checkConfigurationInitialState() {
        if(ConfigurationManager.getInitialState(this)) {
            btnCancel.setVisibility(View.INVISIBLE);
            //btnOK.setPadding(Gravity.CENTER,0,0,0);
        }
    }

    private void loadConfiguration(){
        cbIcon.setChecked(ConfigurationManager.getShowFloatingIcon(ConfigurationActivity.this));
        cbEmail.setChecked(ConfigurationManager.getAllowEmail(ConfigurationActivity.this));
        edtEmail.setText(ConfigurationManager.getUserEmail(ConfigurationActivity.this));
        RadioButton radioButton = null;

        switch(ConfigurationManager.getPriceDropPercentage(ConfigurationActivity.this)){
            case 5:
                radioButton = (RadioButton) rgDropPercentage.findViewById(R.id.rbFive);
                break;
            case 10:
                radioButton = (RadioButton) rgDropPercentage.findViewById(R.id.rbTen);
                break;
            case 20:
                radioButton = (RadioButton) rgDropPercentage.findViewById(R.id.rbTwenty);
                break;
            case 30:
                radioButton = (RadioButton) rgDropPercentage.findViewById(R.id.rbThirty);
                break;
            default:
                radioButton = (RadioButton) rgDropPercentage.findViewById(R.id.rbFive);
                break;
        }

        radioButton.setChecked(true);
    }

    private void saveConfiguration() {
        Log.d(TAG,"saveConfiguration");
        ConfigurationManager.setShowFloatingIcon(ConfigurationActivity.this, cbIcon.isChecked());
        ConfigurationManager.setAllowEmail(ConfigurationActivity.this, cbEmail.isChecked());
        ConfigurationManager.setUserEmail(ConfigurationActivity.this,edtEmail.getText().toString());
        int priceDropPercentage = 0;

        switch(rgDropPercentage.getCheckedRadioButtonId()){
            case R.id.rbFive:
                priceDropPercentage=5;
                break;
            case R.id.rbTen:
                priceDropPercentage=10;
                break;
            case R.id.rbTwenty:
                priceDropPercentage=20;
                break;
            case R.id.rbThirty:
                priceDropPercentage=30;
                break;
            default:
                priceDropPercentage=5;
                break;
        }
        ConfigurationManager.setPriceDropPercentage(ConfigurationActivity.this,priceDropPercentage);
        Intent intentBroadcast = new Intent(BroadcastActionConstant.CONFIGURATION_UPDATED);
        sendBroadcast(intentBroadcast);
    }


}
