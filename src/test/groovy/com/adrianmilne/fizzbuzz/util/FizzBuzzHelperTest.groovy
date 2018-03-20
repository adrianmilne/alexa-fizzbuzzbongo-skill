package com.adrianmilne.fizzbuzz.util

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Unit Tests for {@link FizzBuzzHelper}.
 *
 */
class FizzBuzzHelperTest extends Specification {

    @Unroll
    def 'test fizz buzz logic: #input should be #expectedResponse'(){

        when:
        def response = FizzBuzzHelper.convertNumberToString(input)

        then:
        response == expectedResponse

        where:
        input   || expectedResponse
        1       || '1'
        2       || '2'
        3       || 'fizz'
        5       || 'buzz'
        8       || 'bongo'
        15      || 'fizzbuzz'
        16      || 'bongo'
        30      || 'fizzbuzz'
        40      || 'buzzbongo'
        120     || 'fizzbuzzbongo'
    }
}
