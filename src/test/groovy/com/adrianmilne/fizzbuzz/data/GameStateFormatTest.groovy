package com.adrianmilne.fizzbuzz.data

import com.adrianmilne.fizzbuzz.domain.GameState

import spock.lang.Specification

/**
 * Unit Tests for {@link GameStateFormat}.
 *
 */
class GameStateFormatTest extends Specification {

    def 'test convert'(){
        given:
        def mockGameState = Mock(GameState){ getCurrentNumber() >> 99 }
        def converter = new GameStateFormat.Converter()

        when:
        def json = converter.convert(mockGameState)

        then:
        assert '{"status":null,"currentNumber":99,"currentScore":0}' == json
    }

    def 'test unconvert'(){
        given:
        def converter = new GameStateFormat.Converter()

        when:
        def gameState = converter.unconvert('{"status":null,"currentNumber":99,"currentScore":0}')

        then:
        assert 99 == gameState.getCurrentNumber()
    }
}
