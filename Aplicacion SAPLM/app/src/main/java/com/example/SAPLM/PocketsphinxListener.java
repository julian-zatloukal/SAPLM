package com.example.SAPLM;

import java.io.File;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public final class PocketsphinxListener implements RecognitionListener {
    private static SpeechRecognizer recognizer;
    private static String KEYPHRASE = "asistente";
    private static final String KWS_SEARCH = "wakeup";


    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();

        if (text.contains(KEYPHRASE)) {

            Application.toastMessage(KEYPHRASE);
            recognizer.stop();
            //recognizer.cancel();
            //recognizer.shutdown();
            GoogleSpeechRecognizer.startSpeechListening();
            //recognizer.startListening(KWS_SEARCH);
        }

    }


    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();

        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onEndOfSpeech() {

    }

    public static void setupRecognizer(){
        try {
            Assets assets = new Assets(Application.getContext());
            File assetDir = assets.syncAssets();
            recognizer = SpeechRecognizerSetup.defaultSetup()
                    .setAcousticModel(new File(assetDir, "es-es-ptm"))
                    .setDictionary(new File(assetDir, "cmudict-es-es.dict"))
                    .setKeywordThreshold(Float.MIN_VALUE
                    )
                    .setRawLogDir(assetDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                    .getRecognizer();
            recognizer.addListener(new PocketsphinxListener());
            recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
        } catch (Throwable th){
            Application.toastMessage(th.toString());
        }
    }

    public static void startListening(){
        recognizer.startListening(KWS_SEARCH);
    }

    public static void shutdownRecognizer(){
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    public static void stopRecognizer(){
        recognizer.stop();
    }

    @Override
    public void onError(Exception error) {
        Application.toastMessage("Error: " + error.toString());
    }

    @Override
    public void onTimeout() {
        Application.toastMessage("Timeout!");
    }




}
