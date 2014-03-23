package com.webtoapp.basetwo.utils;

import android.view.MotionEvent;
import com.webtoapp.basetwo.game.Board;
import com.webtoapp.basetwo.game.SwipeDirection;


public class GameUtils {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    public static String getGameOverMessage(Board board, int level) {
        String message = "GAME OVER!";
        int lowerLimit = 21;
        int upperLimit = 50;
        if (level == 2) {
            lowerLimit = 500;
            upperLimit = 1100;
        } else if (level == 3) {
            lowerLimit = 18000;
            upperLimit = 25000;
        }
        if (board.score() > upperLimit) {
            message = "EXCELLENT!";
        } else if (board.score() > lowerLimit) {
            message = "WELL DONE!";
        }
        return message;
    }

    public static SwipeDirection getDirection(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
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
}
