package com.example.SAPLM;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.SAPLM.bluetoothActivities.BluetoothConnection;
import com.example.SAPLM.settingsActivities.settings_layout;

public class MainActivity extends AppCompatActivity {

    static ImageButton imgBtn_microphone;
    static ImageButton imgBtn_microphoneBackground;
    static ImageButton imgBtn_commands;
    static ImageButton imgBtn_contacts;
    static ImageButton imgBtn_bluetooth;
    static ImageButton imgBtn_settings;
    static ImageButton imgBtn_menu;
    static MainMenuHandler mainMenuHandler;
    static TextView textView_speechRecognitionResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {

            mainMenuHandler = new MainMenuHandler(this, this);
            Application.setActivity(this);
            Application.setContext(this);
            MainMenuHandler.dpiConstant = ((float) this.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);

            imgBtn_microphone = findViewById(R.id.imgBtn_microphone);
            imgBtn_microphoneBackground = findViewById(R.id.imgBtn_microphone_background);
            imgBtn_commands = findViewById(R.id.imgBtn_commands);
            imgBtn_contacts = findViewById(R.id.imgBtn_contacts);
            imgBtn_bluetooth = findViewById(R.id.imgBtn_bluetooth);
            imgBtn_settings = findViewById(R.id.imgBtn_settings);
            imgBtn_menu = findViewById(R.id.imgBtn_menu);
            textView_speechRecognitionResults = findViewById(R.id.textView_speechRecognitionResults);

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            527);
                }
            } else {
                GoogleSpeechRecognizer.setupRecognizer();
                PocketsphinxListener.setupRecognizer();
                PocketsphinxListener.startListening();

            }

            settings_layout.setupSettingsList();

            BluetoothConnection.initBTAutoConnectService();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 527: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    GoogleSpeechRecognizer.setupRecognizer();
                    PocketsphinxListener.setupRecognizer();
                    PocketsphinxListener.startListening();

                } else { }
            }
        }
    }

    public void onClickListener_menu(View view){ mainMenuHandler.menuButtonOnClick(); }

    public void onClickListener_commands(View view){ mainMenuHandler.commandsButtonOnClick(); }

    public void onClickListener_settings(View view){ mainMenuHandler.settingsButtonOnClick(); }

    public void onClickListener_contacts(View view){ mainMenuHandler.contactsButtonOnClick(); }

    public void onClickListener_bluetooth(View view){ mainMenuHandler.bluetoothButtonOnClick(); }

    public void onClickListener_microphone(View view){ MicrophoneHandler.microphoneOnClick(); }

    @Override
    public void onDestroy() {

        PocketsphinxListener.shutdownRecognizer();
        GoogleSpeechRecognizer.shutdownRecognizer();
        BluetoothConnection.closeAllConnections();
        TextToSpeechHandler.shutdownTextSpeech();

        super.onDestroy();
    }

}
