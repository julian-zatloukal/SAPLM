package com.example.SAPLM.settingsActivities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.SAPLM.Application;
import com.example.SAPLM.R;

public class DeveloperConsoleActivity extends AppCompatActivity {
    private static Toolbar toolbar;
    private static ImageButton enterButton;
    private static ScrollView scrollViewTerminal;
    private static EditText editTextTerminal;
    private static LinearLayout terminalLinearLayout;
    private static TextView mainTextView;

    private static final String mainText_KEY = "MAIN_TEXT_KEY";
    private static final String ongoingText_KEY = "ONGOING_TEXT_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.developer_console_layout);
        Application.setActivity(this);
        Application.setContext(this);

        toolbar = this.findViewById(R.id.toolbar);
        enterButton = this.findViewById(R.id.enterButton);
        scrollViewTerminal = this.findViewById(R.id.scrollViewTerminal);
        editTextTerminal = this.findViewById(R.id.editTextTerminal);
        terminalLinearLayout = this.findViewById(R.id.terminalLinearLayout);




        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        mainTextView = new TextView(this);
        mainTextView.setHint("SAPLM TERMINAL");
        mainTextView.setTextColor(Color.BLACK);
        mainTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 54.0f /getResources().getDisplayMetrics().density);
        mainTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mainTextView.setTypeface(Typeface.MONOSPACE);
        terminalLinearLayout.addView(mainTextView);



        scrollViewTerminal.fullScroll(View.FOCUS_DOWN);

    }



    @Override
    protected void onPause() {
        if(mainTextView!=null) {
            try {
                Application.persistentDataSave.putString(ongoingText_KEY, editTextTerminal.getText().toString());
            }catch (Throwable th){}
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (Application.persistentDataSave.containsKey(mainText_KEY)&&mainTextView!=null) {
            try {
                mainTextView.setText(Html.fromHtml(Application.persistentDataSave.getString(mainText_KEY), Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);
                editTextTerminal.setText(Application.persistentDataSave.getString(ongoingText_KEY));
                scrollViewTerminal.fullScroll(View.FOCUS_DOWN);
            }catch (Throwable th){}
        }
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public static void sendTextToTerminal(Spanned newText){

        String newString = Application.persistentDataSave.getString(mainText_KEY) + Html.toHtml(newText, Html.FROM_HTML_MODE_LEGACY);
        Application.persistentDataSave.putString(mainText_KEY,newString);

        if (Looper.myLooper() == Looper.getMainLooper()){
            mainTextView.setText(Html.fromHtml(newString, Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);
            scrollViewTerminal.fullScroll(View.FOCUS_DOWN);
        } else {
            Application.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainTextView.setText(Html.fromHtml(Application.persistentDataSave.getString(mainText_KEY), Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);
                    scrollViewTerminal.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }

    public static void sendTextToTerminal(String newText){

        Spanned spannedConverted = Html.fromHtml(newText,Html.FROM_HTML_MODE_LEGACY);

        String newString = Application.persistentDataSave.getString(mainText_KEY) + Html.toHtml(spannedConverted, Html.FROM_HTML_MODE_LEGACY);
        Application.persistentDataSave.putString(mainText_KEY,newString);

        if (mainTextView!=null) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                mainTextView.setText(Html.fromHtml(newString, Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);
                scrollViewTerminal.fullScroll(View.FOCUS_DOWN);
            } else {
                Application.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainTextView.setText(Html.fromHtml(Application.persistentDataSave.getString(mainText_KEY), Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);
                        scrollViewTerminal.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }
    }



    public static void clearTerminalText(){
        Application.persistentDataSave.putString(mainText_KEY,"");

        if (Looper.myLooper() == Looper.getMainLooper()){
            mainTextView.setText("");
            scrollViewTerminal.fullScroll(View.FOCUS_DOWN);
        } else {
            Application.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainTextView.setText("");
                    scrollViewTerminal.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }


    public void onClickEnterButton(View view){
        sendTextToTerminal(Html.fromHtml(editTextTerminal.getText().toString(),Html.FROM_HTML_MODE_LEGACY));
        TerminalManager.handleTerminalInput(editTextTerminal.getText().toString());
    }

    public void onClickClearButton(View view){
        clearTerminalText();
    }


}
