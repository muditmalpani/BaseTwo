package com.webtoapp.basetwo.game.boards;

import com.webtoapp.basetwo.game.SwipeDirection;


public interface Board {
    public void addValueToRandomPosition();

    /**
     * Returns true if no further move is possible
     */
    public boolean isFull();

    /**
     * Updates the board based on the direction of the swipe
     *
     * @param direction - Direction of the swipe
     * @return Returns true if the boards gets update and false otherwise
     */
    public boolean update(SwipeDirection direction);

    public void resetBoard();

    public int score();

    public int highestTile();

    public void parseBoardStr(String board);

    public String toString();
}
