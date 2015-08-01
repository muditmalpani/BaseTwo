package com.webtoapp.basetwo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.adjust.sdk.Adjust;
import com.facebook.AppEventsLogger;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.gson.Gson;
import com.webtoapp.basetwo.game.GameStats;

public class LevelSelectActivity extends Activity {
    public static final int ACTIVITY_CODE = 2;
    private GameStats stats;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        settings = getSharedPreferences(GameActivity.PREFS_NAME, 0);
        String statsStr = settings.getString("stats", null);
        if (statsStr != null) {
            Gson gson = new Gson();
            stats = gson.fromJson(statsStr, GameStats.class);
        }

        Button b = null;
        switch (stats.gameLevel) {
            case 2:
                b = (Button) findViewById(R.id.level1_btn);
                break;
            case 5:
                b = (Button) findViewById(R.id.level3_btn);
                break;
            case 6:
                b = (Button) findViewById(R.id.level4_btn);
                break;
            default:
                b = (Button) findViewById(R.id.level2_btn);
                break;
        }
        if (b != null) {
            b.setBackgroundResource(R.drawable.button_selected);
        }
    }

    public void chooseLevel(View view) {
        int new_level = stats.gameLevel;
        switch (view.getId()) {
            case R.id.level1_btn:
                new_level = 2;
                break;
            case R.id.level2_btn:
                new_level = 4;
                break;
            case R.id.level3_btn:
                new_level = 5;
                break;
            case R.id.level4_btn:
                new_level = 6;
                break;
        }
        if (new_level != stats.gameLevel) {
            stats.gameLevel = new_level;
            if (settings.contains("board")) {
                new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("This will end your current saved game. Are you sure you want to change the level?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                settings.edit().putString("stats", stats.toString()).remove("board").commit();
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            } else {
                settings.edit().putString("stats", stats.toString()).commit();
                finish();
            }
        } else {
            finish();
        }
    }

    // Google Analytics
    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Adjust.onResume(this);
        AppEventsLogger.activateApp(getApplicationContext(), "1453866754846254");
    }

    @Override
    public void onPause() {
        super.onPause();
        Adjust.onPause();
    }

}
