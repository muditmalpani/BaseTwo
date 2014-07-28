package com.webtoapp.basetwo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.facebook.AppEventsLogger;
import com.google.analytics.tracking.android.EasyTracker;
import com.webtoapp.basetwo.game.GameLevel;
import com.webtoapp.basetwo.game.GameStats;
import com.webtoapp.basetwo.game.UserLevelStats;

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

        String stats = settings.getString("stats", null);
        if (stats == null) {
            addOldStatsIfPresent();
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

    private void addOldStatsIfPresent() {
        GameStats gameStats = new GameStats();
        SharedPreferences.Editor editor = settings.edit();
        for (GameLevel level : GameLevel.values()) {
            int levelId = level.levelId;
            int matches = settings.getInt("matches" + levelId, 0);
            if (matches > 0) {
                UserLevelStats stats = new UserLevelStats();
                stats.totalMatches = matches;
                stats.highestScore = settings.getInt("highScore" + levelId, 0);
                stats.totalScore = settings.getInt("totalScore" + levelId, 0);
                stats.highestTile = settings.getInt("highestTile" + levelId, 0);
                gameStats.addOldUserStats(level, stats);
                editor.remove("matches" + levelId)
                        .remove("highScore" + levelId)
                        .remove("totalScore" + levelId)
                        .remove("highestTile" + levelId);
            }
        }
        editor.putString("stats", gameStats.toString());
        editor.commit();
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

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(getApplicationContext(), "1453866754846254");
    }
}
