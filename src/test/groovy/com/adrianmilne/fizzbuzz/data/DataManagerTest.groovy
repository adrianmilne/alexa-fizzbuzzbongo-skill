package com.adrianmilne.fizzbuzz.data

import com.adrianmilne.fizzbuzz.domain.GameState
import com.adrianmilne.fizzbuzz.domain.SavedGame
import com.amazon.speech.speechlet.Session
import com.amazon.speech.speechlet.User
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper

import spock.lang.Specification

/**
 * Unit Tests for {@link DataManager}.
 *
 */
class DataManagerTest extends Specification {

    private static final String USER_ID = 'user1'

    def 'test get saved game from database'(){
        given:
        def mockUser = Mock(User){ getUserId() >> USER_ID }
        def mockSession = Mock(Session){ getUser() >> mockUser }
        def mockGameState = Mock(GameState)
        def mockSavedGameItem = Mock(SavedGameItem){ getGameState() >> mockGameState }
        def mockMapper = Mock(DynamoDBMapper){
            load({ it.userId == USER_ID}) >> mockSavedGameItem
        }
        def manager = new DataManager(mockMapper)

        when:
        def savedGame = manager.getSavedGame(mockSession)

        then:
        assert savedGame.getSession() == mockSession
        assert savedGame.getGameState() == mockGameState
        assert savedGame.getSession().getUser().getUserId() == USER_ID
    }

    def 'test get saved game when none exists returns null'(){
        given:
        def mockUser = Mock(User)
        def mockSession = Mock(Session){ getUser() >> mockUser }
        def mockMapper = Mock(DynamoDBMapper){ loadItem(_) >> null }
        def manager = new DataManager(mockMapper)

        when:
        def savedGame = manager.getSavedGame(mockSession)

        then:
        assert null == savedGame
    }

    def 'test save game'(){
        given:
        def mockMapper = Mock(DynamoDBMapper)
        def mockUser = Mock(User){ getUserId() >> USER_ID }
        def mockSession = Mock(Session){ getUser() >> mockUser }
        def mockGameState = Mock(GameState)
        def mockSavedGame = Mock(SavedGame){
            getSession() >> mockSession
            getGameState() >> mockGameState
        }
        def manager = new DataManager(mockMapper)

        when:
        def savedGame = manager.saveGame(mockSavedGame)

        then:
        1 * mockMapper.save({
            it.userId == USER_ID &&
                    it.gameState == mockGameState
        })
    }
}
