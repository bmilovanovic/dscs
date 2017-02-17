package com.example.dscs.game;

/**
 * Presenter interface between {@link GameActivity} and {@link GameEngine} in classic MVP model.
 */
interface GameEngineInterface {

    /**
     * Resets the state and runs the game again from the beginning.
     */
    void restart();

    /**
     * Stops the game and kills the engine state.
     */
    void stop();

    /**
     * Informs the engine that user connected the pair.
     */
    void hit();

    /**
     * Informs the engine that user made wrong connection.
     */
    void miss();

}
