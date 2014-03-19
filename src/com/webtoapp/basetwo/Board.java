package com.webtoapp.basetwo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class Board {
    private Cell[][] cells;
    private int size;
    private Context context;
    private TableLayout boardView;
    private int numEmptyCells;
    private int score;

    public static Board init(SharedPreferences settings, Context context, TableLayout view) {
        int level = settings.getInt("level", 3);
        int size = level + 1;

        String boardStr = settings.getString("board", null);
        if (boardStr != null) {
            try {
                JSONObject json = new JSONObject(boardStr);
                return new Board(json, context, view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Board b = new Board(size, context, view);
        b.addCells();
        b.addValueToRandomPosition();
        b.addValueToRandomPosition();
        return b;
    }

    private Board(JSONObject json, Context context, TableLayout view) throws JSONException {
        size = json.getInt("size");
        numEmptyCells = json.getInt("numEmptyCells");
        score = json.getInt("score");
        boardView = view;
        this.context = context;
        cells = new Cell[size][size];
        String board = json.getString("board");
        parseBoardStr(board);
    }

    private Board(int size, Context context, TableLayout view) {
        this.size = size;
        cells = new Cell[size][size];
        boardView = view;
        this.context = context;
        numEmptyCells = size * size;
        score = 0;
    }

    public void parseBoardStr(String board) {
        String[] rows = board.split("\n");
        int i = 0;
        for (String row : rows) {
            TableRow tr = new TableRow(context);
            tr.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            String[] cellsInRow = row.split(",");
            int j = 0;
            for (String cell : cellsInRow) {
                Cell c = new Cell(context);
                c.setValue(Integer.valueOf(cell));
                cells[i][j] = c;
                tr.addView(c.view);
                j++;
            }
            boardView.addView(tr);
            i++;
        }
    }

    public void addCells() {
        for (int row = 0; row < size; row++) {
            TableRow tr = new TableRow(context);
            tr.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            for (int col = 0; col < size; col++) {
                Cell c = new Cell(context);
                cells[row][col] = c;
                tr.addView(c.view);
            }
            boardView.addView(tr);
        }
    }

    private int getRandomValue() {
        if (size < 3) {
            return 2;
        }
        double x = Math.random();
        if (x < 0.8) {
            return 2;
        }
        return 4;
    }

    public void addValueToRandomPosition() {
        Cell cell = getRandomEmptyCell();
        if (cell != null) {
            cell.setValue(getRandomValue());
        }
        numEmptyCells--;
    }

    public Cell getRandomEmptyCell() {
        if (numEmptyCells == 0) {
            return null;
        }
        int i = (int) (Math.random() * size * size);
        int row = (int) i / size;
        int col = i % size;
        if (cells[row][col].value() == 0) {
            return cells[row][col];
        }
        return getRandomEmptyCell();
    }

    // returns true if no further move is possible
    public boolean isFull() {
        if (numEmptyCells != 0) {
            return false;
        }
        // if no empty cell, check if no further move can be made

        // check for similar continuous cells in row
        for (int row = 0; row < size; row++) {
            int prev = -1;
            for (int col = 0; col < size; col++) {
                int v = cells[row][col].value();
                if (v != 0) {
                    if (prev != -1 && v == prev) {
                        return false;
                    }
                    prev = v;
                } else {
                    prev = -1;
                }
            }
        }

        // check for similar continuous cells in column
        for (int col = 0; col < size; col++) {
            int prev = -1;
            for (int row = 0; row < size; row++) {
                int v = cells[row][col].value();
                if (v != 0) {
                    if (prev != -1 && v == prev) {
                        return false;
                    }
                    prev = v;
                } else {
                    prev = -1;
                }
            }
        }
        return true;
    }

    public boolean pushUp() {
        numEmptyCells = 0;
        boolean didMove = false;
        for (int col = 0; col < size; col++) {
            List<Cell> cellList = new ArrayList<Cell>();
            for (int row = 0; row < size; row++) {
                cellList.add(cells[row][col]);
            }
            didMove |= updateCellsAndScore(cellList);
        }
        return didMove;
    }

    public boolean pushDown() {
        numEmptyCells = 0;
        boolean didMove = false;
        for (int col = 0; col < size; col++) {
            List<Cell> cellList = new ArrayList<Cell>();
            for (int row = size - 1; row >= 0; row--) {
                cellList.add(cells[row][col]);
            }
            didMove |= updateCellsAndScore(cellList);
        }
        return didMove;
    }

    public boolean pushLeft() {
        numEmptyCells = 0;
        boolean didMove = false;
        for (int row = 0; row < size; row++) {
            List<Cell> cellList = new ArrayList<Cell>();
            for (int col = 0; col < size; col++) {
                cellList.add(cells[row][col]);
            }
            didMove |= updateCellsAndScore(cellList);
        }
        return didMove;
    }

    public boolean pushRight() {
        numEmptyCells = 0;
        boolean didMove = false;
        for (int row = 0; row < size; row++) {
            List<Cell> cellList = new ArrayList<Cell>();
            for (int col = size - 1; col >= 0; col--) {
                cellList.add(cells[row][col]);
            }
            didMove |= updateCellsAndScore(cellList);
        }
        return didMove;
    }

    // returns true if cells were updated
    private boolean updateCellsAndScore(List<Cell> cellList) {
        List<Integer> initialList = getCellValues(cellList);
        List<Integer> finalList = getPushedList(initialList);
        if (!initialList.equals(finalList)) {
            addValuesForListToCells(finalList, cellList);
            return true;
        }
        return false;
    }

    private List<Integer> getCellValues(List<Cell> cells) {
        List<Integer> values = new ArrayList<Integer>();
        for (Cell cell : cells) {
            values.add(cell.value());
        }
        return values;
    }

    private void addValuesForListToCells(List<Integer> values, List<Cell> cells) {
        for (int i = 0; i < cells.size(); i++) {
            cells.get(i).setValue(values.get(i));
        }
    }

    // this method also updates the score
    private List<Integer> getPushedList(List<Integer> initialList) {
        List<Integer> finalList = new ArrayList<Integer>();
        int prev = -1;
        for (Integer i : initialList) {
            if (i != null && i != 0) {
                if (i == prev) {
                    finalList.add(2 * i);
                    score += 2 * i;
                    prev = -1;
                } else {
                    if (prev != -1) {
                        finalList.add(prev);
                    }
                    prev = i;
                }
            }
        }
        if (prev != -1) {
            finalList.add(prev);
        }

        // make Final list size equal to initial list
        for (int i = finalList.size(); i < initialList.size(); i++) {
            finalList.add(0);
            numEmptyCells++;
        }
        return finalList;
    }

    public int score() {
        return score;
    }

    public int size() {
        return size;
    }

    public void resetBoard() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Cell c = cells[row][col];
                c.makeEmpty();
            }
        }
        numEmptyCells = size * size;
        score = 0;
    }

    public String toString() {
        String[] boardRows = new String[size];
        for (int row = 0; row < size; row++) {
            boardRows[row] = StringUtils.join(cells[row], ',');
        }
        String boardStr = StringUtils.join(boardRows, '\n');
        JSONObject json = new JSONObject();
        try {
            json.put("size", size);
            json.put("score", score);
            json.put("numEmptyCells", numEmptyCells);
            json.put("board", boardStr);
        } catch (JSONException e) {
            Log.w("BaseTwo", e);
            e.printStackTrace();
        }
        return json.toString();
    }
}
