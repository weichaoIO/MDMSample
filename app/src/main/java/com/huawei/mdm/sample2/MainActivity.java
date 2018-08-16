package com.huawei.mdm.sample2;

import com.huawei.android.app.AppOpsManagerEx;
import com.huawei.android.app.admin.DeviceRestrictionManager;

import android.os.Bundle;
import android.os.RemoteException;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private DeviceRestrictionManager mDeviceRestrictionManager = null;
    private DevicePolicyManager mDevicePolicyManager = null;
    private ComponentName mAdminName = null;
    private TextView mStatusText;
    private Button wifiDisableBtn;
    private Button wifiEnableBtn;
    private TextView mStatusText2;
    private Button googleDisableBtn;
    private Button googleEnableBtn;
    private TextView mStatusText3;
    private Button captureDisableBtn;
    private Button captureEnableBtn;

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        AppOpsManagerEx appManager = new AppOpsManagerEx();
//        try {
//            appManager.setMode(AppOpsManagerEx.TYPE_OPEN_BLUETOOTH, "com.huawei.mdm.sample2", AppOpsManagerEx.MODE_ALLOWED);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceRestrictionManager = new DeviceRestrictionManager();
        mAdminName = new ComponentName(this, SampleDeviceReceiver.class);

        initSampleView();
        updateState();
        new SampleEula(this, mDevicePolicyManager, mAdminName).show();
    }

    private void initSampleView() {
        mStatusText = findViewById(R.id.wifiStateTxt);
        wifiDisableBtn = findViewById(R.id.disableWifi);
        wifiEnableBtn = findViewById(R.id.enableWifi);
        mStatusText2 = findViewById(R.id.googleAccountStateTxt);
        googleDisableBtn = findViewById(R.id.disableGoogleAccount);
        googleEnableBtn = findViewById(R.id.enableGoogleAccount);
        mStatusText3 = findViewById(R.id.screenCaptureStateTxt);
        captureDisableBtn = findViewById(R.id.disableScreenCapture);
        captureEnableBtn = findViewById(R.id.enableScreenCapture);

        wifiDisableBtn.setOnClickListener(new SampleOnClickListener());
        wifiEnableBtn.setOnClickListener(new SampleOnClickListener());
        googleDisableBtn.setOnClickListener(new SampleOnClickListener());
        googleEnableBtn.setOnClickListener(new SampleOnClickListener());
        captureDisableBtn.setOnClickListener(new SampleOnClickListener());
        captureEnableBtn.setOnClickListener(new SampleOnClickListener());
    }

    private void updateState() {
        if (!isActiveMe()) {
            mStatusText.setText(getString(R.string.state_not_actived));
            mStatusText2.setText(getString(R.string.state_not_actived));
            mStatusText3.setText(getString(R.string.state_not_actived));
            return;
        }

        boolean isWifiDisabled = false;
        boolean isGoogleAccountDisabled = false;
        boolean isScreenCaptureDisabled = false;
        try {
            isWifiDisabled = mDeviceRestrictionManager.isWifiDisabled(mAdminName);
            isGoogleAccountDisabled = mDeviceRestrictionManager.isGoogleAccountDisabled(mAdminName);
            isScreenCaptureDisabled = mDeviceRestrictionManager.isScreenCaptureDisabled(mAdminName);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        if (isWifiDisabled) {
            mStatusText.setText(R.string.state_restricted);
        } else {
            mStatusText.setText(getString(R.string.state_nomal));
        }
        if (isGoogleAccountDisabled) {
            mStatusText2.setText(R.string.state_restricted);
        } else {
            mStatusText2.setText(getString(R.string.state_nomal));
        }
        if (isScreenCaptureDisabled) {
            mStatusText3.setText(R.string.state_restricted);
        } else {
            mStatusText3.setText(getString(R.string.state_nomal));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateState();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isActiveMe() {
        return mDevicePolicyManager != null && mDevicePolicyManager.isAdminActive(mAdminName);
    }

    private class SampleOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            boolean disableWfi = false;
            boolean disableGoogleAccount = false;
            boolean disableScreenCapture = false;

            if (v.getId() == R.id.disableWifi) {
                disableWfi = true;
            } else if (v.getId() == R.id.enableWifi) {
                disableWfi = false;
            } else if (v.getId() == R.id.disableGoogleAccount) {
                disableGoogleAccount = true;
            } else if (v.getId() == R.id.enableGoogleAccount) {
                disableGoogleAccount = false;
            } else if (v.getId() == R.id.disableScreenCapture) {
                disableScreenCapture = true;
            } else if (v.getId() == R.id.enableScreenCapture) {
                disableScreenCapture = false;
            }

            try {
                if (mDeviceRestrictionManager != null) {
                    mDeviceRestrictionManager.setWifiDisabled(mAdminName, disableWfi);
                    mDeviceRestrictionManager.setGoogleAccountDisabled(mAdminName, disableGoogleAccount);
                    mDeviceRestrictionManager.setScreenCaptureDisabled(mAdminName, disableScreenCapture);
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
            updateState();
        }
    }
}