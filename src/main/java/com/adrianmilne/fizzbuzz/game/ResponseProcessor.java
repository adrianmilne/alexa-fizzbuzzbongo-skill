package com.adrianmilne.fizzbuzz.game;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adrianmilne.fizzbuzz.data.DataManager;
import com.adrianmilne.fizzbuzz.domain.GameState;
import com.adrianmilne.fizzbuzz.domain.SavedGame;
import com.adrianmilne.fizzbuzz.domain.Status;
import com.adrianmilne.fizzbuzz.util.FizzBuzzHelper;
import com.adrianmilne.fizzbuzz.util.TextUtils;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;

/**
 * Encapsulates detailed game logic - it processes voice requests and constructs
 * suitable responses. It delegates to the {@link DataManager} for persisting
 * {@link GameState} between interactions.
 *
 */
@SuppressWarnings("boxing")
public class ResponseProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseProcessor.class);

    private static final int DEFAULT_STARTING_NUMBER = 1;
    /*
     * The last number the computer said is stored in the database, e.g. 3. If
     * the user responds 4 correctly, the computer will respond 5 and update the
     * database by +2 (3 to 5) each time. However, if the user starts the game
     * e.g. at 10, the computer replies 11, and stores the user's start number
     * +1 initially. It then falls back to a +2 update. Bit convoluted - need to
     * revist this.
     */
    private static final int INCREMENT_NUMBER_COMPUTER_PROMPTS = 2;
    private static final int INCREMENT_NUMBER_USER_STARTS = 1;

    private final DataManager dataManager;

    public ResponseProcessor() {
        this.dataManager = new DataManager();
    }

    // Support for unit test mocking
    ResponseProcessor(final DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * A game has started and here we are processing the users response.
     *
     * @param session
     *        user session
     * @param savedGame
     *        saved game from the database
     * @param answer
     *        user's response
     * @return SpeechletResponse
     */
    public SpeechletResponse getFizzBuzzAskResponse(final Session session, final SavedGame savedGame,
            final String answer) {
        LOG.info("getFizzBuzzAskResponse");

        final GameState gameState = savedGame.getGameState();
        SpeechletResponse response;

        if (isUserStartingNewGame(gameState)) {
            if (isNumber(answer)) {
                // User is starting a new game
                savedGame.getGameState().setCurrentNumber(Integer.valueOf(answer).intValue());
                response = checkAnswerAndUpdateState(session, savedGame, answer, answer, INCREMENT_NUMBER_USER_STARTS);
            } else {
                // User started with fizz, buzz or similar - we need to bomb out
                response = getAskSpeechletResponse(TextUtils.getNonNumericStartText(),
                        TextUtils.getNonNumericRepromtText());
            }
        } else {
            // A game has started, or Alexa is starting a new one
            final String expected = gameState.getNextNumberAsString();
            response =
                    checkAnswerAndUpdateState(session, savedGame, expected, answer, INCREMENT_NUMBER_COMPUTER_PROMPTS);
        }

        return response;
    }

    /**
     * Is the user starting a new game as a result of Alexa asking them to
     * start?
     *
     * @param gameState
     *        game state
     * @return <code>true</code> if the users response will start a new game,
     *         <code>false</code> if a game is already underway
     */
    public boolean isUserStartingNewGame(final GameState gameState) {
        return Status.USER_STARTING_NEW_GAME == gameState.getStatus();
    }

    /**
     * We are just checking if the user has answered with a valid number. We
     * only need to check this when they are kicking off a new game with a
     * number. 'Fizz', etc are valid responses once a game has started, but we
     * can't start a game with those. We have to do this explicit check as they
     * are valid slot values - so Alexa will allow them.
     *
     * @param answer
     *        users response
     * @return <code>true</code> if the answer is a valid number,
     *         <code>false</code> if it is not
     */
    public boolean isNumber(final String answer) {
        return answer.chars().allMatch(Character::isDigit);
    }

    /**
     * Checks if the users response is valid or not, updates the database and
     * returns a response accordingly.
     *
     * @param session
     *        user session
     * @param savedGame
     *        saved game from database
     * @param expected
     *        expected result
     * @param answer
     *        users answer
     * @param increment
     *        how much we increment the currentNumber in the database if the
     *        answer is correct (sometimes this is 1, sometimes 2, depending on
     *        whether the user has answered first)
     * @return SpeechletResponse
     */
    public SpeechletResponse checkAnswerAndUpdateState(final Session session, final SavedGame savedGame,
            final String expected, final String answer, final int increment) {

        LOG.info("checkAnswerAndUpdateState: expected=[{}], answer=[{}], increment=[{}]", expected, answer, increment);
        SpeechletResponse response;
        if (expected.equals(answer)) {
            // Correct Answer
            savedGame.getGameState().setStatus(Status.GAME_STARTED);
            final int newNumber = saveGame(session, savedGame, increment);
            final String newNumberAsString = FizzBuzzHelper.convertNumberToString(newNumber);
            response = getAskSpeechletResponse(newNumberAsString, newNumberAsString);
        } else {
            // Incorrect Answer - game over
            final GameState gameState = savedGame.getGameState();
            final String speechText = TextUtils.getGameOverText(answer, expected, gameState.getCurrentScore());
            response = getAskSpeechletResponse(speechText, TextUtils.getGameOverRepromtText());
            resetGame(session, Status.PENDING, 0);
        }
        return response;
    }

    /**
     * Gets the response for starting a new game where no starting number has
     * been specifically set.
     *
     * @param session
     *        user session
     * @return SpeechletResponse
     */
    public SpeechletResponse getNewGameAskResponse(final Session session) {
        LOG.info("getNewGameAskResponse");

        final boolean userStarts = new Random().nextBoolean();
        if (userStarts) {
            resetGame(session, Status.USER_STARTING_NEW_GAME, 0);
            return getAskSpeechletResponse(TextUtils.getNewGameTextUserStarts(),
                    TextUtils.getNewGameTextUserStartsRepromtText());
        }
        return getNewGameAtNumberAskResponse(String.valueOf(DEFAULT_STARTING_NUMBER), session);
    }

    public SpeechletResponse getResumeGameAskResponse(final SavedGame savedGame) {
        LOG.info("getResumeGameAskResponse");
        return getResumeGameWithPrefixAskResponse(null, savedGame);
    }

    public SpeechletResponse getResumeGameWithPrefixAskResponse(final String prefixTest, final SavedGame savedGame) {
        LOG.info("getResumeGameWithPrefixAskResponse");

        final GameState gameState = savedGame.getGameState();
        final String currentNumber = gameState.getCurrentNumberAsString();
        final String previousNumber = gameState.getPreviousNumberAsString();
        return getAskSpeechletResponse(TextUtils.getResumeText(prefixTest, previousNumber, currentNumber),
                TextUtils.getRepromptText(currentNumber));
    }

    /**
     * Gets the response for starting a new game where a starting number has
     * been specifically set.
     *
     * @param suppliedStartNumber
     *        the starting number which was supplied
     * @param session
     *        user session
     * @return SpeechletResponse
     */
    public SpeechletResponse getNewGameAtNumberAskResponse(final String suppliedStartNumber, final Session session) {
        LOG.info("getNewGameAskResponse: suppliedStartNumber=[{}]", suppliedStartNumber);

        final int startNumber = Integer.valueOf(suppliedStartNumber).intValue();
        final String startNumberAsString = FizzBuzzHelper.convertNumberToString(startNumber);
        resetGame(session, Status.GAME_STARTED, startNumber);
        return getAskSpeechletResponse(TextUtils.getNewGameTextAlexaStarts(startNumberAsString),
                TextUtils.getRepromptText(startNumberAsString));
    }

    /**
     * Response when we did not understand what was said - we try and provide
     * some guidance of context in the game in our response.
     *
     * @param savedGame
     *        saved game from database
     * @return SpeechletResponse
     */
    public SpeechletResponse getUnrecognisedAskResponse(final SavedGame savedGame) {
        LOG.info("getUnrecognisedResponse");

        final GameState gameState = savedGame.getGameState();
        final String lastNumberAsString = FizzBuzzHelper.convertNumberToString(gameState.getCurrentNumber());
        return getAskSpeechletResponse(TextUtils.getUnrecognisedText(lastNumberAsString),
                TextUtils.getRepromptText(lastNumberAsString));
    }

    /**
     * Help response (if starting a new game).
     *
     * @return SpeechletResponse
     */
    public SpeechletResponse getNewGameHelpAskResponse() {
        return getAskSpeechletResponse(TextUtils.getNewGameHelpText(), TextUtils.getNewGameHelpText());
    }

    /**
     * Exit game response.
     *
     * @return SpeechletResponse
     */
    public SpeechletResponse getExitResponse() {
        return getTellSpeechletResponse(TextUtils.getExitText());
    }

    /**
     * Generates an 'Ask' response - i.e. one where we are expecting the user to
     * respond.
     *
     * @param speechText
     *        response text
     * @param repromptText
     *        reprompt text
     * @return SpeechletResponse
     */
    private SpeechletResponse getAskSpeechletResponse(final String speechText, final String repromptText) {
        // SSML example
        final SsmlOutputSpeech speech = new SsmlOutputSpeech();
        speech.setSsml("<speak>" + speechText + "</speak>");

        final SsmlOutputSpeech repromptSpeech = new SsmlOutputSpeech();
        repromptSpeech.setSsml("<speak>" + repromptText + "</speak>");
        final Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);

        return SpeechletResponse.newAskResponse(speech, reprompt);
    }

    /**
     * Generates a 'Tell' response - i.e. one where we are not expecting the
     * user to respond.
     *
     * @param speechText
     *        response text
     * @param repromptText
     *        reprompt text
     * @return SpeechletResponse
     */
    private SpeechletResponse getTellSpeechletResponse(final String speechText) {
        final SsmlOutputSpeech speech = new SsmlOutputSpeech();
        speech.setSsml("<speak>" + speechText + "</speak>");
        return SpeechletResponse.newTellResponse(speech);
    }

    /**
     * Updates a {@link SavedGame} - increments the currentNumber and
     * currentScore.
     *
     * @param session
     *        user session
     * @param savedGame
     *        existing saved game to update
     * @param increment
     *        amount to increment currentNumber by.
     * @return
     */
    private int saveGame(final Session session, final SavedGame savedGame, final int increment) {
        LOG.info("incrementNumberAndSave");

        final GameState game = savedGame.getGameState();
        final int newNumber = game.incrementNumber(increment);
        game.incrementScore();
        saveGameToDatabase(session, game);
        return newNumber;
    }

    /**
     * Saves a brand new game in the Database.
     *
     * @param session
     *        user session
     * @param startNumber
     *        starting number for the game
     */
    private void resetGame(final Session session, final Status status, final int startNumber) {
        LOG.info("saveNewGame");

        final SavedGame savedGame = dataManager.getSavedGame(session);
        GameState gameData;
        if (savedGame == null) {
            gameData = new GameState();
        } else {
            gameData = savedGame.getGameState();
        }
        gameData.setStatus(status);
        gameData.setCurrentNumber(startNumber);
        gameData.setCurrentScore(0);
        saveGameToDatabase(session, gameData);
    }

    /**
     * Saves the supplied {@link GameState} to DynamoDB, associated with the
     * user {@link Session}.
     *
     * @param session
     *        user session
     * @param gameState
     *        game state to persist
     */
    private void saveGameToDatabase(final Session session, final GameState gameState) {
        final SavedGame sg = new SavedGame(session, gameState);
        dataManager.saveGame(sg);
    }

    public SavedGame getSavedGame(final Session session) {
        return dataManager.getSavedGame(session);
    }
}
