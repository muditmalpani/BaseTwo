package com.webtoapp.basetwo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.analytics.tracking.android.EasyTracker;

public class MainMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void quitApp(View view) {
        this.finish();
    }

    public void showStats(View view) {
        Intent startIntent = new Intent(this.getApplicationContext(), StatisticsActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.getApplicationContext().startActivity(startIntent);
    }

    public void showInstructions(View view) {
        Intent startIntent = new Intent(this.getApplicationContext(), InstructionsActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.getApplicationContext().startActivity(startIntent);
    }

    public void showLevels(View view) {
        Intent startIntent = new Intent(this.getApplicationContext(), LevelSelectActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.getApplicationContext().startActivity(startIntent);
    }

    public void playGame(View view) {
        Intent startIntent = new Intent(this.getApplicationContext(), GameActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.getApplicationContext().startActivity(startIntent);
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
