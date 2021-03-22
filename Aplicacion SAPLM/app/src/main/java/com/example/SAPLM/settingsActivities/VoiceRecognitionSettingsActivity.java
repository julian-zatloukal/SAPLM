package com.example.SAPLM.settingsActivities;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.tts.Voice;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.SAPLM.Application;
import com.example.SAPLM.R;
import com.example.SAPLM.TextToSpeechHandler;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class VoiceRecognitionSettingsActivity extends AppCompatActivity {
    private static Toolbar toolbar;

    private Button speech_test_btn;
    private Button set_voice_btn;
    private EditText phrase_editText;
    private static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_recognition_settings_layout);
        Application.setActivity(this);
        Application.setContext(this);

        toolbar = this.findViewById(R.id.toolbar);
        speech_test_btn = this.findViewById(R.id.speech_test_btn);
        set_voice_btn = this.findViewById(R.id.set_voice_btn);
        phrase_editText = this.findViewById(R.id.phrase_editText);

        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        TextToSpeechHandler.initializeTextToSpeech();


        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        Sprite fadingCircle = new FadingCircle();
        fadingCircle.setColor(R.color.colorPrimary);
        progressBar.setIndeterminateDrawable(fadingCircle);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void speechTestOnClick(View view){
        if (TextToSpeechHandler.isInitialized){
            //TextToSpeechHandler.addToSpeakingQueue(phrase_editText.getText().toString());

            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("d-MMMM-yyyy", new Locale("spa"));
            String todayAsString = df.format(currentTime);
            SimpleDateFormat onlyTimeFormar = new SimpleDateFormat("HH:mm:ss", new Locale("spa"));
            String onlyTimeString = onlyTimeFormar.format(currentTime);

            TextToSpeechHandler.addToSpeakingQueue("Hoy es " + todayAsString + "y son las" + onlyTimeString);

        }else{
            Application.toastMessage("Not initialized");
        }
    }

    public static ArrayList<String> voiceName= new ArrayList<>();
    public static Voice[] voiceArray;

    public void setVoiceOnClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(Application.getContext());

        voiceArray = TextToSpeechHandler.getVoicesList().toArray(new Voice[TextToSpeechHandler.getVoicesList().size()]);

        for(Voice voice : voiceArray){
            voiceName.add(voice.getName());
        }

        builder.setTitle("Seleccionar voz");
        builder.setItems(voiceName.toArray(new String[voiceName.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextToSpeechHandler.setVoice(voiceArray[which]);
            }
        });
        builder.show();
    }

    @Override
    protected void onPause() {
        if (TextToSpeechHandler.isInitialized){
            //TextToSpeechHandler.shutdownTextSpeech();
        }

        super.onPause();
    }
}
