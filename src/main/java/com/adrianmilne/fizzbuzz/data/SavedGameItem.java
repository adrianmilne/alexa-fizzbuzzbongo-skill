package com.adrianmilne.fizzbuzz.data;

import com.adrianmilne.fizzbuzz.domain.GameState;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * FizzBuzz Saved Game Instance Entity. One of these is created per user, and is
 * used to maintain state between user interactions with the game.
 */
@DynamoDBTable(tableName = "FizzBuzzBongoUserData")
public class SavedGameItem {

    private String userId;
    private GameState gameState;

    @DynamoDBHashKey(attributeName = "UserId")
    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    @GameStateFormat
    @DynamoDBAttribute(attributeName = "GameState")
    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(final GameState gameData) {
        this.gameState = gameData;
    }

}
