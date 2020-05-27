package com.jc.simpleapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.jc.simpleapp.service.MainService;
import com.jc.simpleapp.R;
import com.jc.simpleapp.accessibility.MyAccessibilityService;
import com.jc.simpleapp.util.ConfigurationManager;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static int REQUEST_SETTING_ACTION_ACCESSIBILITY = 2;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 3;
    private Button btnConfiguration, btnShowResult, btnStartMainService, btnStopMainService, btnLicense, btnAccess;
    private Button.OnClickListener buttonOnClickListener =  new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btnConfiguration:
                    startActivity(new Intent(MainActivity.this,ConfigurationActivity.class));
                    break;
                case R.id.btnShowResult:
                    startActivity(new Intent(MainActivity.this,PriceReportActivity.class));
                    break;
                case R.id.btnStartMainService:
                    Log.d(TAG,"startMainService");
                    startService(new Intent(MainActivity.this,MainService.class));
                    break;
                case R.id.btnStopMainService:
                    stopService(new Intent(MainActivity.this,MainService.class));
                    break;
                case R.id.btnLicense:
                    showLicenseInfo();
                    break;
                case R.id.btnAccess:
                    if(!isAccessibilitySettingsOn()) {
                        startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), REQUEST_SETTING_ACTION_ACCESSIBILITY);
                    }

                    break;
                default:

                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkOverlayPermission();
        setContentView(R.layout.activity_main);
        checkConfigurationInitialState();
        startService(new Intent(MainActivity.this,MainService.class));
        initView();
    }

    private void checkConfigurationInitialState() {
        if(ConfigurationManager.getInitialState(this)){  //First time to launch app
            startActivity(new Intent(MainActivity.this,ConfigurationActivity.class));
        }
    }

    protected void initView() {
        btnConfiguration = (Button)findViewById(R.id.btnConfiguration);
        btnShowResult = (Button)findViewById(R.id.btnShowResult);
        btnStartMainService = (Button)findViewById(R.id.btnStartMainService);
        btnStopMainService = (Button)findViewById(R.id.btnStopMainService);
        btnLicense = (Button) findViewById(R.id.btnLicense);
        btnAccess = (Button)findViewById(R.id.btnAccess);

        btnConfiguration.setOnClickListener(buttonOnClickListener);
        btnShowResult.setOnClickListener(buttonOnClickListener);
        btnStartMainService.setOnClickListener(buttonOnClickListener);
        btnStopMainService.setOnClickListener(buttonOnClickListener);
        btnLicense.setOnClickListener(buttonOnClickListener);
        btnAccess.setOnClickListener(buttonOnClickListener);

    }

    private void showLicenseInfo(){
        Dialog licenseInfoDialog = new Dialog(this);
        licenseInfoDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        licenseInfoDialog.setContentView(R.layout.license_info);
        licenseInfoDialog.setTitle(getResources().getString(R.string.license_info_title));
        licenseInfoDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.alvose_green);
        licenseInfoDialog.show();
    }

    private boolean isAccessibilitySettingsOn() {   //Will move to other places
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + MyAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v("", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v("", "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v("", "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v("", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v("", "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }

    // https://blog.csdn.net/bestcxcxj/article/details/81385288
    private void checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }
    }

}
