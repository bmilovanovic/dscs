package com.example.dscs.game;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.example.dscs.Network;
import com.example.dscs.utility.PreferenceUtility;
import com.example.movie.tables.Film;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.lang.Thread.sleep;

/**
 * Presenter in MVP model. Runs the game displayed in {@link GameActivity}.
 */
class GameEngine implements GameEngineInterface {

    private static final int LEVEL_STEP_TIME = 2000;
    private static final int LEVEL_STARTING_TIME = 30000 + LEVEL_STEP_TIME;
    private static final int LEVEL_MIN_TIME = 3000;

    private static final long TIMER_DELAY_BEFORE_LEVEL_STARTING = 500;
    private static final int TIMER_DELAY_BETWEEN_CLOCK_REFRESHING = 100;
    private static final int PAIRS_LIST_SIZE = 10;

    private static final int HIT_BONUS_TIME = 2000;
    private static final int POINTS_HIT = 5;
    private static final int POINTS_MISS = -3;

    private Handler mUiHandler = new Handler(Looper.getMainLooper());
    private List<Pair> mAllPairs = new ArrayList<>();

    private int mScore;
    private int mTimeLeft;
    private int mUnsolvedItems;
    private int mCurrentLevel;
    private boolean mLevelCompleted;
    private boolean mIsStopped;

    private GameViewInterface mView;

    GameEngine(GameViewInterface view) {
        mView = view;
    }

    @Override
    public void stop() {
        mIsStopped = true;
    }

    @Override
    public void restart() {
        mIsStopped = false;
        resetProgress();
        mView.showScore(mScore);
        mView.showTimeLeft(mTimeLeft);
        loadNextLevel();
    }

    /**
     * Resets all the state of the running engine.
     */
    private void resetProgress() {
        mScore = 0;
        mCurrentLevel = 1;
        resetLevel();
    }

    /**
     * Resets the progress in a single level. Done when progressing to next level.
     */
    private void resetLevel() {
        mTimeLeft = LEVEL_STARTING_TIME - (mCurrentLevel * LEVEL_STEP_TIME);
        if (mTimeLeft < LEVEL_MIN_TIME) {
            mTimeLeft = LEVEL_MIN_TIME;
        }
        mUnsolvedItems = PAIRS_LIST_SIZE;
        mLevelCompleted = false;
    }

    /**
     * Loads the next level and starts the timer.
     */
    private void loadNextLevel() {
        resetLevel();
        mView.showLoadingNextLevel(mCurrentLevel);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Pair> pairs = getPairs();

                    if (!mIsStopped) {
                        mUiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mView.displayGame(pairs);
                            }
                        });
                        sleep(TIMER_DELAY_BEFORE_LEVEL_STARTING);
                        startTimer();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Extracts exactly {@link #PAIRS_LIST_SIZE} number of pairs from a {@link #mAllPairs}.
     *
     * @return List of pairs for the next level.
     * @throws ExecutionException   Error connecting to Azure.
     * @throws InterruptedException Somebody interrupted the network operation.
     */
    private List<Pair> getPairs() throws ExecutionException, InterruptedException {
        if (mAllPairs.isEmpty()) {
            mAllPairs = getPairsFromAllMovies();
        }

        List<Pair> pairs = new ArrayList<>();
        for (int i = 0; i < PAIRS_LIST_SIZE; i++) {
            int randomPosition = (int) (Math.random() * mAllPairs.size());
            Pair pair = mAllPairs.get(randomPosition);
            if (!isThereASimilarPair(pairs, pair)) {
                pairs.add(pair);
            } else {
                i--;
            }
        }
        return pairs;
    }

    /**
     * Gets the pairs from a network and stores them into a local list.
     *
     * @return List of the pairs stored in a distant data base.
     * @throws ExecutionException   Error connecting to Azure.
     * @throws InterruptedException Somebody interrupted the network operation.
     */
    private List<Pair> getPairsFromAllMovies() throws ExecutionException, InterruptedException {
        MobileServiceList<Film> films = Network.getTable((Context) mView, Film.class).where()
                .orderBy("filmId", QueryOrder.Ascending).execute().get();
        List<Pair> allPairs = new ArrayList<>();
        for (Film film : films) {
            String title = film.getTitle();
            if (!TextUtils.isEmpty(title) && title.contains(" ")) {
                allPairs.add(new Pair(title));
            }
        }

        return allPairs;
    }

    /**
     * Detects if there is similar pair in the list. Pairs are similar if they have either the same
     * first part equal or the second.
     *
     * @param pairs   List of the pairs to search.
     * @param newPair A pair to compare with all the pairs from the list.
     * @return Whether there is similar pair.
     */
    private boolean isThereASimilarPair(List<Pair> pairs, Pair newPair) {
        for (Pair pair : pairs) {
            if (TextUtils.equals(pair.first, newPair.first) ||
                    TextUtils.equals(pair.second, newPair.second)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Starts the level by timer ticking.
     *
     * @throws InterruptedException Thread sleeping ran into a problem.
     */
    private void startTimer() throws InterruptedException {
        while (mTimeLeft > 0 && !mLevelCompleted && !mIsStopped) {
            Thread.sleep(TIMER_DELAY_BETWEEN_CLOCK_REFRESHING - 5);
            mTimeLeft -= TIMER_DELAY_BETWEEN_CLOCK_REFRESHING;
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    mView.showTimeLeft(mTimeLeft);
                }
            });
        }
        if (!mIsStopped) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (!mLevelCompleted) {
                        // User didn't connect all the tiles. This game is over.
                        int highestScore = PreferenceUtility.getHighestScore((Context) mView);
                        mView.gameOver(mScore, highestScore);
                        if (mScore > highestScore) {
                            PreferenceUtility.setHighestScore((Context) mView, mScore);
                        }
                    } else {
                        // User completed the level. Load the next one.
                        mCurrentLevel++;
                        int bonusPoints = mTimeLeft / 1000;
                        if (bonusPoints > 0) {
                            mScore += bonusPoints;
                            mView.showScoreChange(bonusPoints);
                            mView.showScore(mScore);
                        }
                        loadNextLevel();
                    }
                }
            });
        }
    }

    @Override
    public void miss() {
        mScore += POINTS_MISS;

        mView.showScoreChange(POINTS_MISS);
        mView.showScore(mScore);
    }

    @Override
    public void hit() {
        mScore += POINTS_HIT;
        mTimeLeft += HIT_BONUS_TIME;
        mUnsolvedItems--;

        if (mUnsolvedItems > 0) {
            mView.showScoreChange(POINTS_HIT);
            mView.showScore(mScore);
        } else {
            mLevelCompleted = true;
        }
    }
}
