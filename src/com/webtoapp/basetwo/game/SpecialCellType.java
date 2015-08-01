package com.webtoapp.basetwo.game;

import android.util.SparseArray;

public enum SpecialCellType {
    DOUBLE(-1, "2X"),
    HALF(-2, "1/2"),
    ;

    public int val;
    public String label;

    private SpecialCellType(int val, String label) {
        this.val = val;
        this.label = label;
    }

    static SparseArray<SpecialCellType> valToType = new SparseArray<SpecialCellType>();

    static {
        for (SpecialCellType cellType : SpecialCellType.values()) {
            valToType.put(cellType.val, cellType);
        }
    }

    public static SpecialCellType fromVal(int val) {
        return valToType.get(val);
    }
}
