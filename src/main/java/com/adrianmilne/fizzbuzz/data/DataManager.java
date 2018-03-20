package com.adrianmilne.fizzbuzz.data;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adrianmilne.fizzbuzz.domain.SavedGame;
import com.amazon.speech.speechlet.Session;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.handlers.TracingHandler;

/**
 * Data Access Layer - stores and retrieves items from DynamoDB.
 *
 */
public class DataManager {

	private static final Logger LOG = LoggerFactory.getLogger(DataManager.class);
	private final DynamoDBMapper dynamoDBMapper;

	public DataManager() {
		final AmazonDynamoDB dynamoDbClient = AmazonDynamoDBClientBuilder.standard()
				.withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder())).build();
		this.dynamoDBMapper = new DynamoDBMapper(dynamoDbClient);
	}

	// Support for unit test mocking
	DataManager(final DynamoDBMapper dynamoDBMapper) {
		this.dynamoDBMapper = dynamoDBMapper;

	}

	/**
	 * Get {@link SavedGame} from the database. Returns <code>null</code> if the
	 * {@link SavedGame} could not be found in the database.
	 *
	 * @param session
	 *            Speech Session
	 * @return SavedGame
	 */
	public SavedGame getSavedGame(final Session session) {
		LOG.info("getSavedGame for user [{}]", session.getUser().getUserId());
		return Optional.ofNullable(loadItem(session)).map(i -> new SavedGame(session, i.getGameState())).orElse(null);
	}

	/**
	 * Saves the {@link SavedGame} to the database.
	 *
	 * @param game
	 *            SaveGame
	 */
	public void saveGame(final SavedGame game) {
		LOG.info("saveGame [{}]", game);
		final SavedGameItem item = new SavedGameItem();
		item.setUserId(game.getSession().getUser().getUserId());
		item.setGameState(game.getGameState());
		saveItem(item);
	}

	/**
	 * Loads an item from DynamoDB by Session.
	 *
	 * @param session
	 * @return SavedGameItem
	 */
	private SavedGameItem loadItem(final Session session) {
		final SavedGameItem item = new SavedGameItem();
		item.setUserId(session.getUser().getUserId());
		return dynamoDBMapper.load(item);
	}

	/**
	 * Stores an item in DynamoDB.
	 *
	 * @param dataItem
	 */
	private void saveItem(final SavedGameItem dataItem) {
		dynamoDBMapper.save(dataItem);
	}

}