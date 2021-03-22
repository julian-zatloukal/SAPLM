package com.example.SAPLM.bluetoothActivities;

import com.example.SAPLM.R;

public class AvailableDevice {

    public enum DeviceState { DEFAULT, DISCONNECTED, CONNECTED, LOADING}
    private String deviceName;
    private DeviceState currentState;
    private int deviceStateIconID;

    public AvailableDevice() {
    }

    public AvailableDevice(String deviceName, DeviceState defaultState) {
        this.deviceName = deviceName;
        this.currentState = defaultState;
    }

    public void setCurrentState(DeviceState currentState) {
        this.currentState = currentState;
    }

    public DeviceState getCurrentState() {
        return currentState;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public int getDeviceStateIconID() {
        switch (currentState){
            case DEFAULT:
                return 0;
            case CONNECTED:
                return R.drawable.ic_done_icon;
            case LOADING:
                return R.drawable.loading_animation;
            case DISCONNECTED:
                return R.drawable.ic_close_icon;
        }
        return 0;
    }

    public int getTextAppearance(){
        switch (currentState){
            case DEFAULT:
                return R.style.btDeviceListAppearance;
            case CONNECTED:
                return R.style.btDeviceListSelectedAppearance;
            case LOADING:
                return R.style.btDeviceListAppearance;
            case DISCONNECTED:
                return R.style.btDeviceListAppearance;
        }
        return R.style.btDeviceListAppearance;
    }
}
