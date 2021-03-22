package com.example.SAPLM;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;

import com.example.SAPLM.bluetoothActivities.BluetoothConnection;
import com.example.SAPLM.bluetoothActivities.UnifiedTransmissionProtocol;

import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GoogleSpeechRecognizer extends MainActivity implements RecognitionListener {

    private static SpeechRecognizer speechRecognizer = null;

    private boolean forceCancel = false;

    public static void startSpeechListening(){


        String language =  "es-ES";
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, Application.getActivity().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);
        if (Application.isSystemOnline()==false) { intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,true); }
        speechRecognizer.startListening(intent);


        if (Application.isSystemOnline()) {
            Thread autoStop = new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(4000);
                        forceStopRecognizer();
                    } catch (Throwable th) {
                        Application.toastMessage(th.toString());
                    }
                }
            };
            autoStop.start();
        }

    }

    public static void setupRecognizer(){
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(Application.getContext());
        speechRecognizer.setRecognitionListener(new GoogleSpeechRecognizer());
    }

    public static void shutdownRecognizer(){
        speechRecognizer.stopListening();
        speechRecognizer.cancel();
        speechRecognizer.destroy();
        speechRecognizer = null;
        System.gc();
    }

    public static void forceStopRecognizer(){
        Application.getActivity().runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        speechRecognizer.stopListening();
                        MicrophoneHandler.setState(MicrophoneHandler.MicrophoneState.LISTENING_KEYOWRD);
                    }
                }
        );

    }


    public static void analiseListenerResults(ArrayList<String> data, float[] confidenceScore){
        outerloop:
        for (int i = 0; i < data.size(); i++) {
            String hypothesis = Normalizer.normalize(((String) data.get(i)).toLowerCase(), Normalizer.Form.NFKD);
            String[] hypothesisSplit = hypothesis.split(" ");

            if (Arrays.asList(hypothesisSplit).contains("encender") && Arrays.asList(hypothesisSplit).contains("velador")) {
                UnifiedTransmissionProtocol.modulesList.get(2).getDevicesList().get(0).setData(Boolean.TRUE);
                UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                break outerloop;
            }

            if (Arrays.asList(hypothesisSplit).contains("encender") && Arrays.asList(hypothesisSplit).contains("estufa")) {
                UnifiedTransmissionProtocol.modulesList.get(2).getDevicesList().get(3).setData(Boolean.TRUE);
                UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                break outerloop;
            }

            if (Arrays.asList(hypothesisSplit).contains("encender") && Arrays.asList(hypothesisSplit).contains("ventilador")) {
                UnifiedTransmissionProtocol.modulesList.get(2).getDevicesList().get(2).setData(Boolean.TRUE);
                UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                break outerloop;
            }

            if (Arrays.asList(hypothesisSplit).contains("encender") && Arrays.asList(hypothesisSplit).contains("luz")) {
                UnifiedTransmissionProtocol.modulesList.get(2).getDevicesList().get(1).setData(Boolean.TRUE);
                UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                break outerloop;
            }

            if (Arrays.asList(hypothesisSplit).contains("apagar") && Arrays.asList(hypothesisSplit).contains("velador")) {
                UnifiedTransmissionProtocol.modulesList.get(2).getDevicesList().get(0).setData(Boolean.FALSE);
                UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                break outerloop;
            }

            if (Arrays.asList(hypothesisSplit).contains("apagar") && Arrays.asList(hypothesisSplit).contains("estufa")) {
                UnifiedTransmissionProtocol.modulesList.get(2).getDevicesList().get(3).setData(Boolean.FALSE);
                UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                break outerloop;
            }

            if (Arrays.asList(hypothesisSplit).contains("apagar") && Arrays.asList(hypothesisSplit).contains("ventilador")) {
                UnifiedTransmissionProtocol.modulesList.get(2).getDevicesList().get(2).setData(Boolean.FALSE);
                UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                break outerloop;
            }

            if (Arrays.asList(hypothesisSplit).contains("apagar") && Arrays.asList(hypothesisSplit).contains("luz")) {
                UnifiedTransmissionProtocol.modulesList.get(2).getDevicesList().get(1).setData(Boolean.FALSE);
                UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                break outerloop;
            }

            if (Arrays.asList(hypothesisSplit).contains("encender") && Arrays.asList(hypothesisSplit).contains("todo")) {
                for(UnifiedTransmissionProtocol.Module.Device dev : UnifiedTransmissionProtocol.modulesList.get(2).getDevicesList()) {
                    dev.setData(Boolean.TRUE);
                }
                UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                break outerloop;
            }

            if (Arrays.asList(hypothesisSplit).contains("apagar") && Arrays.asList(hypothesisSplit).contains("todo")) {
                for(UnifiedTransmissionProtocol.Module.Device dev : UnifiedTransmissionProtocol.modulesList.get(2).getDevicesList()) {
                    dev.setData(Boolean.FALSE);
                }
                UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                break outerloop;
            }

            if (Arrays.asList(hypothesisSplit).contains("llamar") && Arrays.asList(hypothesisSplit).contains("enfermera")) {
                UnifiedTransmissionProtocol.modulesList.get(3).getDevicesList().get(2).setData(Boolean.TRUE);
                UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                UnifiedTransmissionProtocol.Module.Device.autoToggleNurseStateThread = new Thread(UnifiedTransmissionProtocol.Module.Device.autoToggleNurseStateRunnable);
                UnifiedTransmissionProtocol.Module.Device.autoToggleNurseStateThread.start();
                break outerloop;
            }

            if (Arrays.asList(hypothesisSplit).contains("llamar") && Arrays.asList(hypothesisSplit).contains("enfermeria")) {
                UnifiedTransmissionProtocol.modulesList.get(3).getDevicesList().get(2).setData(Boolean.TRUE);
                UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                UnifiedTransmissionProtocol.Module.Device.autoToggleNurseStateThread = new Thread(UnifiedTransmissionProtocol.Module.Device.autoToggleNurseStateRunnable);
                UnifiedTransmissionProtocol.Module.Device.autoToggleNurseStateThread.start();
                break outerloop;
            }

            if (Arrays.asList(hypothesisSplit).contains("subir") && Arrays.asList(hypothesisSplit).contains("toda") && Arrays.asList(hypothesisSplit).contains("cortina")) {
                UnifiedTransmissionProtocol.modulesList.get(3).getDevicesList().get(1).setData((byte)7);
                UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                break outerloop;
            }

            if (Arrays.asList(hypothesisSplit).contains("bajar") && Arrays.asList(hypothesisSplit).contains("toda") && Arrays.asList(hypothesisSplit).contains("cortina")) {
                UnifiedTransmissionProtocol.modulesList.get(3).getDevicesList().get(1).setData((byte)0);
                UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                break outerloop;
            }

            if (Arrays.asList(hypothesisSplit).contains("subir") && Arrays.asList(hypothesisSplit).contains("toda") && Arrays.asList(hypothesisSplit).contains("camilla")) {
                UnifiedTransmissionProtocol.modulesList.get(3).getDevicesList().get(0).setData((byte)3);
                UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                break outerloop;
            }

            if (Arrays.asList(hypothesisSplit).contains("bajar") && Arrays.asList(hypothesisSplit).contains("toda") && Arrays.asList(hypothesisSplit).contains("camilla")) {
                UnifiedTransmissionProtocol.modulesList.get(3).getDevicesList().get(0).setData((byte)0);
                UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                break outerloop;
            }


            if (Arrays.asList(hypothesisSplit).contains("encender") && Arrays.asList(hypothesisSplit).contains("rele")) {
                for (String word : hypothesisSplit) {
                    if (StringUtils.isNumeric(word) == true) {
                        int number = Integer.parseInt(word);
                        if (number<4) {
                            UnifiedTransmissionProtocol.modulesList.get(2).getDevicesList().get(number).setData(Boolean.TRUE);
                            UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                        }
                        break outerloop;
                    }
                }
            }

            if (Arrays.asList(hypothesisSplit).contains("apagar") && Arrays.asList(hypothesisSplit).contains("rele")) {
                for (String word : hypothesisSplit) {
                    if (StringUtils.isNumeric(word) == true) {
                        int number = Integer.parseInt(word);
                        if (number<4) {
                            UnifiedTransmissionProtocol.modulesList.get(2).getDevicesList().get(number).setData(Boolean.FALSE);
                            UnifiedTransmissionProtocol.Module.syncAllDevicesValueToSystem();
                        }
                        break outerloop;
                    }
                }
            }

            if (Arrays.asList(hypothesisSplit).contains("decir") && Arrays.asList(hypothesisSplit).contains("hora")){
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("d-MMMM-yyyy", new Locale("spa"));
                String todayAsString = df.format(currentTime);
                SimpleDateFormat onlyTimeFormar = new SimpleDateFormat("HH:mm:ss", new Locale("spa"));
                String onlyTimeString = onlyTimeFormar.format(currentTime);

                //TextToSpeechHandler.initializeTextToSpeech();
                TextToSpeechHandler.addToSpeakingQueue("Hoy es " + todayAsString + "y son las" + onlyTimeString);
                Application.toastMessage("Decir hora");
            }

        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        MicrophoneHandler.setState(MicrophoneHandler.MicrophoneState.LISTENING_COMMAND);
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

        //Application.toastMessage("End of speech");
    }

    @Override
    public void onError(int error) {
        //Application.toastMessage("On error" + error);

        if (forceCancel==false) {
            PocketsphinxListener.startListening();
            MicrophoneHandler.setState(MicrophoneHandler.MicrophoneState.LISTENING_KEYOWRD);
        }
    }

    @Override
    public void onResults(Bundle results) {
        String str = new String();
        ArrayList<String> resultsArrayList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        float[] confidenceScore = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
        analiseListenerResults(resultsArrayList, confidenceScore);

        StringBuilder builder = new StringBuilder();
        for (String line : resultsArrayList) {
            builder.append(line + "\n");
        }

        MainActivity.textView_speechRecognitionResults.setText(builder.toString());
        MainActivity.textView_speechRecognitionResults.setVisibility(View.VISIBLE);



        MicrophoneHandler.setState(MicrophoneHandler.MicrophoneState.LISTENING_KEYOWRD);

        PocketsphinxListener.startListening();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        String str = new String();
        ArrayList<String> resultsArrayList = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        float[] confidenceScore = partialResults.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);

        StringBuilder builder = new StringBuilder();
        for (String line : resultsArrayList) {
            builder.append(line + "\n");
        }

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }
}
