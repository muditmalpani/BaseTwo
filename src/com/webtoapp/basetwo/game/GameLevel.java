package com.webtoapp.basetwo.game;

import android.util.SparseArray;

public enum GameLevel {
    BEGINNER(2, "Beginner", DirectionType.CARTESIAN, 21, 50, 59, 1),
    INTERMEDIATE(4, "Intermediate", DirectionType.CARTESIAN, 6000, 20000, 35000, 3),
    TRIMEDIATE(5, "TriMediate", DirectionType.DIAGONAL, 15000, 40000, 70000, 4),
    HEXAMDIATE(6, "Hexamediate", DirectionType.DIAGONAL, 30000, 80000, 150000, 6),
    EXPERT(7, "Expert", DirectionType.CARTESIAN, 2000, 8000, 15000, 6);

    public int levelId;
    public String name;
    public DirectionType directionType;
    public int averageScoreLimit;
    public int goodScoreLimit;
    public int excellentScoreLimit;
    public int scoreMultiplier;

    private GameLevel(int level, String name, DirectionType directionType, int avgScoreLimit, int goodScoreLimit,
            int excellentScoreLimit, int multiplier) {
        this.levelId = level;
        this.name = name;
        this.directionType = directionType;
        this.averageScoreLimit = avgScoreLimit;
        this.goodScoreLimit = goodScoreLimit;
        this.excellentScoreLimit = excellentScoreLimit;
        this.scoreMultiplier = multiplier;
    }

    private static final SparseArray<GameLevel> levelIdToGameLevel =
            new SparseArray<GameLevel>() {
                {
                    for (GameLevel gameLevel : GameLevel.values()) {
                        append(gameLevel.levelId, gameLevel);
                    }
                }
            };

    public static GameLevel fromId(int id) {
        return levelIdToGameLevel.get(id);
    }
}
