package com.webtoapp.basetwo.game;

import android.util.SparseArray;

public enum GameLevel {
    BEGINNER(2, "Beginner", DirectionType.CARTESIAN),
    INTERMEDIATE(4, "Intermediate", DirectionType.CARTESIAN),
    TRIMEDIATE(5, "TriMediate", DirectionType.DIAGONAL),
    HEXAMDIATE(6, "Hexamediate", DirectionType.DIAGONAL),
    EXPERT(7, "Expert", DirectionType.CARTESIAN);

    public int levelId;
    public String name;
    public DirectionType directionType;

    private GameLevel(int level, String name, DirectionType directionType) {
        this.levelId = level;
        this.name = name;
        this.directionType = directionType;
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
