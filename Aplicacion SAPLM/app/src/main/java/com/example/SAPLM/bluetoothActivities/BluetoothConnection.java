package com.example.SAPLM.bluetoothActivities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TabHost;

import com.example.SAPLM.Application;
import com.example.SAPLM.settingsActivities.DeveloperConsoleActivity;
import com.example.SAPLM.settingsActivities.TerminalManager;


import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;


public class BluetoothConnection extends BluetoothActivity {

    static ScheduledExecutorService bluetoothThreadPool= Executors.newScheduledThreadPool(1);
    static ScheduledFuture<?> bluetoothComThread;
    public static OutputStream outputStream;
    public static InputStream inputStream;
    public static BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    public static BluetoothDevice targetDevice;
    public static BluetoothDevice[] bluetoothDevicesArray;
    public static List<BluetoothDevice> bluetoothDevicesList;
    public static BluetoothSocket btSocket;
    public static BluetoothServerSocket btServerSocket;
    public static UUID btUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public static String btString = "HC-05";
    public static Boolean bluetoothConnected = false;

    private static Executor bluetoothConnectorExecutor = Executors.newSingleThreadExecutor();
    private static Boolean isListAvailableBluetoothDevicesRunning = false;

    public static void listAvailableBluetoothDevices(){
        if ((!isListAvailableBluetoothDevicesRunning)) {
            try {
                BluetoothActivity.setBluetoothStateGui(bluetoothConnectionState.WAITING);
                isListAvailableBluetoothDevicesRunning = true;
                bluetoothConnectorExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (btAdapter != null) {
                            if (btAdapter.isEnabled()) {
                                Set<BluetoothDevice> bondedDevices = btAdapter.getBondedDevices();

                                bluetoothDevicesList = new ArrayList<>();
                                bluetoothDevicesList.addAll(bondedDevices);

                                if (bondedDevices.size() > 0) {
                                    bluetoothDevicesArray = bondedDevices.toArray(new BluetoothDevice[bondedDevices.size()]);
                                }
                                updateBluetoothListGUI();
                            } else {
                                BluetoothActivity.setBluetoothStateGui(bluetoothConnectionState.BLUETOOTH_DISABLED);
                            }
                        }
                        isListAvailableBluetoothDevicesRunning = false;
                    }
                });
            }catch (Throwable th)
            {
                Application.toastMessage(th.toString()); th.printStackTrace();
            }
        }
    }

    public static Boolean isBluetoothAdapterReady(){
        return ((btAdapter != null)&&(btAdapter.isEnabled()));
    }

    public static void updateBluetoothListGUI(){
        if (BluetoothActivity.mAdapter!=null) {
            BluetoothActivity.deviceList.clear();
            for (int x = 0; x < bluetoothDevicesArray.length; x++) {
                BluetoothActivity.deviceList.add(new AvailableDevice(bluetoothDevicesArray[x].getName(), AvailableDevice.DeviceState.DEFAULT));
            }

            if (bluetoothConnected) {
                BluetoothActivity.setBluetoothStateGui(bluetoothConnectionState.SYSTEM_CONNECTED);
                deviceList.get(bluetoothDevicesList.indexOf(targetDevice)).setCurrentState(AvailableDevice.DeviceState.CONNECTED);
            }
            else {
                BluetoothActivity.setBluetoothStateGui(bluetoothConnectionState.SYSTEM_DISCONNECTED);
            }

            BluetoothActivity.updateDevicesList();
        }
    }

    private static Boolean attemptingBluetoothConnection = false;


    public static void connectBluetoothDeviceSync(int deviceIndex){
        if ((!isListAvailableBluetoothDevicesRunning)&&(!attemptingBluetoothConnection)&&isBluetoothAdapterReady()) {
            try {
                attemptingBluetoothConnection = true;
                if (bluetoothConnected) closeAllConnections();
                BluetoothActivity.setBluetoothStateGui(bluetoothConnectionState.WAITING);
                targetDevice = bluetoothDevicesArray[deviceIndex];
                btSocket = targetDevice.createRfcommSocketToServiceRecord(btUUID);
                btSocket.connect();
                if (btSocket.isConnected()) {
                    inputStream = btSocket.getInputStream();
                    outputStream = btSocket.getOutputStream();
                    bluetoothConnected = true;
                    BluetoothActivity.setBluetoothStateGui(bluetoothConnectionState.SYSTEM_CONNECTED);
                    BluetoothActivity.updateDevicesList();
                } else {
                    bluetoothConnected = false;
                    BluetoothActivity.setBluetoothStateGui(bluetoothConnectionState.SYSTEM_DISCONNECTED);
                }
                attemptingBluetoothConnection = false;
            } catch (Throwable th) {
                bluetoothConnected = false;
                Application.toastMessage(th.toString());
                BluetoothActivity.setBluetoothStateGui(bluetoothConnectionState.SYSTEM_DISCONNECTED);
                attemptingBluetoothConnection = false;
            }
        }
    }

    private static int attemptingConnectionDeviceIndex;

    public static void connectBluetoothDeviceAsync(int deviceIndex){
        attemptingConnectionDeviceIndex = deviceIndex;
        if ((!isListAvailableBluetoothDevicesRunning)&&(!attemptingBluetoothConnection)&&isBluetoothAdapterReady()) {
            bluetoothConnectorExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    connectBluetoothDeviceSync(attemptingConnectionDeviceIndex);
                }
            });
        }
    }

    public static void sendCommandBuffer(byte[] data){
        try {
            outputStream.write(new byte[] {(byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F});
            outputStream.write(data,0, 20);
        }catch (Throwable th) {
            Application.toastMessage(th.toString());
        }
    }

    public static byte[] receiveCommandBuffer(int timeout){
        long startTimeMillis = System.currentTimeMillis();
        while((System.currentTimeMillis()-startTimeMillis)<500L){
            try {
                if (inputStream.available() >= 32) {
                    byte[] inputBuffer = new byte[32];
                    inputStream.read(inputBuffer, 0, 32);
                    return inputBuffer;
                }
            }catch (Throwable th){
                Application.toastMessage(th.toString());
            }
        }

        return null;
    }



    public static class BluetoothReceiverService{

        private static Executor receiverExecutor = Executors.newSingleThreadExecutor();
        private static Boolean terminate = false;
        private static Boolean isAutoConnectRunning = false;

        public static void initialize(){
            if (isAutoConnectRunning==false) {
                receiverExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        isAutoConnectRunning = true;
                        while (terminate == false) {
                            try {
                                if (inputStream.available()>= 32) {   //  >= 32
                                    byte[] inputBuffer = new byte[32];
                                    inputStream.read(inputBuffer, 0, 32);
                                    UnifiedTransmissionProtocol.command_buffer = inputBuffer;
                                    DeveloperConsoleActivity.sendTextToTerminal("Received from: " + UnifiedTransmissionProtocol.lastMessageTransmitterModule.getModuleName() + " : " + TerminalManager.insertSpaces(UnifiedTransmissionProtocol.bytesToHex(inputBuffer), " ", 2));
                                    if (UnifiedTransmissionProtocol.DecomposeMessageFromBuffer()== UnifiedTransmissionProtocol.CommandStatus.SUCCESSFUL_DECOMPOSITION) {
                                        UnifiedTransmissionProtocol.handleReceivedCommand();
                                    } else { Application.toastMessage("UNSUCCESSFUL DECOMPOSITION");}
                                }
                            } catch (Throwable th) {
                                Application.toastMessage(th.toString());
                            }
                        }
                        isAutoConnectRunning = false;
                    }
                });
            }
        }

        public static void terminateSync(){
            terminate = true;
            while(isAutoConnectRunning==true) { }
            terminate = false;
        }

    }







    private static Executor autoConnectExecutor = Executors.newSingleThreadExecutor();
    private static Boolean terminateAutoConnect = false;
    private static Boolean isAutoConnectRunning = false;

    public static void initBTAutoConnectService(){
        if (isAutoConnectRunning() == false) {
            autoConnectExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    isAutoConnectRunning = true;
                    try { Thread.sleep(5000);} catch (Throwable th) { th.printStackTrace(); }
                    while (!terminateAutoConnect && isBluetoothAdapterReady()) {
                        listAvailableBluetoothDevices();
                        while (isListAvailableBluetoothDevicesRunning) {}
                        for (int x = 0; x < bluetoothDevicesArray.length; x++) {
                            // MAC ADDRESS HC-05 : 98:D3:91:FD:38:05
                            // MAC ADDRESS XT1068 : 5C:51:88:A2:38:97
                            if (bluetoothDevicesArray[x].getAddress().compareToIgnoreCase("98:D3:91:FD:38:05") == 0) {
                                if (!terminateAutoConnect&&!bluetoothConnected) connectBluetoothDeviceSync(x);
                            }
                        }
                        try { Thread.sleep(10000);} catch (Throwable th) { th.printStackTrace(); }
                    }
                    isAutoConnectRunning = false;
                    terminateAutoConnect = false;
                }
            });
        }else{
            terminateAutoConnect = false;
        }
    }

    public static void terminateBTAutoConnect(){
        terminateAutoConnect = true;
    }

    public static Boolean isAutoConnectRunning(){
        return isAutoConnectRunning;
    }




    public static void connectionLost(){

    }

    public static void closeAllConnections(){
        try {
            bluetoothConnected = false;
            if (bluetoothComThread != null) bluetoothComThread.cancel(true);
            if (btSocket != null) btSocket.close();
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();

            btSocket = null;
            outputStream = null;
            inputStream = null;
        } catch (Throwable th){
            th.printStackTrace();
        }
    }



    public static BroadcastReceiver bluetoothAdapterStatusBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                final int stateCode = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (stateCode){
                    case BluetoothAdapter.STATE_OFF:
                        //Bluetooth adapter turned off handler
                        BluetoothActivity.setBluetoothStateGui(bluetoothConnectionState.BLUETOOTH_DISABLED);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        //Bluetooth adapter turning off handler
                        terminateBTAutoConnect();
                        BluetoothActivity.setBluetoothStateGui(bluetoothConnectionState.BT_TURNING_OFF);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        BluetoothActivity.setBluetoothStateGui(bluetoothConnectionState.SYSTEM_DISCONNECTED);
                        updateDevicesList();
                        initBTAutoConnectService();

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        //Bluetooth adapter turning on handler
                        BluetoothActivity.setBluetoothStateGui(bluetoothConnectionState.BT_TURNING_ON);
                        break;
                }
            }
        }
    };

    public static BroadcastReceiver bluetoothScanModeStatusBR = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:

                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:

                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:

                        break;
                }
            }
        }
    };

    public static BroadcastReceiver bluetoothDeviceStatusBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action){
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    terminateBTAutoConnect();
                    BluetoothReceiverService.initialize();
                    updateDevicesList();
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    BluetoothReceiverService.terminateSync();
                    closeAllConnections();
                    BluetoothActivity.setBluetoothStateGui(bluetoothConnectionState.SYSTEM_DISCONNECTED);
                    initBTAutoConnectService();
                    updateBluetoothListGUI();
                    //Application.toastMessage("Dispositivo desconectado!");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:

                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:

                    break;
            }
        }
    };

}
