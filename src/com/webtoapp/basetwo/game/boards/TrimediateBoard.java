package com.webtoapp.basetwo.game.boards;

import android.content.Context;
import android.widget.TableLayout;
import com.webtoapp.basetwo.game.Cell;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public final class TrimediateBoard extends AbstractBoard implements Board {
    // CONSTRUCTORS
    protected static Board getEmptyBoard(Context context, TableLayout view) {
        TrimediateBoard b = new TrimediateBoard(context, view);
        b.addRows();
        b.addValueToRandomPosition();
        b.addValueToRandomPosition();
        return b;
    }

    protected static Board fromString(Context context, TableLayout view, String boardStr) {
        try {
            JSONObject json = new JSONObject(boardStr);
            return new TrimediateBoard(context, view, json);
        } catch (Exception e) {
            e.printStackTrace();
            return getEmptyBoard(context, view);
        }
    }

    private TrimediateBoard(Context context, TableLayout view, JSONObject json) throws JSONException {
        this.size = 5;
        this.numEmptyCells = json.getInt("numEmptyCells");
        this.score = json.getInt("score");
        this.boardView = view;
        this.context = context;
        this.rows = new Row[size];
        String board = json.getString("board");
        parseBoardStr(board);
    }

    private TrimediateBoard(Context context, TableLayout view) {
        this.size = 5;
        this.numEmptyCells = getBoardSize();
        this.score = 0;
        this.boardView = view;
        this.context = context;
        this.rows = new Row[size];
    }

    protected boolean pushUpRight() {
        numEmptyCells = 0;
        boolean didMove = false;
        for (int i = 0; i < size; i++) {
            List<Cell> cellList = new ArrayList<Cell>();
            for (int row = i; row < size; row++) {
                cellList.add(rows[row].cells[i]);
            }
            didMove |= updateCellsAndScore(cellList);
        }
        return didMove;
    }

    protected boolean pushDownLeft() {
        numEmptyCells = 0;
        boolean didMove = false;
        for (int i = 0; i < size; i++) {
            List<Cell> cellList = new ArrayList<Cell>();
            for (int row = i; row < size; row++) {
                cellList.add(rows[row].cells[i]);
            }
            Collections.reverse(cellList);
            didMove |= updateCellsAndScore(cellList);
        }
        return didMove;
    }

    protected boolean pushUpLeft() {
        numEmptyCells = 0;
        boolean didMove = false;
        for (int i = 0; i < size; i++) {
            List<Cell> cellList = new ArrayList<Cell>();
            for (int row = i; row < size; row++) {
                cellList.add(rows[row].cells[row - i]);
            }
            didMove |= updateCellsAndScore(cellList);
        }
        return didMove;
    }

    protected boolean pushDownRight() {
        numEmptyCells = 0;
        boolean didMove = false;
        for (int i = 0; i < size; i++) {
            List<Cell> cellList = new ArrayList<Cell>();
            for (int row = i; row < size; row++) {
                cellList.add(rows[row].cells[row - i]);
            }
            Collections.reverse(cellList);
            didMove |= updateCellsAndScore(cellList);
        }
        return didMove;
    }

    @Override
    protected void addRows() {
        for (int row = 0; row < size; row++) {
            Row r = new Row(row + 1, context);
            r.addCells();
            boardView.addView(r.rowView);
            rows[row] = r;
        }
    }

    @Override
    public boolean isFull() {
        if (numEmptyCells != 0) {
            return false;
        }
        // if no empty cell, check if no further move can be made
        boolean canMove = isHorizontalMovePossible() || isDiagonalMovePossible();

        return !canMove;
    }

    @Override
    protected int getBoardSize() {
        return size * (size + 1) / 2;
    }

}
