package com.example.SAPLM;

import android.app.Activity;
import androidx.lifecycle.LifecycleObserver;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import com.example.SAPLM.bluetoothActivities.BluetoothConnection;
import com.example.SAPLM.settingsActivities.DeveloperConsoleActivity;

import java.io.IOException;

public class Application extends android.app.Application implements LifecycleObserver {

    @Override
    public void onCreate() {
        super.onCreate();

        // ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        IntentFilter bluetoothAdapterActionChangedFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(BluetoothConnection.bluetoothAdapterStatusBR, bluetoothAdapterActionChangedFilter);

        IntentFilter bluetoothScanModeStatusChangedFilter = new IntentFilter();
        bluetoothScanModeStatusChangedFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        bluetoothScanModeStatusChangedFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        bluetoothScanModeStatusChangedFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(BluetoothConnection.bluetoothScanModeStatusBR, bluetoothScanModeStatusChangedFilter);

        IntentFilter bluetoothDeviceStatusChangedFilter = new IntentFilter();
        bluetoothDeviceStatusChangedFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        bluetoothDeviceStatusChangedFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        bluetoothDeviceStatusChangedFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        bluetoothDeviceStatusChangedFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(BluetoothConnection.bluetoothDeviceStatusBR, bluetoothDeviceStatusChangedFilter);

        Thread internetDetector = new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){
                    try {
                        internetConnectionStatus = isConnected();
                        Thread.sleep(10000);
                        if (internetConnectionStatus) {Application.toastMessage("Internet connection detected");}
                        else {Application.toastMessage("Proper internet connection has not been detected");}
                    } catch (Throwable th) { th.printStackTrace(); Application.toastMessage(th.toString());}
                }
            }
        };

        internetDetector.start();

        Application.persistentDataSave.putString("MAIN_TEXT_KEY", new String());
        Application.persistentDataSave.putString("ONGOING_TEXT_KEY", new String());

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private static Activity mActivity;
    private static Context mContext;
    public static Bundle persistentDataSave = new Bundle();


    public static Activity getActivity() { return mActivity; }
    public static void setActivity(Activity mActivitySet) { mActivity = mActivitySet; }

    public static Context getContext() { return mContext; }
    public static void setContext(Context mContextoSet) { mContext = mContextoSet; }

    public static void toastMessage(final String message){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mActivity != null) {
                    //Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static boolean internetConnectionStatus;

    public static boolean isSystemOnline(){
        return internetConnectionStatus;
    }

    public boolean isConnected() throws InterruptedException, IOException {
        final String command = "ping -c 1 google.com";
        return Runtime.getRuntime().exec(command).waitFor() == 0;
    }






}
