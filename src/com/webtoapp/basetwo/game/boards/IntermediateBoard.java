package com.webtoapp.basetwo.game.boards;

import android.content.Context;
import android.widget.TableLayout;
import org.json.JSONException;
import org.json.JSONObject;

public final class IntermediateBoard extends NormalBoard implements Board {
    // CONSTRUCTORS
    protected static Board getEmptyBoard(Context context, TableLayout view) {
        IntermediateBoard b = new IntermediateBoard(context, view);
        b.addRows();
        b.addValueToRandomPosition();
        b.addValueToRandomPosition();
        return b;
    }

    protected static Board fromString(Context context, TableLayout view, String boardStr) {
        try {
            JSONObject json = new JSONObject(boardStr);
            return new IntermediateBoard(context, view, json);
        } catch (Exception e) {
            e.printStackTrace();
            return getEmptyBoard(context, view);
        }
    }

    private IntermediateBoard(Context context, TableLayout view, JSONObject json) throws JSONException {
        this.size = 4;
        this.numEmptyCells = json.getInt("numEmptyCells");
        this.score = json.getInt("score");
        this.boardView = view;
        this.context = context;
        this.rows = new Row[size];
        String board = json.getString("board");
        parseBoardStr(board);
    }

    private IntermediateBoard(Context context, TableLayout view) {
        this.size = 4;
        this.numEmptyCells = getBoardSize();
        this.score = 0;
        this.boardView = view;
        this.context = context;
        this.rows = new Row[size];
    }
}
