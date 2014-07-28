package com.webtoapp.basetwo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import com.facebook.AppEventsLogger;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.gson.Gson;
import com.webtoapp.basetwo.game.GameLevel;
import com.webtoapp.basetwo.game.GameStats;
import com.webtoapp.basetwo.game.UserLevelStats;
import com.webtoapp.basetwo.utils.GameUtils;

public class StatisticsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        SharedPreferences settings = getSharedPreferences(GameActivity.PREFS_NAME, MODE_PRIVATE);
        String statsStr = settings.getString("stats", null);
        GameStats stats = new GameStats();
        if (statsStr != null) {
            Gson gson = new Gson();
            stats = gson.fromJson(statsStr, GameStats.class);
        }

        TableLayout statsTable = (TableLayout) findViewById(R.id.stats_table);
        for (GameLevel level : GameLevel.values()) {
            addStatsViewForLevel(stats, level, statsTable);
        }

        addLevelAndPoints(stats);
    }

    public void addLevelAndPoints(GameStats gameStats) {
        int level = gameStats.userLevel;
        int pointsForCurrentLevel = level * (level + 1) * 50;
        int pointsForNextLevel = (level + 1) * (level + 2) * 50;
        int pointsNeeded = pointsForNextLevel - gameStats.totalPoints;

        TextView levelView = (TextView) findViewById(R.id.level_stat_view);
        levelView.setText(String.valueOf(level));

        TextView pointsNeededView = (TextView) findViewById(R.id.points_needed_stat_view);
        pointsNeededView.setText(String.valueOf(pointsNeeded));

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.points_stat_view);
        progressBar.setMax(pointsForNextLevel - pointsForCurrentLevel);
        progressBar.setProgress(gameStats.totalPoints - pointsForCurrentLevel);
    }

    public void addStatsViewForLevel(GameStats gameStats, GameLevel level, TableLayout statsTable) {
        UserLevelStats userStats = gameStats.getUserStatsForLevel(level);
        if (userStats == null) {
            userStats = new UserLevelStats();
        }
        addHeaderRow(level, statsTable);

        int avgScore = userStats.totalMatches == 0 ? 0 : userStats.totalScore / userStats.totalMatches;
        addStatRow("Games Played", userStats.totalMatches, statsTable);
        addStatRow("Highest Score", userStats.highestScore, statsTable);
        addStatRow("Highest Tile", userStats.highestTile, statsTable);
        addStatRow("Average Score", avgScore, statsTable);
        addRatingRow(gameStats, level, statsTable);
    }

    private void addHeaderRow(GameLevel level, TableLayout statsTable) {
        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        headerRow.setBackgroundColor(Color.parseColor("#50000000"));
        headerRow.setPadding(5, 10, 5, 5);
        statsTable.addView(headerRow);

        TextView tv = new TextView(this);
        tv.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tv.setText(level.name + " Level Stats");
        tv.setTextColor(Color.parseColor("#CCCCCC"));
        tv.setTextSize(22);
        tv.setTypeface(null, Typeface.BOLD);
        headerRow.addView(tv);
    }

    private void addStatRow(String statName, int statValue, TableLayout statsTable) {
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tr.setPadding(5, 5, 5, 5);
        statsTable.addView(tr);

        TextView statNameView = new TextView(this);
        statNameView.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        statNameView.setText(statName);
        statNameView.setTextColor(Color.parseColor("#DDDDDD"));
        statNameView.setTextSize(18);
        tr.addView(statNameView);

        TextView statValueView = new TextView(this);
        statValueView.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        statValueView.setText(String.valueOf(statValue));
        statValueView.setTextColor(Color.parseColor("#DDDDDD"));
        statValueView.setTextSize(18);
        tr.addView(statValueView);
    }

    private void addRatingRow(GameStats gameStats, GameLevel level, TableLayout statsTable) {
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tr.setPadding(5, 5, 5, 5);
        statsTable.addView(tr);

        TextView statNameView = new TextView(this);
        statNameView.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        statNameView.setText("Rating");
        statNameView.setTextColor(Color.parseColor("#DDDDDD"));
        statNameView.setTextSize(18);
        tr.addView(statNameView);

        LinearLayout stars = new LinearLayout(this);
        int starSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30,
                getResources().getDisplayMetrics());
        stars.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        UserLevelStats userStatsForLevel = gameStats.getUserStatsForLevel(level);
        int rating = userStatsForLevel == null ? 0 : GameUtils.getRating(userStatsForLevel.highestScore, level,
                userStatsForLevel);
        for (int i = 0; i < 3; i++) {
            ImageView star = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(starSize, starSize);
            params.setMargins(0, 0, 10, 0);
            star.setLayoutParams(params);
            if (i < rating) {
                star.setImageResource(R.drawable.star);
            } else {
                star.setImageResource(R.drawable.empty_star);
            }
            star.setScaleType(ScaleType.FIT_XY);
            stars.addView(star);
        }
        tr.addView(stars);
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
