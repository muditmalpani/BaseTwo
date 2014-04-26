package com.webtoapp.basetwo.game.boards;


public abstract class NormalBoard extends AbstractBoard implements Board {

    @Override
    protected void addRows() {
        for (int row = 0; row < size; row++) {
            Row r = new Row(size, context);
            r.addCells();
            boardView.addView(r.rowView);
            rows[row] = r;
        }
    }

    /**
     * Returns true if no further move is possible
     */
    public boolean isFull() {
        if (numEmptyCells != 0) {
            return false;
        }
        // if no empty cell, check if no further move can be made
        boolean canMove = isHorizontalMovePossible() || isVerticalMovePossible();

        return !canMove;
    }

    @Override
    protected int getBoardSize() {
        return size * size;
    }

    protected boolean pushUpLeft() {
        return false;
    }

    protected boolean pushUpRight() {
        return false;
    }

    protected boolean pushDownLeft() {
        return false;
    }

    protected boolean pushDownRight() {
        return false;
    }
}
