package com.webtoapp.basetwo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.analytics.tracking.android.EasyTracker;

public class MainMenuActivity extends Activity {
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        settings = getSharedPreferences(GameActivity.PREFS_NAME, 0);
        String boardStr = settings.getString("board", null);
        if (boardStr != null) {
            Button playButton = (Button) findViewById(R.id.play_btn);
            playButton.setText("Resume");
        }
    }

    public void quitApp(View view) {
        this.finish();
    }

    public void showStats(View view) {
        Intent startIntent = new Intent(this.getApplicationContext(), StatisticsActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startIntent);
    }

    public void showInstructions(View view) {
        Intent startIntent = new Intent(this.getApplicationContext(), InstructionsActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startIntent);
    }

    public void showLevels(View view) {
        Intent startIntent = new Intent(this, LevelSelectActivity.class);
        startActivityForResult(startIntent, LevelSelectActivity.ACTIVITY_CODE);
    }

    public void playGame(View view) {
        Intent startIntent = new Intent(this, GameActivity.class);
        startActivityForResult(startIntent, GameActivity.ACTIVITY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String boardStr = settings.getString("board", null);
        Button playButton = (Button) findViewById(R.id.play_btn);
        if (boardStr != null) {
            playButton.setText("Resume");
        } else {
            playButton.setText("Play");
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
