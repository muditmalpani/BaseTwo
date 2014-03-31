package com.webtoapp.basetwo.utils;

import android.view.MotionEvent;
import com.webtoapp.basetwo.game.DirectionType;
import com.webtoapp.basetwo.game.GameLevel;
import com.webtoapp.basetwo.game.SwipeDirection;
import com.webtoapp.basetwo.game.UserLevelStats;


public class GameUtils {
    private static final int SWIPE_MIN_DISTANCE = 150;
    private static final int SWIPE_MIN_SECONDARY_DISTANCE = 80;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    public static String getGameOverMessage(int score, GameLevel level, UserLevelStats stats) {
        String message = "GAME OVER!";
        if (score > level.excellentScoreLimit) {
            message = "EXCELLENT!";
        } else if (score > level.goodScoreLimit) {
            message = "WELL DONE!";
        } else if (score > level.averageScoreLimit) {
            message = "AVERAGE";
        }

        if (stats.highestScore <= score) {
            message = "HIGH SCORE!";
        }
        return message;
    }

    public static int getRating(int score, GameLevel level, UserLevelStats stats) {
        if (score < level.averageScoreLimit) {
            return 0;
        } else if (score < level.goodScoreLimit) {
            return 1;
        } else if (score < level.excellentScoreLimit) {
            return 2;
        }
        return 3;
    }

    public static SwipeDirection getDirection(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY,
            DirectionType type) {
        if (type == DirectionType.CARTESIAN) {
            return getCartesianDirection(e1, e2, velocityX, velocityY);
        } else {
            return getDiagonalDirection(e1, e2, velocityX, velocityY);
        }
    }

    public static SwipeDirection getCartesianDirection(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(velocityX) > Math.abs(velocityY) && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
                return SwipeDirection.LEFT;
            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
                return SwipeDirection.RIGHT;
            }
        } else if (Math.abs(velocityX) < Math.abs(velocityY) && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
            // bottom to top swipe
            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE) {
                return SwipeDirection.UP;
            }
            // left to right swipe
            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE) {
                return SwipeDirection.DOWN;
            }
        }
        return null;
    }

    public static SwipeDirection getDiagonalDirection(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY
                && Math.abs(e1.getX() - e2.getX()) > SWIPE_MIN_SECONDARY_DISTANCE
                && Math.abs(e1.getY() - e2.getY()) > SWIPE_MIN_DISTANCE) {
            // diagonal swipe
            if (e1.getX() - e2.getX() > 0) {
                // right to left
                if (e1.getY() - e2.getY() > 0) {
                    return SwipeDirection.UP_LEFT;
                } else {
                    return SwipeDirection.DOWN_LEFT;
                }
            } else {
                // left to right
                if (e1.getY() - e2.getY() > 0) {
                    return SwipeDirection.UP_RIGHT;
                } else {
                    return SwipeDirection.DOWN_RIGHT;
                }
            }
        } else if (Math.abs(velocityX) > Math.abs(velocityY) && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            // horizontal swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
                // right to left swipe
                return SwipeDirection.LEFT;
            }
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
                // left to right swipe
                return SwipeDirection.RIGHT;
            }
        }
        return null;
    }
}
