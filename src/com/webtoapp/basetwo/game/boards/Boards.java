package com.webtoapp.basetwo.game.boards;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TableLayout;
import com.webtoapp.basetwo.game.GameLevel;

public class Boards {
    public static Board init(SharedPreferences settings, Context context, TableLayout view, GameLevel level) {
        String boardStr = settings.getString("board", null);
        if (boardStr == null) {
            return getEmptyBoard(context, view, level);
        }
        return getBoardFromString(context, view, boardStr, level);
    }

    private static Board getEmptyBoard(Context context, TableLayout view, GameLevel level) {
        switch (level) {
            case BEGINNER:
                return BeginnerBoard.getEmptyBoard(context, view);
            case INTERMEDIATE:
                return IntermediateBoard.getEmptyBoard(context, view);
            case TRIMEDIATE:
                return TrimediateBoard.getEmptyBoard(context, view);
            case HEXAMDIATE:
                return HexamediateBoard.getEmptyBoard(context, view);
            default:
                return null;
        }
    }

    private static Board getBoardFromString(Context context, TableLayout view, String boardStr, GameLevel level) {
        switch (level) {
            case BEGINNER:
                return BeginnerBoard.fromString(context, view, boardStr);
            case INTERMEDIATE:
                return IntermediateBoard.fromString(context, view, boardStr);
            case TRIMEDIATE:
                return TrimediateBoard.fromString(context, view, boardStr);
            case HEXAMDIATE:
                return HexamediateBoard.fromString(context, view, boardStr);
            default:
                return null;
        }
    }
}
