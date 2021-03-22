package com.example.SAPLM;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.SAPLM.bluetoothActivities.BluetoothActivity;
import com.example.SAPLM.commandActivity.CommandsActivity;
import com.example.SAPLM.settingsActivities.settings_layout;

public class MainMenuHandler extends MainActivity{

    public static float dpiConstant;
    public static float radialMenuRadius = 263.0f;
    public static boolean menuUncoiled = false;
    public static boolean isAnimationRunning = false;
    public static Context mainMenuContext;
    public static Activity mainMenuActivity;

    public MainMenuHandler(Context context, Activity activity){
        this.mainMenuActivity = activity;
        this.mainMenuContext = context;
    }

    public static void menuButtonOnClick(){
        long animationDuration = 850;

        if(isAnimationRunning==false) {
            isAnimationRunning = true;

            if (menuUncoiled == false) {
                ValueAnimator animatorCommands = ValueAnimator.ofFloat(360, 72);
                animatorCommands.setDuration(animationDuration);
                animatorCommands.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorCommands.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = ((Float) (animation.getAnimatedValue()))
                                .floatValue();

                        float translationX = (float) (Math.cos(value * (Math.PI / 180.0f)) * (radialMenuRadius / 2.0f) - (radialMenuRadius / 2.0f));
                        float translationY = (float) (Math.sqrt(-Math.pow(translationX, 2.0d) - radialMenuRadius * translationX));
                        if (value > 180.0f) {
                            translationY = -translationY;
                        }

                        imgBtn_commands.setTranslationX(convertDpToPixel(translationX));
                        imgBtn_commands.setTranslationY(convertDpToPixel(translationY));

                    }
                });
                imgBtn_commands.setVisibility(View.VISIBLE);
                animatorCommands.start();

                ValueAnimator animatorContacts = ValueAnimator.ofFloat(360, 144);
                animatorContacts.setDuration(animationDuration);
                animatorContacts.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorContacts.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = ((Float) (animation.getAnimatedValue()))
                                .floatValue();

                        float translationX = (float) (Math.cos(value * (Math.PI / 180.0f)) * (radialMenuRadius / 2.0f) - (radialMenuRadius / 2.0f));
                        float translationY = (float) (Math.sqrt(-Math.pow(translationX, 2.0d) - radialMenuRadius * translationX));
                        if (value > 180.0f) {
                            translationY = -translationY;
                        }

