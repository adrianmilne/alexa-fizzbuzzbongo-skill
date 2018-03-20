package com.adrianmilne.fizzbuzz;

import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adrianmilne.fizzbuzz.game.GameManager;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazonaws.xray.AWSXRay;

/**
 * FizzBuzz Game Speechlet implementation - handles the initial Alexa
 * interactions to determine what action the user is performing in order to hand
 * off to the {@link GameManager} for processing and supplying a response.
 *
 */
public class GameSpeechlet implements Speechlet {

    private static final Logger LOG = LoggerFactory.getLogger(GameSpeechlet.class);
    private final GameManager gameManager;

    public GameSpeechlet() {
        this.gameManager = new GameManager();
    }

    // Support for unit test mocking
    public GameSpeechlet(final GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session) throws SpeechletException {
        LOG.info("onSessionStarted requestId=[{}], sessionId=[{}]", request.getRequestId(), session.getSessionId());
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
        LOG.info("onSessionEnded requestId=[{}], sessionId=[{}]", request.getRequestId(), session.getSessionId());
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session) throws SpeechletException {
        LOG.info("onLaunch requestId=[{}], sessionId=[{}]", request.getRequestId(), session.getSessionId());
        AWSXRay.beginSubsegment("Speechlet: On Launch");

        try {
            return gameManager.getLaunchIntentResponse(session);
        } finally {
            AWSXRay.endSubsegment();
        }
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException {
        LOG.info("onIntent requestId=[{}], sessionId=[{}]", request.getRequestId(), session.getSessionId());
        AWSXRay.beginSubsegment("Speechlet: On Intent");

        try {
            final Intent intent = request.getIntent();
            LOG.info("Intent Name=[{}]", intent.getName());

            if ("NewGameIntent".equals(intent.getName())) {
                return gameManager.getNewGameIntentResponse(intent, session);
            } else if ("FizzBuzzIntent".equals(intent.getName())) {
                return gameManager.getFizzBuzzIntentResponse(intent, session);
            } else if ("AMAZON.HelpIntent".equals(intent.getName())) {
                return gameManager.getHelpIntentResponse(session);
            } else if ("AMAZON.CancelIntent".equals(intent.getName())) {
                return gameManager.getExitIntentResponse();
            } else if ("AMAZON.StopIntent".equals(intent.getName())) {
                return gameManager.getExitIntentResponse();
            } else {
                throw new SpeechletException(format("Unrecognized intent: [%s]", intent.getName()));
            }
        } finally {
            AWSXRay.endSubsegment();
        }
    }
}
