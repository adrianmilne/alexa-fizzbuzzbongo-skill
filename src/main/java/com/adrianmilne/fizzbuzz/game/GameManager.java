package com.adrianmilne.fizzbuzz.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adrianmilne.fizzbuzz.domain.SavedGame;
import com.adrianmilne.fizzbuzz.domain.Status;
import com.adrianmilne.fizzbuzz.util.TextUtils;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;

/**
 * Encapsulates high level game logic - it interrogates Intents and current game
 * state and decides how best to respond to the request.
 *
 */
public class GameManager {

    private static final Logger LOG = LoggerFactory.getLogger(GameManager.class);
    private static final String SLOT_FIZZBUZZ = "FizzBuzz";
    private static final String SLOT_NUMBER = "Number";

    private final ResponseProcessor responseProcessor;

    public GameManager() {
        this.responseProcessor = new ResponseProcessor();
    }

    // Support for unit test mocking
    GameManager(final ResponseProcessor responseHelper) {
        this.responseProcessor = responseHelper;
    }

    /**
     * Launch Game. State is persisted between launches - it will pick up any
     * games that were abandoned mid way through.
     *
     * @param session
     *        user session
     * @return SpeechletResponse
     */
    public SpeechletResponse getLaunchIntentResponse(final Session session) {
        LOG.info("getLaunchResponse");
        final SavedGame savedGame = responseProcessor.getSavedGame(session);
        if (isNewGame(savedGame)) {
            return responseProcessor.getNewGameAskResponse(session);
        }
        return responseProcessor.getResumeGameAskResponse(savedGame);
    }

    /**
     * Starts a New Game. If a starting number has been specified - Alexa will
     * start at that number, otherwise we will randomly choose whether Alexa
     * starts (at 1), or lets the user start (at whatever number they choose).
     *
     * @param intent
     *        alexa intent
     * @param session
     *        user session
     * @return SpeechletResponse
     */
    public SpeechletResponse getNewGameIntentResponse(final Intent intent, final Session session) {
        LOG.info("getNewGameResponse");
        final String suppliedStartNumber = intent.getSlot(SLOT_NUMBER).getValue();
        if (suppliedStartNumber == null) {
            return responseProcessor.getNewGameAskResponse(session);
        }
        return responseProcessor.getNewGameAtNumberAskResponse(suppliedStartNumber, session);
    }

    /**
     * Process the users response, and return the computers response.
     *
     * @param intent
     *        alexa intent
     * @param session
     *        user session
     * @return SpeechletResponse
     */
    public SpeechletResponse getFizzBuzzIntentResponse(final Intent intent, final Session session) {
        LOG.info("getFizzBuzzResponse");
        final String answerKey =
                intent.getSlot(SLOT_FIZZBUZZ).getValue() == null ? intent.getSlot(SLOT_NUMBER).getValue()
                        : intent.getSlot(SLOT_FIZZBUZZ).getValue();

                LOG.info("You answered [{}]", answerKey);

                final SavedGame savedGame = responseProcessor.getSavedGame(session);
                SpeechletResponse response;

                if (savedGame == null) {
                    response = responseProcessor.getNewGameAskResponse(session);
                } else if (answerKey == null) {
                    response = responseProcessor.getUnrecognisedAskResponse(savedGame);
                } else {
                    response = responseProcessor.getFizzBuzzAskResponse(session, savedGame, answerKey);
                }
                return response;
    }

    /**
     * Generate Help Response.
     */
    public SpeechletResponse getHelpIntentResponse(final Session session) {
        LOG.info("getHelpIntentResponse");
        final SavedGame savedGame = responseProcessor.getSavedGame(session);
        if (isNewGame(savedGame)) {
            return responseProcessor.getNewGameHelpAskResponse();
        }
        return responseProcessor.getResumeGameWithPrefixAskResponse(TextUtils.getHelpText(), savedGame);
    }

    /**
     * Generate Exit Game Response.
     */
    public SpeechletResponse getExitIntentResponse() {
        LOG.info("getExitIntentResponse");
        return responseProcessor.getExitResponse();
    }

    /**
     * Are we due to start a new game?
     *
     * @param savedGame
     *        saved game
     * @return <code>true</code> if we should start a new game,
     *         <code>false</code> if we are currently playing a game.
     */
    private boolean isNewGame(final SavedGame savedGame) {
        return savedGame == null || savedGame.getGameState().getStatus() == Status.PENDING;
    }

}