                        imgBtn_contacts.setTranslationX(convertDpToPixel(translationX));
                        imgBtn_contacts.setTranslationY(convertDpToPixel(translationY));

                    }
                });
                imgBtn_contacts.setVisibility(View.VISIBLE);
                animatorContacts.start();

                ValueAnimator animatorSettings = ValueAnimator.ofFloat(360, 216);
                animatorSettings.setDuration(animationDuration);
                animatorSettings.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorSettings.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = ((Float) (animation.getAnimatedValue()))
                                .floatValue();

                        float translationX = (float) (Math.cos(value * (Math.PI / 180.0f)) * (radialMenuRadius / 2.0f) - (radialMenuRadius / 2.0f));
                        float translationY = (float) (Math.sqrt(-Math.pow(translationX, 2.0d) - radialMenuRadius * translationX));
                        if (value > 180.0f) {
                            translationY = -translationY;
                        }

                        imgBtn_settings.setTranslationX(convertDpToPixel(translationX));
                        imgBtn_settings.setTranslationY(convertDpToPixel(translationY));

                    }
                });
                imgBtn_settings.setVisibility(View.VISIBLE);
                animatorSettings.start();

                ValueAnimator animatorBluetooth = ValueAnimator.ofFloat(360, 288);
                animatorBluetooth.setDuration(animationDuration);
                animatorBluetooth.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorBluetooth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = ((Float) (animation.getAnimatedValue()))
                                .floatValue();

                        float translationX = (float) (Math.cos(value * (Math.PI / 180.0f)) * (radialMenuRadius / 2.0f) - (radialMenuRadius / 2.0f));
                        float translationY = (float) (Math.sqrt(-Math.pow(translationX, 2.0d) - radialMenuRadius * translationX));
                        if (value > 180.0f) {
                            translationY = -translationY;
                        }

                        imgBtn_bluetooth.setTranslationX(convertDpToPixel(translationX));
                        imgBtn_bluetooth.setTranslationY(convertDpToPixel(translationY));

                    }
                });
                imgBtn_bluetooth.setVisibility(View.VISIBLE);
                animatorBluetooth.start();

                imgBtn_commands.animate().alpha(1.0f).setDuration(animationDuration).setInterpolator(new AccelerateDecelerateInterpolator());
                imgBtn_contacts.animate().alpha(1.0f).setDuration(animationDuration).setInterpolator(new AccelerateDecelerateInterpolator());
                imgBtn_settings.animate().alpha(1.0f).setDuration(animationDuration).setInterpolator(new AccelerateDecelerateInterpolator());
                imgBtn_bluetooth.animate().alpha(1.0f).setDuration(animationDuration).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        isAnimationRunning = false;
                    }
                });

                menuUncoiled = true;


            } else {


                ValueAnimator animatorCommands = ValueAnimator.ofFloat(72, 360);
                animatorCommands.setDuration(animationDuration);
                animatorCommands.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorCommands.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = ((Float) (animation.getAnimatedValue()))
                                .floatValue();

                        float translationX = (float) (Math.cos(value * (Math.PI / 180.0f)) * (radialMenuRadius / 2.0f) - (radialMenuRadius / 2.0f));
                        float translationY = (float) (Math.sqrt(-Math.pow(translationX, 2.0d) - radialMenuRadius * translationX));
                        if (value > 180.0f) {
                            translationY = -translationY;
                        }

                        imgBtn_commands.setTranslationX(convertDpToPixel(translationX));
                        imgBtn_commands.setTranslationY(convertDpToPixel(translationY));

                    }
                });
                imgBtn_commands.setVisibility(View.VISIBLE);
                animatorCommands.start();

                ValueAnimator animatorContacts = ValueAnimator.ofFloat(144, 360);
                animatorContacts.setDuration(animationDuration);
                animatorContacts.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorContacts.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = ((Float) (animation.getAnimatedValue()))
                                .floatValue();

                        float translationX = (float) (Math.cos(value * (Math.PI / 180.0f)) * (radialMenuRadius / 2.0f) - (radialMenuRadius / 2.0f));
                        float translationY = (float) (Math.sqrt(-Math.pow(translationX, 2.0d) - radialMenuRadius * translationX));
                        if (value > 180.0f) {
                            translationY = -translationY;
                        }

                        imgBtn_contacts.setTranslationX(convertDpToPixel(translationX));
                        imgBtn_contacts.setTranslationY(convertDpToPixel(translationY));

                    }
                });
                imgBtn_contacts.setVisibility(View.VISIBLE);
                animatorContacts.start();

                ValueAnimator animatorSettings = ValueAnimator.ofFloat(216, 360);
                animatorSettings.setDuration(animationDuration);
                animatorSettings.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorSettings.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = ((Float) (animation.getAnimatedValue()))
                                .floatValue();

                        float translationX = (float) (Math.cos(value * (Math.PI / 180.0f)) * (radialMenuRadius / 2.0f) - (radialMenuRadius / 2.0f));
                        float translationY = (float) (Math.sqrt(-Math.pow(translationX, 2.0d) - radialMenuRadius * translationX));
                        if (value > 180.0f) {
                            translationY = -translationY;
                        }

                        imgBtn_settings.setTranslationX(convertDpToPixel(translationX));
                        imgBtn_settings.setTranslationY(convertDpToPixel(translationY));

                    }
                });
                imgBtn_settings.setVisibility(View.VISIBLE);
                animatorSettings.start();

                ValueAnimator animatorBluetooth = ValueAnimator.ofFloat(288, 360);
                animatorBluetooth.setDuration(animationDuration);
                animatorBluetooth.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorBluetooth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = ((Float) (animation.getAnimatedValue()))
                                .floatValue();

                        float translationX = (float) (Math.cos(value * (Math.PI / 180.0f)) * (radialMenuRadius / 2.0f) - (radialMenuRadius / 2.0f));
                        float translationY = (float) (Math.sqrt(-Math.pow(translationX, 2.0d) - radialMenuRadius * translationX));
                        if (value > 180.0f) {
                            translationY = -translationY;
                        }

                        imgBtn_bluetooth.setTranslationX(convertDpToPixel(translationX));
                        imgBtn_bluetooth.setTranslationY(convertDpToPixel(translationY));

                    }
                });
                imgBtn_bluetooth.setVisibility(View.VISIBLE);
                animatorBluetooth.start();

                imgBtn_commands.animate().alpha(0.0f).setDuration(animationDuration).setInterpolator(new AccelerateDecelerateInterpolator());
                imgBtn_contacts.animate().alpha(0.0f).setDuration(animationDuration).setInterpolator(new AccelerateDecelerateInterpolator());
                imgBtn_settings.animate().alpha(0.0f).setDuration(animationDuration).setInterpolator(new AccelerateDecelerateInterpolator());
                imgBtn_bluetooth.animate().alpha(0.0f).setDuration(animationDuration).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        isAnimationRunning = false;
                    }
                });

                menuUncoiled = false;
            }
        }
    }

    public static void commandsButtonOnClick(){
        Context currentContext = Application.getContext();
        Activity currentActivity = Application.getActivity();
        Intent i = new Intent(currentContext, CommandsActivity.class);
        currentContext.startActivity(i);
    }

    public static void contactsButtonOnClick(){
        Context currentContext = Application.getContext();
        Activity currentActivity = Application.getActivity();
        Intent i = new Intent(currentContext, ContactsActivity.class);
        currentContext.startActivity(i);
    }

    public static void bluetoothButtonOnClick(){
        Context currentContext = Application.getContext();
        Activity currentActivity = Application.getActivity();
        Intent i = new Intent(currentContext, BluetoothActivity.class);
        currentContext.startActivity(i);
    }

    public static void settingsButtonOnClick(){
        Intent i = new Intent(Application.getContext(), settings_layout.class);
        mainMenuContext.startActivity(i);
        //mainMenuActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }








    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp){ return dp * dpiConstant; }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px){
        return px / dpiConstant;
    }
}
