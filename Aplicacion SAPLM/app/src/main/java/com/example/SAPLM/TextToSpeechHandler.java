package com.example.SAPLM;

import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;

import java.util.Locale;
import java.util.Set;

public class TextToSpeechHandler {

    private static TextToSpeech textToSpeech;
    public static boolean isInitialized = false;

    public static void initializeTextToSpeech(){
        if (isInitialized == false) {
            textToSpeech = new TextToSpeech(Application.getContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    try {
                        if (status != TextToSpeech.ERROR) {
                            textToSpeech.setLanguage(new Locale("spa"));
                            //textToSpeech.setLanguage(Locale.UK);
                            //textToSpeech.setOnUtteranceProgressListener(utteranceListener);
                            textToSpeech.setPitch(1.0f);
                            textToSpeech.setSpeechRate(1.0f);
                            textToSpeech.getVoices();
                            isInitialized = true;
                        }
                    } catch (Throwable th) {
                        Application.toastMessage(th.toString());
                    }
                }
            });
        }
    }

    public static void addToSpeakingQueue(String text){
        try {
            if (isInitialized) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, "232");
            }
        } catch (Throwable th) {
            th.printStackTrace();
            Application.toastMessage(th.toString());
        }
    }

    public static void shutdownTextSpeech(){
        if(textToSpeech != null) {

            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    public static Set<Voice> getVoicesList(){
        if (isInitialized){
            return textToSpeech.getVoices();
        } else {
            return null;
        }
    }

    public static void setVoice(Voice targetVoice){
        if(isInitialized) {
            textToSpeech.setVoice(targetVoice);
        }
    }



}


