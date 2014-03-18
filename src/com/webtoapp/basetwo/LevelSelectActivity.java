package com.webtoapp.basetwo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.analytics.tracking.android.EasyTracker;

public class LevelSelectActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        SharedPreferences settings = getSharedPreferences(GameActivity.PREFS_NAME, 0);
        int level = settings.getInt("level", 0);
        Button b = null;
        switch (level) {
            case 1:
                b = (Button) findViewById(R.id.level1_btn);
                break;
            case 2:
                b = (Button) findViewById(R.id.level2_btn);
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
        int level = 3;
        switch (view.getId()) {
            case R.id.level1_btn:
                level = 1;
                break;
            case R.id.level2_btn:
                level = 2;
                break;
            case R.id.level3_btn:
                level = 3;
                break;
        }
        SharedPreferences settings = getSharedPreferences(GameActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("level", level);
        editor.commit();
        this.finish();
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
