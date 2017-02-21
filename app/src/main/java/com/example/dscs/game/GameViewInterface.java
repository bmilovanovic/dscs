package com.example.dscs.game;

import java.util.List;

/**
 * View interface between {@link GameEngine} and {@link GameActivity} in classic MVP model.
 */
interface GameViewInterface {

    /**
     * Informs the user that level loading is in the progress.
     *
     * @param mCurrentLevel Order of the level that is to be run in a few moments.
     */
    void showLoadingNextLevel(int mCurrentLevel);

    /**
     * Displays the tiles ready for connecting.
     *
     * @param pairs Pair of the literals that user is to try to connect.
     */
    void displayGame(List<Pair> pairs);

    /**
     * Displays the total score.
     *
     * @param score Number of points.
     */
    void showScore(int score);

    /**
     * Displays how much points user scored with the last move.
     *
     * @param points Points, either positive or negative.
     */
    void showScoreChange(int points);

    /**
     * Shows time left until the end of the round.
     *
     * @param timeLeft Time to display on a clock.
     */
    void showTimeLeft(int timeLeft);

    /**
     * The user didn't succeed to connect all the pairs in a level. Proposes to user to try again.
     *
     * @param score        Total score in all levels.
     * @param highestScore Highest score after last reset.
     */
    void gameOver(int score, int highestScore);

}
