package com.example.SAPLM.commandActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.view.MenuItem;
import android.view.View;

import com.example.SAPLM.Application;
import com.example.SAPLM.R;
import com.example.SAPLM.bluetoothActivities.BluetoothConnection;
import com.example.SAPLM.settingsActivities.AvailableSetting;
import com.example.SAPLM.settingsActivities.RecyclerTouchListener;
import com.example.SAPLM.settingsActivities.SettingsLayoutAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandsActivity extends AppCompatActivity {
    private static Toolbar toolbar;

    public static List<CommandItem> commandList = new ArrayList<>();
    private RecyclerView recyclerView;
    public static CommandsListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commands_layout);
        Application.setActivity(this);
        Application.setContext(this);

        toolbar = this.findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        recyclerView = (RecyclerView) findViewById(R.id.commandsRecyclerView);

        mAdapter = new CommandsListAdapter(commandList);

        recyclerView.setHasFixedSize(true);

        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // horizontal RecyclerView
        // keep movie_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        //recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                CommandItem commandItem = commandList.get(position);
                if (commandItem.getItemType()== CommandItem.ItemType.PARENT){

                }else{
                    commandItem.onClickHandler();
                }


                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        if (commandList.isEmpty()) setupCommandsList();

        mAdapter.notifyDataSetChanged();
    }


    public static void updateGUI(){
        if (mAdapter!=null) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                mAdapter.notifyDataSetChanged();
            } else {
                Application.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public static void setupCommandsList(){
        //commandList.add(new CommandItem("MODULO PRINCIPAL","MAIN_BOARD", new ArrayList(Arrays.asList("LED"))));

        commandList.add(new CommandItem(1));
        commandList.add(new CommandItem(1,0));
        commandList.add(new CommandItem(1, 1));

        commandList.add(new CommandItem(2));
        commandList.add(new CommandItem(2,0));
        commandList.add(new CommandItem(2, 1));
        commandList.add(new CommandItem(2, 2));
        commandList.add(new CommandItem(2, 3));

        commandList.add(new CommandItem(3));
        commandList.add(new CommandItem(3,0));
        commandList.add(new CommandItem(3, 1));
        commandList.add(new CommandItem(3, 2));
    }
}
