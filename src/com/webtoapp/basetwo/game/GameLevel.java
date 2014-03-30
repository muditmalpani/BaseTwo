package com.webtoapp.basetwo.game;

import android.util.SparseArray;

public enum GameLevel {
    BEGINNER(2, "Beginner", DirectionType.CARTESIAN, 21, 50, 59),
    INTERMEDIATE(4, "Intermediate", DirectionType.CARTESIAN, 6000, 20000, 35000),
    TRIMEDIATE(5, "TriMediate", DirectionType.DIAGONAL, 15000, 40000, 70000),
    HEXAMDIATE(6, "Hexamediate", DirectionType.DIAGONAL, 30000, 80000, 150000),
    EXPERT(7, "Expert", DirectionType.CARTESIAN, 2000, 8000, 15000);

    public int levelId;
    public String name;
    public DirectionType directionType;
    public int averageScoreLimit;
    public int goodScoreLimit;
    public int excellentScoreLimit;

    private GameLevel(int level, String name, DirectionType directionType, int avgScoreLimit, int goodScoreLimit,
            int excellentScoreLimit) {
        this.levelId = level;
        this.name = name;
        this.directionType = directionType;
        this.averageScoreLimit = avgScoreLimit;
        this.goodScoreLimit = goodScoreLimit;
        this.excellentScoreLimit = excellentScoreLimit;
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
