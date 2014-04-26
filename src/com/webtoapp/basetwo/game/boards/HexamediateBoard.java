package com.webtoapp.basetwo.game.boards;

import android.content.Context;
import android.widget.TableLayout;
import com.webtoapp.basetwo.game.Cell;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class HexamediateBoard extends AbstractBoard implements Board {
    // CONSTRUCTORS
    protected static Board getEmptyBoard(Context context, TableLayout view) {
        HexamediateBoard b = new HexamediateBoard(context, view);
        b.addRows();
        b.addValueToRandomPosition();
        b.addValueToRandomPosition();
        return b;
    }

    protected static Board fromString(Context context, TableLayout view, String boardStr) {
        try {
            JSONObject json = new JSONObject(boardStr);
            return new HexamediateBoard(context, view, json);
        } catch (Exception e) {
            e.printStackTrace();
            return getEmptyBoard(context, view);
        }
    }

    private HexamediateBoard(Context context, TableLayout view, JSONObject json) throws JSONException {
        this.size = 5;
        this.numEmptyCells = json.getInt("numEmptyCells");
        this.score = json.getInt("score");
        this.boardView = view;
        this.context = context;
        this.rows = new Row[size];
        String board = json.getString("board");
        parseBoardStr(board);
    }

    private HexamediateBoard(Context context, TableLayout view) {
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
            for (int row = 0; row < size; row++) {
                int index = row <= size / 2 ? i : (i - row + (size - 1) / 2);
                if (index >= 0 && index < rows[row].size) {
                    cellList.add(rows[row].cells[index]);
                }
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
            for (int row = 0; row < size; row++) {
                int index = row <= size / 2 ? i : (i - row + (size - 1) / 2);
                if (index >= 0 && index < rows[row].size) {
                    cellList.add(rows[row].cells[index]);
                }
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
            for (int row = 0; row < size; row++) {
                int index = row >= size / 2 ? i : (i + row - (size - 1) / 2);
                if (index >= 0 && index < rows[row].size) {
                    cellList.add(rows[row].cells[index]);
                }
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
            for (int row = 0; row < size; row++) {
                int index = row >= size / 2 ? i : (i + row - (size - 1) / 2);
                if (index >= 0 && index < rows[row].size) {
                    cellList.add(rows[row].cells[index]);
                }
            }
            Collections.reverse(cellList);
            didMove |= updateCellsAndScore(cellList);
        }
        return didMove;
    }

    @Override
    protected boolean isDiagonalMovePossible() {
        //check for similar cells in front diagonal
        for (int i = 0; i < size; i++) {
            int prev = -1;
            for (int row = 0; row < size; row++) {
                int index = row <= size / 2 ? i : (i - row + (size - 1) / 2);
                int v = 0;
                if (index >= 0 && index < rows[row].size) {
                    v = rows[row].cells[index].value();
                }
                if (v != 0) {
                    if (prev != -1 && v == prev) {
                        return true;
                    }
                    prev = v;
                } else {
                    prev = -1;
                }
            }
        }

        //check for similar cells in back diagonal
        for (int i = 0; i < size; i++) {
            int prev = -1;
            for (int row = 0; row < size; row++) {
                int index = row >= size / 2 ? i : (i + row - (size - 1) / 2);
                int v = 0;
                if (index >= 0 && index < rows[row].size) {
                    v = rows[row].cells[index].value();
                }
                if (v != 0) {
                    if (prev != -1 && v == prev) {
                        return true;
                    }
                    prev = v;
                } else {
                    prev = -1;
                }
            }
        }

        return false;
    }

    @Override
    protected void addRows() {
        for (int row = 0; row < size; row++) {
            int rowSize = size - Math.abs(row - (size - 1) / 2);
            Row r = new Row(rowSize, context);
            r.addCells();
            boardView.addView(r.rowView);
            rows[row] = r;
        }
    }

    @Override
    protected int getBoardSize() {
        return size * size - 6;
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

}
