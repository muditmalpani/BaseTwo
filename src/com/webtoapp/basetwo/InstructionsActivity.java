package com.webtoapp.basetwo;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import com.mopub.mobileads.MoPubView;

public class InstructionsActivity extends Activity {
    private MoPubView moPubView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        String gameplayPara = "Swipe horizontally/vertically/diagonally to move the tiles in the corresponding direction. "
                + "Two adjacent tiles with same number in direction of the swipe combine to form a single tile "
                + "with value double of the original value.";
        String objectivePara = "This is a puzzle which contains a board consisting of tile which hold a number "
                + "which is multiple of two. The objective of the game is to combine these tiles to get the maximum "
                + "value in a single tile.";
        String scoringPara = "Your score is computed based on the value of the tiles you combine. "
                + "For every combined pair of tiles, your score is incremented by the value of the new tile created. "
                + "You are rated based on your average score across the games played "
                + "and your hightest score at each level";
        String specialCellPara = "Apart from the number cells which always have a numerical value, there are some special cells."
                + "These cells have no value of their own, but combine with another cell to change its value."
                + " The special cells always have <b>green</b> color There are two special cells:<br/>"
                + "<p>&#8226; <b>Double(2x):</b> This cell doubles the value of the cell with which it combines. The cell will only combile in the"
                + "direction of the swipe and other cell can't combine with 'Double'. For example if have a '2' followed by a 'Double' in first row, the left swipe will make 'Double' combile with '2'"
                + "to make 4, but if you swipe right '2' cannot combine with 'Double'. This allows you to move 'Double' around and combine with appropriate cell.</p>"
                + "<p>&#8226; <b>Half(1/2):</b> This cell reduces the value of a cell to half. Similar to 'Double', 'Half' only combines in the direction"
                + "of the swipe and other cells can't combine with 'Half'. It also doens't combine with '2'.</p>";
        String levelPara = "There are 5 levels in this game:<br/>"
                + "<p>&#8226; <b>Beginner:</b> This level has 2x2 square board with swipes allowed in horizontal and vertical directions. This is very simple puzzle where the maximum possible tile is 16. This level is for newbies to understand the game.<br></p>"
                + "<p>&#8226; <b>Intermediate:</b> This level has 4x4 square board with swipes allowed in horizontal and vertical direction. Combine the tiles to get tiles with larger values and try to keep the high-valued tiles in a corner to make the game last longer.<br></p>"
                + "<p>&#8226; <b>Trimediate:</b> This level has a triangular board with swipes allowed in horizontal and diagonal direction. Combine the tiles and try to get the tiles with higher values to the top of the triangle. This level helps you to develop the understanding of positioning of high-valued tiles.<br></p>"
                + "<p>&#8226; <b>Hexamediate:</b> This level has a hexagonal board with swipes allowed in horizontal and diagonal direction. This is a high scoring level where your score can easily reach more than 100k. Its easier to combine tiles as the tiles connected to one tile is more than intermediate level. <br></p>"
                + "<p>&#8226; <b>Expert:</b> Coming soon!</p>";

        ((TextView) findViewById(R.id.inst_gameplay)).setText(gameplayPara);
        ((TextView) findViewById(R.id.inst_objective)).setText(objectivePara);
        ((TextView) findViewById(R.id.inst_scoring)).setText(scoringPara);
        ((TextView) findViewById(R.id.inst_special)).setText(Html.fromHtml(specialCellPara));
        ((TextView) findViewById(R.id.inst_levels)).setText(Html.fromHtml(levelPara));

        moPubView = (MoPubView) findViewById(R.id.inst_adview);
        moPubView.setAdUnitId("a0459dba947a466a833db1ec27e04dc1");
        moPubView.loadAd();
    }

    @Override
    protected void onDestroy() {
        moPubView.destroy();
        super.onDestroy();
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
