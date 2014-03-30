package com.webtoapp.basetwo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.Toast;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.google.gson.Gson;
import com.mopub.mobileads.MoPubView;
import com.webtoapp.basetwo.game.GameLevel;
import com.webtoapp.basetwo.game.GameStats;
import com.webtoapp.basetwo.game.SwipeDirection;
import com.webtoapp.basetwo.game.UserLevelStats;
import com.webtoapp.basetwo.game.boards.Board;
import com.webtoapp.basetwo.game.boards.Boards;
import com.webtoapp.basetwo.utils.BaseUtils;
import com.webtoapp.basetwo.utils.GameUtils;

public class GameActivity extends Activity implements OnGestureListener {
    public static final int ACTIVITY_CODE = 1;
    public static final String PREFS_NAME = "AppPrefsFile";

    private Board board;
    private GameLevel gameLevel;
    private boolean gameOver;
    private SharedPreferences settings;
    private GestureDetectorCompat mDetector;
    private GameStats gameStats;

    private MoPubView moPubView;

    private Tracker tracker;// Google Analytics

    private UiLifecycleHelper uiHelper;// Facebook share

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

        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);

        settings = getSharedPreferences(GameActivity.PREFS_NAME, 0);

        String userStatsStr = settings.getString("stats", null);
        if (userStatsStr == null) {
            gameStats = new GameStats();
        } else {
            Gson gson = new Gson();
            gameStats = gson.fromJson(userStatsStr, GameStats.class);
        }

        gameLevel = GameLevel.fromId(gameStats.gameLevel);

        TableLayout boardView = (TableLayout) findViewById(R.id.playBoard);
        board = Boards.init(settings, this.getApplicationContext(), boardView, gameLevel);
        displayScore();
        startGame();
    }

    public void displayScore() {
        TextView scoreView = (TextView) findViewById(R.id.scoreView);
        scoreView.setText(String.valueOf(board.score()));
    }

    public void startGame() {
        gameOver = false;
        tracker.send(MapBuilder
                .createEvent("game_event",
                        "start",
                        BaseUtils.getShaAndroidId(getApplicationContext()),
                        null)
                .build());
    }

    private void updateStats() {
        gameStats.updateUserStatsForGame(gameLevel, board.score(), board.highestTile());

        // update points and userLevel

        // store stats & delete the stored board
        settings.edit()
                .putString("stats", gameStats.toString())
                .remove("board")
                .commit();
    }

    private void showGameOverScreen() {
        String message = GameUtils.getGameOverMessage(board.score(), gameLevel,
                gameStats.getUserStatsForLevel(gameLevel));
        UserLevelStats stats = gameStats.getUserStatsForLevel(gameLevel);
        int avgScore = stats.totalMatches == 0 ? 0 : stats.totalScore / stats.totalMatches;

        // add result view
        RelativeLayout resultView = (RelativeLayout) findViewById(R.id.game_over_layout);
        resultView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        LayoutInflater.from(getApplicationContext()).inflate(R.layout.game_over, resultView, true);

        TextView scoreView = (TextView) findViewById(R.id.score_result_view);
        scoreView.setText(String.valueOf(board.score()));
        TextView highTileView = (TextView) findViewById(R.id.highest_tile_view);
        highTileView.setText("Highest Tile: " + board.highestTile());
        TextView highScoreView = (TextView) findViewById(R.id.high_score_view);
        highScoreView.setText("High Score: " + String.valueOf(stats.highestScore));
        TextView avgScoreView = (TextView) findViewById(R.id.avg_score_view);
        avgScoreView.setText("Average Score: " + String.valueOf(avgScore));
        TextView messageView = (TextView) findViewById(R.id.result_message);
        messageView.setText(message);

        ImageButton shareBtn = (ImageButton) findViewById(R.id.share_btn);
        final Activity activity = this;
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FacebookDialog.canPresentShareDialog(getApplicationContext(),
                        FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
                    FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(activity)
                            .setName("I just scored " + board.score() + " points on Base Two.")
                            .setDescription(
                                    "Base Two is an amazing app. I love playing this game.\n"
                                            + "Swipe to move the tiles in a particular direction, adjacent tiles with same value in "
                                            + "the direction of swipe combine to form a single tile with double the value.")
                            .setLink("https://play.google.com/store/apps/details?id=com.webtoapp.basetwo")
                            .build();
                    uiHelper.trackPendingDialogCall(shareDialog.present());
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Facebook sharing is not supported for your device.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

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

    public void finishGame() {
        gameOver = true;

        if (board.score() > 0) {
            updateStats();
        }

        showGameOverScreen();

        // send stats to google analytics
        tracker.send(MapBuilder
                .createEvent("game_event",
                        "score_" + gameLevel.levelId,
                        BaseUtils.getShaAndroidId(getApplicationContext()),
                        (long) board.score())
                .build());

        tracker.send(MapBuilder
                .createEvent("high_tile_event_" + gameLevel.levelId,
                        String.valueOf(board.highestTile()),
                        BaseUtils.getShaAndroidId(getApplicationContext()),
                        (long) board.score())
                .build());
    }

    public void restartGame() {
        RelativeLayout resultView = (RelativeLayout) findViewById(R.id.game_over_layout);
        // remove the result view
        resultView.removeAllViews();
        resultView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        // reset the board
        board.resetBoard();
        board.addValueToRandomPosition();
        board.addValueToRandomPosition();
        startGame();
        displayScore();
    }

    @Override
    public void onBackPressed() {
        if (!gameOver) {
            new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Save Game?")
                    .setMessage("Click save if you want to continue this game next time.")
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("BaseTwo", "Board: " + board.toString());
                            settings.edit()
                                    .putString("board", board.toString())
                                    .commit();
                            finish();
                        }
                    })
                    .setNegativeButton("End Game", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishGame();
                        }
                    })
                    .show();
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
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
        SwipeDirection direction = GameUtils.getDirection(e1, e2, velocityX, velocityY, gameLevel.directionType);
        if (direction != null) {
            Log.i("BaseTwo", direction.name());
        }
        boolean didMove = board.update(direction);
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

    // Facebook share
    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(ACTIVITY_CODE);
        moPubView.destroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

}
