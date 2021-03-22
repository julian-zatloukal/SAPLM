package com.example.SAPLM;

import android.graphics.PorterDuff;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MicrophoneHandler extends MainActivity {

    public enum MicrophoneState { INHIBITED, LISTENING_KEYOWRD, LISTENING_COMMAND}

    static private MicrophoneState currentState = MicrophoneState.LISTENING_KEYOWRD;

    public static void microphoneOnClick(){
        switch (currentState){
            case LISTENING_KEYOWRD:
                PocketsphinxListener.stopRecognizer();
                setState(MicrophoneState.INHIBITED);
                break;
            case LISTENING_COMMAND:
                GoogleSpeechRecognizer.forceStopRecognizer();
                //PocketsphinxListener.startListening();
                setState(MicrophoneState.LISTENING_KEYOWRD);
                break;
            case INHIBITED:
                setState(MicrophoneState.LISTENING_KEYOWRD);
                PocketsphinxListener.startListening();
                break;
        }

    }

    public static MicrophoneState getState(){
        return currentState;
    }

    public static void setState(MicrophoneState desiredState){
        switch (desiredState){
            case INHIBITED:
                imgBtn_microphoneBackground.clearColorFilter();
                imgBtn_microphoneBackground.setColorFilter(Application.getContext().getColor(R.color.ColorBackMicInhibited), PorterDuff.Mode.SRC_ATOP);
                imgBtn_microphone.setImageResource(R.drawable.ic_main_mic_off);
                endAnimation();
                currentState = MicrophoneState.INHIBITED;
                break;
            case LISTENING_KEYOWRD:
                imgBtn_microphoneBackground.clearColorFilter();
                imgBtn_microphoneBackground.setColorFilter(Application.getContext().getColor(R.color.ColorBackMicKeyword), PorterDuff.Mode.SRC_ATOP);
                imgBtn_microphone.setImageResource(R.drawable.ic_main_mic_listening);
                endAnimation();
                currentState = MicrophoneState.LISTENING_KEYOWRD;
                break;
            case LISTENING_COMMAND:
                imgBtn_microphoneBackground.clearColorFilter();
                imgBtn_microphoneBackground.setColorFilter(Application.getContext().getColor(R.color.ColorBackMicCommand), PorterDuff.Mode.SRC_ATOP);
                imgBtn_microphone.setImageResource(R.drawable.ic_main_mic_listening);
                startAnimation();
                currentState = MicrophoneState.LISTENING_COMMAND;
                break;
        }
    }

    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    private static boolean terminateAnimation = false;
    private static boolean animationHasFinished = false;

    public static void startAnimation(){
        terminateAnimation = false;
        Future<Void> future = executor.submit(new Callable<Void>() {
            public Void call() throws Exception {
                try {
                    while (terminateAnimation == false) {
                        imgBtn_microphoneBackground.animate().scaleX(0.90f).setDuration(350).setInterpolator(new AccelerateDecelerateInterpolator());
                        imgBtn_microphone.animate().scaleX(0.90f).setDuration(350).setInterpolator(new AccelerateDecelerateInterpolator());
                        imgBtn_microphoneBackground.animate().scaleY(0.90f).setDuration(350).setInterpolator(new AccelerateDecelerateInterpolator());
                        imgBtn_microphone.animate().scaleY(0.90f).setDuration(350).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                animationHasFinished = true;
                            }
                        });
                        while(animationHasFinished==false);
                        animationHasFinished= false;
                        imgBtn_microphoneBackground.animate().scaleX(1.00f).setDuration(350).setInterpolator(new AccelerateDecelerateInterpolator());
                        imgBtn_microphone.animate().scaleX(1.00f).setDuration(350).setInterpolator(new AccelerateDecelerateInterpolator());
                        imgBtn_microphoneBackground.animate().scaleY(1.00f).setDuration(350).setInterpolator(new AccelerateDecelerateInterpolator());
                        imgBtn_microphone.animate().scaleY(1.00f).setDuration(350).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                animationHasFinished = true;
                            }
                        });;
                        while(animationHasFinished==false);
                        animationHasFinished = false;
                    }
                    terminateAnimation = false;
                }catch (Throwable th){ ;
                    th.printStackTrace();
                }
                return null;
            }
        });
    }

    public static void endAnimation(){
        terminateAnimation = true;
    }

}
