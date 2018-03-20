package com.adrianmilne.fizzbuzz.domain;

import com.amazon.speech.speechlet.Session;

/**
 * A saved game POJO. Encapsulates game state for a session between requests.
 *
 */
public class SavedGame {

    private Session session;
    private GameState gameState;

    public SavedGame(final Session session, final GameState gameState) {
        super();
        this.session = session;
        this.gameState = gameState;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(final Session session) {
        this.session = session;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(final GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public String toString() {
        return "SavedGame [session=" + session + ", gameState=" + gameState + "]";
    }

}
