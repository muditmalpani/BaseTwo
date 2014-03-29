package com.webtoapp.basetwo.game;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

public class GameStats {
    public int gameLevel;
    public int userLevel;
    public int totalPoints;
    private Map<GameLevel, UserLevelStats> userStatsByLevel;

    public GameStats() {
        gameLevel = GameLevel.INTERMEDIATE.levelId;
        userLevel = 0;
        totalPoints = 0;
        userStatsByLevel = new HashMap<GameLevel, UserLevelStats>();
    }

    public void updateUserStatsForGame(GameLevel level, int score, int highestTile) {
        UserLevelStats userStats = userStatsByLevel.get(level);
        if (userStats == null) {
            userStats = new UserLevelStats();
            userStatsByLevel.put(level, userStats);
        }
        userStats.totalMatches += 1;
        userStats.totalScore += score;
        userStats.highestScore = Math.max(score, userStats.highestScore);
        userStats.highestTile = Math.max(highestTile, userStats.highestTile);
    }

    public UserLevelStats getUserStatsForLevel(GameLevel level) {
        return userStatsByLevel.get(level);
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // For Backward compatibility of data storage
    @Deprecated
    public void addOldUserStats(GameLevel level, UserLevelStats stats) {
        userStatsByLevel.put(level, stats);
    }
}
