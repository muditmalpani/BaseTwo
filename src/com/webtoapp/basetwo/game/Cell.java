package com.webtoapp.basetwo.game;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TableRow;
import android.widget.TextView;

public class Cell {
    private static String[] cellColors = {
        "#EDE0C8",//2
        "#EDE0B8",//4
        "#EDE0A8",//8
        "#EDD0A8",//16
        "#EDD088",//32
        "#EDD068",//64
        "#EDB068",//128
        "#ED9068",//256
        "#ED7058",//512
        "#ED6038",//1024
        "#F24018",//2048
        "#F52008",//4096
        "#FF0000",//8192
        "#FF0000",
        "#FF0000",
        "#FF0000",
        "#FF0000",
    };
    private static String cellColor = "#D8C8B0";
    private static String cellTextColor = "#776E65";

    private int value;
    final public TextView view;

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
        Resources r = context.getResources();
        int cellWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics());
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics());
        TableRow.LayoutParams params = new TableRow.LayoutParams(cellWidth, cellWidth);
        params.setMargins(margin, margin, margin, margin);
        v.setLayoutParams(params);
        v.setTextSize(textSize);
        v.setTextColor(Color.parseColor(cellTextColor));
        v.setBackgroundColor(Color.parseColor(cellColor));
        v.setGravity(Gravity.CENTER);
        v.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        return v;
    }

    private void displayText() {
        if (value > 0) {
            view.setText(String.valueOf(value));
            int colorIndex = logBase2(value) - 1;
            view.setBackgroundColor(Color.parseColor(cellColors[colorIndex]));
            if (value < 10) {
                view.setTextColor(Color.parseColor("#776666"));
            } else {
                view.setTextColor(Color.parseColor("#EEEEEE"));
            }
            if (value > 1000) {
                view.setTextSize(25);
            } else if (value > 100) {
                view.setTextSize(28);
            } else {
                view.setTextSize(30);
            }
        } else {
            view.setText("");
            view.setBackgroundColor(Color.parseColor(cellColor));
        }
    }

    public void makeEmpty() {
        value = 0;
        view.setText("");
        view.setBackgroundColor(Color.parseColor(cellColor));
    }

    private static int logBase2(int a) {
        return (int) (Math.log(a) / Math.log(2));
    }

    public String toString() {
        return String.valueOf(value);
    }
}
