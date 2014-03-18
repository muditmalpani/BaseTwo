package com.webtoapp.basetwo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
}
