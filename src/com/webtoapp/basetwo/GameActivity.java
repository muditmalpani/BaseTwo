package com.webtoapp.basetwo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.mopub.mobileads.MoPubView;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GameActivity extends Activity implements OnGestureListener {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    public static final int ACTIVITY_CODE = 1;
    public static final String PREFS_NAME = "AppPrefsFile";
    private Board board;
    private Tracker tracker;
    private GestureDetectorCompat mDetector;
    private MoPubView moPubView;
    private SharedPreferences settings;
    private boolean isOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Instantiate the gesture detector with the application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(this, this);
        tracker = GoogleAnalytics.getInstance(this).getTracker("UA-49097018-1");

        moPubView = (MoPubView) findViewById(R.id.game_adview);
        moPubView.setAdUnitId("edef7449ac8040048a2be832117c3609");
        moPubView.loadAd();

        settings = getSharedPreferences(GameActivity.PREFS_NAME, 0);

        TableLayout boardView = (TableLayout) findViewById(R.id.playBoard);
        board = Board.init(settings, this.getApplicationContext(), boardView);
        displayScore();
        startGame();
    }

    public void displayScore() {
        TextView scoreView = (TextView) findViewById(R.id.scoreView);
        scoreView.setText(String.valueOf(board.score()));
    }

    public static String getShaAndroidId(Context context) {
        try {
            return sha1(Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
        } catch (Exception e) {
            return "N/A";
        }
    }

    public static String sha1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ?
                        (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public void startGame() {
        isOver = false;
        tracker.send(MapBuilder
                .createEvent("game_event",
                        "start",
                        getShaAndroidId(getApplicationContext()),
                        null)
                .build());
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
        isOver = true;
        String message = getGameOverMessage();
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

        // send stats to google analytics
        tracker.send(MapBuilder
                .createEvent("game_event",
                        "score",
                        getShaAndroidId(getApplicationContext()),
                        (long) board.score())
                .build());

        // store stats
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("matches" + board.size(), matches);
        editor.putInt("highScore" + board.size(), highScore);
        editor.putInt("totalScore" + board.size(), totalScore);
        editor.commit();

        // add result view
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

        ImageButton restartBtn = (ImageButton) findViewById(R.id.restart_btn);
        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });

        ImageButton backBtn = (ImageButton) findViewById(R.id.back_btn);
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
    public void onBackPressed() {
        if (!board.isFull()) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Save Game?")
                    .setMessage("Click save if you want to continue this game next time.")
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("BaseTwo", "Board: " + board.toString());
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("board", board.toString());
                            editor.commit();
                            finish();
                        }
                    })
                    .setNegativeButton("End Game", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // delete board from setting
                            SharedPreferences.Editor editor = settings.edit();
                            editor.remove("board").commit();
                            //Stop the activity
                            finishGame();
                        }
                    })
                    .show();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        setResult(ACTIVITY_CODE);
        moPubView.destroy();
        super.onDestroy();
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

    // Google Analytics
    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        setResult(ACTIVITY_CODE);
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

}
