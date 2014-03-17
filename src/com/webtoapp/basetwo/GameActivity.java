package com.webtoapp.basetwo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TextView;

public class GameActivity extends Activity implements OnGestureListener {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    public static final String PREFS_NAME = "AppPrefsFile";
    private Board board;
    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(this, this);

        int size = 4;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            size = extras.getInt("level") + 1;
        }

        TableLayout boardView = (TableLayout) findViewById(R.id.playBoard);
        board = new Board(size, this.getApplicationContext(), boardView);
        board.addCells();
        displayScore();
        startGame();
    }

    public void displayScore() {
        TextView scoreView = (TextView) findViewById(R.id.scoreView);
        scoreView.setText(String.valueOf(board.score()));
    }

    public void startGame() {
        board.addValueToRandomPosition();
        board.addValueToRandomPosition();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (board.isFull()) {
            return false;
        }
        boolean didMove = false;
        if (Math.abs(velocityX) > Math.abs(velocityY)) {
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                didMove = board.pushLeft();
            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                didMove = board.pushRight();
            }
        } else {
            // bottom to top swipe
            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                didMove = board.pushUp();
            }
            // left to right swipe
            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                didMove = board.pushDown();
            }
        }
        if (didMove) {
            board.addValueToRandomPosition();
            displayScore();
            if (board.isFull()) {
                finishGame();
            }
            return true;
        }
        return false;
    }

    private String getGameOverMessage() {
        String message = "GAME OVER!";
        int lowerLimit = 21;
        int upperLimit = 50;
        if (board.size() == 3) {
            lowerLimit = 900;
            upperLimit = 1900;
        } else if (board.size() == 4) {
            lowerLimit = 18000;
            upperLimit = 25000;
        }
        if (board.score() > upperLimit) {
            message = "EXCELLENT!";
        } else if (board.score() > lowerLimit) {
            message = "WELL DONE!";
        }
        return message;
    }

    public void finishGame() {
        String message = getGameOverMessage();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int matches = settings.getInt("matches" + board.size(), 0);
        int highScore = settings.getInt("highScore" + board.size(), 0);
        int totalScore = settings.getInt("totalScore" + board.size(), 0);

        matches += 1;
        totalScore += board.score();
        if (board.score() > highScore) {
            highScore = board.score();
            message = "HIGH SCORE!";
        }
        int avgScore = matches == 0 ? 0 : totalScore / matches;
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("matches" + board.size(), matches);
        editor.putInt("highScore" + board.size(), highScore);
        editor.putInt("totalScore" + board.size(), totalScore);
        editor.commit();

        RelativeLayout resultView = (RelativeLayout) findViewById(R.id.game_over_layout);
        resultView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        LayoutInflater.from(getApplicationContext()).inflate(R.layout.game_over, resultView, true);

        TextView scoreView = (TextView) findViewById(R.id.score_result_view);
        scoreView.setText(String.valueOf(board.score()));
        TextView highScoreView = (TextView) findViewById(R.id.high_score_view);
        highScoreView.setText("High Score: " + String.valueOf(highScore));
        TextView avgScoreView = (TextView) findViewById(R.id.avg_score_view);
        avgScoreView.setText("Average Score: " + String.valueOf(avgScore));
        TextView messageView = (TextView) findViewById(R.id.result_message);
        messageView.setText(message);

        Button restartBtn = (Button) findViewById(R.id.restart_btn);
        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });

        Button backBtn = (Button) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void restartGame() {
        RelativeLayout resultView = (RelativeLayout) findViewById(R.id.game_over_layout);
        // remove the result view
        resultView.removeAllViews();
        resultView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        // reset the board
        board.resetBoard();
        startGame();
        displayScore();
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

}
