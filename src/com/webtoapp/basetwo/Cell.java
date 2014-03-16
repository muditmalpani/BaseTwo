package com.webtoapp.basetwo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TableRow;
import android.widget.TextView;

public class Cell {
    private static String[] cellColors = {
        "#CCC0B4",//0
        "#CCC0B4",//1
        "#CCC0B4",//2
        "#CCC0B4",//3
        "#CCC0B4",//4
        "#CCC0B4",//5
        "#CCC0B4",//6
        "#CCC0B4",//7
        "#CCC0B4",//8
        "#CCC0B4",//9
        "#CCC0B4",//10
        "#CCC0B4",//11
        "#CCC0B4",//12
        "#CCC0B4",//13
        "#CCC0B4",//14
        "#CCC0B4",//15
    };
    private static String cellColor = "#CCC0B4";
    private static String cellTextColor = "#776E65";

    private int value;
    public TextView view;

    public Cell(Context context) {
        value = 0;
        view = getCellView(context);
        displayText();
    }

    public void setValue(int val) {
        value = val;
        displayText();
    }

    public int value() {
        return value;
    }

    private TextView getCellView(Context context) {
        TextView v = new TextView(context);
        TableRow.LayoutParams params = new TableRow.LayoutParams(130, 130);
        params.setMargins(10, 10, 10, 10);
        v.setLayoutParams(params);
        v.setTextSize(30);
        v.setTextColor(Color.parseColor(cellTextColor));
        v.setBackgroundColor(Color.parseColor(cellColor));
        v.setGravity(Gravity.CENTER);
        v.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        return v;
    }

    private void displayText() {
        if (value > 0) {
            view.setText(String.valueOf(value));
            int colorIndex = logBase2(value);
            view.setBackgroundColor(Color.parseColor(cellColors[colorIndex]));
        } else {
            view.setText("");
            view.setBackgroundColor(Color.parseColor(cellColor));
        }
    }

    private static int logBase2(int a) {
        return (int) (Math.log(a) / Math.log(2));
    }
}
