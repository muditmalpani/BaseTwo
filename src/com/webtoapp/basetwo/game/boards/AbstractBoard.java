package com.webtoapp.basetwo.game.boards;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import com.webtoapp.basetwo.game.Cell;
import com.webtoapp.basetwo.game.SwipeDirection;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class AbstractBoard implements Board {
    protected class Row {
        public final int size;
        public final Cell[] cells;
        public final TableRow rowView;
        private final Context context;

        protected Row(int size, Context context) {
            this.size = size;
            this.cells = new Cell[size];
            this.context = context;
            this.rowView = new TableRow(context);
            this.rowView.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            this.rowView.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        protected void addCells() {
            for (int i = 0; i < size; i++) {
                Cell c = new Cell(context);
                cells[i] = c;
                rowView.addView(c.view);
            }
        }
    }

    protected Row[] rows;
    protected int size; // number of rows
    protected Context context;
    protected TableLayout boardView;
    protected int numEmptyCells;
    protected int score;
    protected int highestTile;

    protected abstract void addRows();

    protected abstract int getRandomValue();

    protected abstract int getBoardSize();

    protected abstract boolean pushUpLeft();

    protected abstract boolean pushUpRight();

    protected abstract boolean pushDownLeft();

    protected abstract boolean pushDownRight();

    private Cell getRandomEmptyCell() {
        if (numEmptyCells == 0) {
            return null;
        }
        List<Cell> emptyCells = getEmptyCells();
        if (emptyCells.isEmpty()) {
            Log.e("BaseTwo", "No empty cell found although numEmptyCells = " + numEmptyCells);
        }
        int i = (int) (Math.random() * emptyCells.size());
        return emptyCells.get(i);
    }

    private List<Cell> getEmptyCells() {
        List<Cell> emptyCells = new ArrayList<Cell>();
        for (Row row : rows) {
            for (Cell cell : row.cells) {
                if (cell.value() <= 0) {
                    emptyCells.add(cell);
                }
            }
        }
        return emptyCells;
    }

    private boolean pushLeft() {
        numEmptyCells = 0;
        boolean didMove = false;
        for (Row row : rows) {
            List<Cell> cellList = new ArrayList<Cell>();
            for (int col = 0; col < row.size; col++) {
                cellList.add(row.cells[col]);
            }
            didMove |= updateCellsAndScore(cellList);
        }
        return didMove;
    }

    private boolean pushRight() {
        numEmptyCells = 0;
        boolean didMove = false;
        for (Row row : rows) {
            List<Cell> cellList = new ArrayList<Cell>();
            for (int col = row.size - 1; col >= 0; col--) {
                cellList.add(row.cells[col]);
            }
            didMove |= updateCellsAndScore(cellList);
        }
        return didMove;
    }

    private boolean pushUp() {
        numEmptyCells = 0;
        boolean didMove = false;
        for (int col = 0; col < size; col++) {
            List<Cell> cellList = new ArrayList<Cell>();
            for (int row = 0; row < size; row++) {
                cellList.add(rows[row].cells[col]);
            }
            didMove |= updateCellsAndScore(cellList);
        }
        return didMove;
    }

    private boolean pushDown() {
        numEmptyCells = 0;
        boolean didMove = false;
        for (int col = 0; col < size; col++) {
            List<Cell> cellList = new ArrayList<Cell>();
            for (int row = size - 1; row >= 0; row--) {
                cellList.add(rows[row].cells[col]);
            }
            didMove |= updateCellsAndScore(cellList);
        }
        return didMove;
    }

    private static List<Integer> getCellValues(List<Cell> cells) {
        List<Integer> values = new ArrayList<Integer>();
        for (Cell cell : cells) {
            values.add(cell.value());
        }
        return values;
    }

    private static void addValuesToCells(List<Integer> values, List<Cell> cells) {
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
                    int newVal = 2 * i;
                    if (newVal > highestTile) {
                        highestTile = newVal;
                    }
                    finalList.add(newVal);
                    score += newVal;
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

    /**
     * Pushes the filled cells to the front and combines cells with same value
     * 
     * @param cellList - List of cells to be pushed
     * @return Returns true if cells were updated
     */
    protected boolean updateCellsAndScore(List<Cell> cellList) {
        List<Integer> initialList = getCellValues(cellList);
        List<Integer> finalList = getPushedList(initialList);
        if (!initialList.equals(finalList)) {
            addValuesToCells(finalList, cellList);
            return true;
        }
        return false;
    }

    protected boolean isHorizontalMovePossible() {
        for (Row row : rows) {
            int prev = -1;
            for (Cell cell : row.cells) {
                int v = cell.value();
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

    protected boolean isVerticalMovePossible() {
        for (int col = 0; col < size; col++) {
            int prev = -1;
            for (int row = 0; row < size; row++) {
                int v = rows[row].cells[col].value();
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

    protected boolean isDiagonalMovePossible() {
        //check for similar cells in front diagonal
        for (int i = 0; i < size; i++) {
            int prev = -1;
            for (int row = i; row < size; row++) {
                int v = rows[row].cells[i].value();
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
            for (int row = i; row < size; row++) {
                int v = rows[row].cells[row - i].value();
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
    public abstract boolean isFull();

    @Override
    public void addValueToRandomPosition() {
        Cell cell = getRandomEmptyCell();
        if (cell != null) {
            cell.setValue(getRandomValue());
        }
        numEmptyCells--;
    }

    /**
     * Updates the board based on the direction of the swipe
     *
     * @param direction - Direction of the swipe
     * @return Returns true if the boards gets update and false otherwise
     */
    @Override
    public boolean update(SwipeDirection direction) {
        if (direction == null) {
            return false;
        }
        switch (direction) {
            case UP:
                return pushUp();
            case DOWN:
                return pushDown();
            case LEFT:
                return pushLeft();
            case RIGHT:
                return pushRight();
            case UP_LEFT:
                return pushUpLeft();
            case UP_RIGHT:
                return pushUpRight();
            case DOWN_LEFT:
                return pushDownLeft();
            case DOWN_RIGHT:
                return pushDownRight();
            default:
                return false;
        }
    }

    @Override
    public void resetBoard() {
        for (Row row : rows) {
            for (Cell cell : row.cells) {
                cell.makeEmpty();
            }
        }
        numEmptyCells = getBoardSize();
        score = 0;
    }

    @Override
    public int score() {
        return score;
    }

    @Override
    public int highestTile() {
        return highestTile;
    }

    @Override
    public void parseBoardStr(String board) {
        String[] boardRows = board.split("\n");
        int i = 0;
        for (String row : boardRows) {
            TableRow tr = new TableRow(context);
            String[] cellsInRow = row.split(",");
            rows[i] = new Row(cellsInRow.length, context);
            tr.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            tr.setGravity(Gravity.CENTER_HORIZONTAL);
            int j = 0;
            for (String cell : cellsInRow) {
                Cell c = new Cell(context);
                c.setValue(Integer.valueOf(cell));
                rows[i].cells[j] = c;
                tr.addView(c.view);
                j++;
            }
            boardView.addView(tr);
            i++;
        }
    }

    @Override
    public String toString() {
        String[] boardRows = new String[size];
        for (int row = 0; row < size; row++) {
            boardRows[row] = StringUtils.join(rows[row].cells, ',');
        }
        String boardStr = StringUtils.join(boardRows, '\n');
        JSONObject json = new JSONObject();
        try {
            json.put("score", score);
            json.put("highestTile", highestTile);
            json.put("numEmptyCells", numEmptyCells);
            json.put("board", boardStr);
        } catch (JSONException e) {
            Log.w("BaseTwo", e);
            e.printStackTrace();
        }
        return json.toString();
    }

}
