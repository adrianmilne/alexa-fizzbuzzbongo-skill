package com.adrianmilne.fizzbuzz.util;

/**
 * Encapsulates the core FizzBuzzBongo game logic.
 *
 */
public class FizzBuzzHelper {

    private static int FIZZ_NUMBER = 3;
    private static int BUZZ_NUMBER = 5;
    private static int BONGO_NUMBER = 8;

    private FizzBuzzHelper() {
        // prevent instantiation
    }

    /**
     * Checks the number - if it is divisible by 3, it will return 'buzz'. If it
     * is divisible by 5 it will return 'fizz'. If it is divisible by 10 it will
     * return 'bongo'. If it is divisible by 3 and 5, it will return 'fizzbuzz',
     * by 3 and 10 it will return 'fizzbongo', by 5 and 10 it will return
     * 'buzzbongo', by 3, 5, and 10 it will return 'fizzbuzzbongo'. Otherwise it
     * will return the original number.
     *
     * @param number
     * @return 'fizz', 'buzz', 'fizzbuzz', 'fizzbongo', 'buzzbongo',
     *         'fizzbuzzbongo' or the supplied number
     */
    public static String convertNumberToString(final int number) {
        if (number % FIZZ_NUMBER == 0 && number % BUZZ_NUMBER == 0 && number % BONGO_NUMBER == 0) {
            return "fizzbuzzbongo";
        } else if (number % FIZZ_NUMBER == 0 && number % BUZZ_NUMBER == 0) {
            return "fizzbuzz";
        } else if (number % FIZZ_NUMBER == 0 && number % BONGO_NUMBER == 0) {
            return "fizzbongo";
        } else if (number % BUZZ_NUMBER == 0 && number % BONGO_NUMBER == 0) {
            return "buzzbongo";
        } else if (number % FIZZ_NUMBER == 0) {
            return "fizz";
        } else if (number % BUZZ_NUMBER == 0) {
            return "buzz";
        } else if (number % BONGO_NUMBER == 0) {
            return "bongo";
        } else {
            return String.valueOf(number);
        }
    }

}
