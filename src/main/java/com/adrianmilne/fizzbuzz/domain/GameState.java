package com.adrianmilne.fizzbuzz.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adrianmilne.fizzbuzz.util.FizzBuzzHelper;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This is the game specific state. For now there is only the current number,
 * but it can be extended to add more data if we decide to enhance the
 * functionality of the game.
 *
 */
@SuppressWarnings("boxing")
public class GameState {

    private static final Logger LOG = LoggerFactory.getLogger(GameState.class);

    private Status status = Status.PENDING;
    private int currentNumber = 0;
    private int currentScore = 0;

    public int getCurrentNumber() {
        return currentNumber;
    }

    public void setCurrentNumber(final int currentNumber) {
        this.currentNumber = currentNumber;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(final int currentScore) {
        this.currentScore = currentScore;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    @JsonIgnore
    public String getCurrentNumberAsString() {
        return FizzBuzzHelper.convertNumberToString(currentNumber);
    }

    @JsonIgnore
    public String getPreviousNumberAsString() {
        return FizzBuzzHelper.convertNumberToString(currentNumber - 1);
    }
    
    @JsonIgnore
    public String getNextNumberAsString() {
        return FizzBuzzHelper.convertNumberToString(currentNumber + 1);
    }

    public int incrementNumber(final int increment) {
        final int newNumber = this.currentNumber + increment;
        LOG.info("Incrementing number from: [{}] to [{}]", this.currentNumber, newNumber);
        setCurrentNumber(newNumber);
        return newNumber;
    }

    public void incrementScore() {
        final int newScore = this.currentScore + 1;
        LOG.info("Incrementing score from: [{}] to [{}]", this.currentScore, newScore);
        setCurrentScore(newScore);
    }
}
