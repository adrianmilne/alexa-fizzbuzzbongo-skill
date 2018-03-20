package com.adrianmilne.fizzbuzz.util;

import static java.lang.String.format;

/**
 * Helper to build responses.
 *
 */
public class TextUtils {

    private TextUtils() {
        // prevent instantiation
    }

    public static String getNewGameTextAlexaStarts(final String startingNumber) {
        final String response = "Starting a new FizzBuzzBongo game. I will start: <break strength=\"strong\"/> '%s'.";
        return format(response, startingNumber);
    }

    public static String getNewGameTextUserStarts() {
        return "Starting a new FizzBuzzBongo game. You start.";
    }

    public static String getResumeText(final String prefixText, final String yourOldNumber, final String computersNumber) {
        final StringBuilder sb = new StringBuilder();
        if(prefixText != null) {
            sb.append(prefixText);
        }
        sb.append("Resuming game. You had previously said: ");
        sb.append(yourOldNumber);
        sb.append(". My response was: ");
        sb.append(computersNumber);
        sb.append(". Ok - your turn - please say the next number in the sequence.");
        return sb.toString();
    }

    public static String getGameOverText(final String answer, final String expected, final int score) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Sorry - that's not correct. ");
        sb.append("You answered: ");
        sb.append(answer);
        sb.append(". The expected response was: ");
        sb.append(expected);
        sb.append(". Your score is: ");
        sb.append(score);
        sb.append(". Please say 'new game' to start a new game, or 'stop' to exit. ");
        return sb.toString();
    }

    public static String getGameOverRepromtText() {
        return "Please say 'new game' to start a new game, or 'stop' to exit.";
    }

    public static String getRepromptText(final String lastNumber) {
        final String response = "Please say the next number in the sequence. My last number was: '%s'. Please try again, or say 'help' for more information.";
        return format(response, lastNumber);
    }

    public static String getNewGameTextUserStartsRepromtText() {
        return "Please start by saying a number - your choice!";
    }

    public static String getUnrecognisedText(final String lastNumber) {
        final String response = "I'm sorry - I didn't recognise your answer. My last number was: '%s'. Please try again, or say 'help' for more information.";
        return format(response, lastNumber);
    }

    public static String getHelpText() {
        final StringBuilder sb = new StringBuilder();
        sb.append("This is a FizzBuzzBongo game. ");
        sb.append("You can say 'new game' to start at '1', or 'new game at 'number' to start at a different number. ");
        sb.append("We then each take turns to say the next number in the sequence, with the number incrementing each turn. ");
        sb.append("However, if the number you say is divisible by 3, you have to say 'fizz'. ");
        sb.append("If it's divisible by '5', it's 'buzz'. ");
        sb.append("divisible by '8' is 'bongo'. ");
        sb.append("divisible by '3' or '5' is 'fizzbuzz'. ");
        sb.append("'3' or '8' is 'fizzbongo'. ");
        sb.append("'5' or '8' is 'buzzbongo', ");
        sb.append("and if it's divisible by '3', '5' or '8' you have to say 'fizzbuzzbongo'. ");
        return sb.toString();
    }

    public static String getNewGameHelpText() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getHelpText());
        sb.append("Say 'new game' to start a new game, or 'stop' to quit");
        return sb.toString();
    }

    public static String getExitText() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Farewell. Thanks for playing 'Fizz Buzz Bongo' with me <break strength=\"strong\"/>. Hope we can play again soon!");
        return sb.toString();
    }

    public static String getNonNumericStartText() {
        return "You need to start with a number.";
    }

    public static String getNonNumericRepromtText() {
        return "Please say a number to start the game.";
    }
}
