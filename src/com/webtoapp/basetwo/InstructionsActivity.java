package com.webtoapp.basetwo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;

public class InstructionsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        String gameplayPara = "Swipe up/down/left/right to move the tiles in the corresponding direction. "
                + "Two adjacent tiles with same number in direction of the swipe combine to form a single tile "
                + "with value double of the original value.";
        String objectivePara = "This is a puzzle which contains a square board with where each tile can hold a number "
                + "which is multiple of two. The objective of the game is two combine these tiles to get the maximum "
                + "value in a single tile.";
        String scoringPara = "Your score is computed based on the total tiles you combine. "
                + "For every combined tiles, your score in incremented by the number on the new tile created "
                + "by combining the tiles. You are rated based on your average score across the games played "
                + "and your hightest score at each level";

        ((TextView) findViewById(R.id.inst_gameplay)).setText(gameplayPara);
        ((TextView) findViewById(R.id.inst_objective)).setText(objectivePara);
        ((TextView) findViewById(R.id.inst_scoring)).setText(scoringPara);
    }

    // Google Analytics
    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

}
