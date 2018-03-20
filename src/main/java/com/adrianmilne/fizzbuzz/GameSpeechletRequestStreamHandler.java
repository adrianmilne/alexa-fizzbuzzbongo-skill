package com.adrianmilne.fizzbuzz;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

/**
 * Alexa specific Handler implementation for deploying the skill as an AWS
 * Lambda.
 */
public class GameSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

    private static final Set<String> SUPPORTED_APPLICATION_IDS = new HashSet<>();

    static {
        /*
         * NOTE: This needs to be updated with your Skill ID after you have
         * created your Alexa skill - you can find this in the Amazon Developer
         * Console - see <<BLOG POST>> for more instructions.
         */
        SUPPORTED_APPLICATION_IDS.add("INSERT_APPLICATION_ID_HERE");
    }

    public GameSpeechletRequestStreamHandler() {
        super(new GameSpeechlet(), SUPPORTED_APPLICATION_IDS);
    }
}
