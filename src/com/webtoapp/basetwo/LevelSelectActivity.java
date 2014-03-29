package com.webtoapp.basetwo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.analytics.tracking.android.EasyTracker;

public class LevelSelectActivity extends Activity {
    public static final int ACTIVITY_CODE = 2;
    private int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        SharedPreferences settings = getSharedPreferences(GameActivity.PREFS_NAME, 0);
        level = settings.getInt("level", 4);
        Button b = null;
        switch (level) {
            case 2:
                b = (Button) findViewById(R.id.level1_btn);
                break;
            case 5:
                b = (Button) findViewById(R.id.level2_btn);
                break;
            case 6:
                b = (Button) findViewById(R.id.level4_btn);
                break;
            default:
                b = (Button) findViewById(R.id.level3_btn);
                break;
        }
        if (b != null) {
            b.setBackgroundResource(R.drawable.button_selected);
        }
    }

    public void chooseLevel(View view) {
        int new_level = level;
        switch (view.getId()) {
            case R.id.level1_btn:
                new_level = 2;
                break;
            case R.id.level2_btn:
                new_level = 5;
                break;
            case R.id.level3_btn:
                new_level = 4;
                break;
            case R.id.level4_btn:
                new_level = 6;
                break;
        }
        if (new_level != level) {
            level = new_level;
            final SharedPreferences settings = getSharedPreferences(GameActivity.PREFS_NAME, 0);
            if (settings.contains("board")) {
                new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("This will end your current saved game. Are you sure you want to change the level?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                settings.edit().putInt("level", level).remove("board").commit();
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
                settings.edit().putInt("level", level).commit();
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

}
