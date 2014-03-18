package com.webtoapp.basetwo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;

public class StatisticsActivity extends Activity {

    private class Stats {
        public int matches;
        public int highScore;
        public int avgScore;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        SharedPreferences settings = getSharedPreferences(GameActivity.PREFS_NAME, 0);
        setEasyStats(settings);
        setMediumStats(settings);
        setHardStats(settings);
    }

    public Stats getStats(SharedPreferences settings, int level) {
        int size = level + 1;
        Stats stats = new Stats();
        stats.matches = settings.getInt("matches" + size, 0);
        stats.highScore = settings.getInt("highScore" + size, 0);
        int totalScore = settings.getInt("totalScore" + size, 0);
        stats.avgScore = stats.matches == 0 ? 0 : totalScore / stats.matches;
        return stats;
    }

    public void setEasyStats(SharedPreferences settings) {
        Stats stats = getStats(settings, 1);
        TextView matches = (TextView) findViewById(R.id.easy_matches);
        matches.setText(String.valueOf(stats.matches));
        TextView highScore = (TextView) findViewById(R.id.easy_high_score);
        highScore.setText(String.valueOf(stats.highScore));
        TextView avgScore = (TextView) findViewById(R.id.easy_avg_score);
        avgScore.setText(String.valueOf(stats.avgScore));
    }

    public void setMediumStats(SharedPreferences settings) {
        Stats stats = getStats(settings, 2);
        TextView matches = (TextView) findViewById(R.id.medium_matches);
        matches.setText(String.valueOf(stats.matches));
        TextView highScore = (TextView) findViewById(R.id.medium_high_score);
        highScore.setText(String.valueOf(stats.highScore));
        TextView avgScore = (TextView) findViewById(R.id.medium_avg_score);
        avgScore.setText(String.valueOf(stats.avgScore));
    }

    public void setHardStats(SharedPreferences settings) {
        Stats stats = getStats(settings, 3);
        TextView matches = (TextView) findViewById(R.id.hard_matches);
        matches.setText(String.valueOf(stats.matches));
        TextView highScore = (TextView) findViewById(R.id.hard_high_score);
        highScore.setText(String.valueOf(stats.highScore));
        TextView avgScore = (TextView) findViewById(R.id.hard_avg_score);
        avgScore.setText(String.valueOf(stats.avgScore));
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
