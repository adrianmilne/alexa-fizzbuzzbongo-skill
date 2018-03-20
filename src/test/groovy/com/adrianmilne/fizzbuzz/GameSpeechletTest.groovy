package com.adrianmilne.fizzbuzz

import com.adrianmilne.fizzbuzz.game.GameManager
import com.amazon.speech.slu.Intent
import com.amazon.speech.speechlet.IntentRequest
import com.amazon.speech.speechlet.LaunchRequest
import com.amazon.speech.speechlet.Session
import com.amazon.speech.speechlet.SpeechletResponse
import com.amazonaws.xray.AWSXRay

import spock.lang.Specification

/**
 * Unit Tests for {@link GameSpeechlet}.
 *
 */
class GameSpeechletTest extends Specification {

    def 'test onLaunch'(){
        given:
        def mockSession = Mock(Session)
        def mockSpeechletResponse = Mock(SpeechletResponse)
        def mockGameManager = Mock(GameManager){ getLaunchIntentResponse(mockSession) >> mockSpeechletResponse }
        def gameSpeechlet = new GameSpeechlet(mockGameManager)

        when:
        AWSXRay.beginSegment('Simulated AWS XRay Segment Context - needs a wrapper context')
        gameSpeechlet.onLaunch(Mock(LaunchRequest), mockSession)
        AWSXRay.endSegment()

        then:
        1 * mockGameManager.getLaunchIntentResponse(mockSession) >> mockSpeechletResponse
    }

    def 'test new game intent'(){
        given:
        def mockSession = Mock(Session)
        def mockSpeechletResponse = Mock(SpeechletResponse)
        def mockGameManager = Mock(GameManager){ getLaunchIntentResponse(mockSession) >> mockSpeechletResponse }
        def gameSpeechlet = new GameSpeechlet(mockGameManager)
        def intent = new Intent("NewGameIntent", null, null)
        def mockIntentRequest = Mock(IntentRequest){ getIntent() >> intent }

        when:
        AWSXRay.beginSegment('Simulated AWS XRay Segment Context - needs a wrapper context')
        gameSpeechlet.onIntent(mockIntentRequest, mockSession)
        AWSXRay.endSegment()

        then:
        1 * mockGameManager.getNewGameIntentResponse(intent, mockSession) >> mockSpeechletResponse
    }

    def 'test fizzbuzz intent'(){
        given:
        def mockSession = Mock(Session)
        def mockSpeechletResponse = Mock(SpeechletResponse)
        def mockGameManager = Mock(GameManager){ getLaunchIntentResponse(mockSession) >> mockSpeechletResponse }
        def gameSpeechlet = new GameSpeechlet(mockGameManager)
        def intent = new Intent("FizzBuzzIntent", null, null)
        def mockIntentRequest = Mock(IntentRequest){ getIntent() >> intent }

        when:
        AWSXRay.beginSegment('Simulated AWS XRay Segment Context - needs a wrapper context')
        gameSpeechlet.onIntent(mockIntentRequest, mockSession)
        AWSXRay.endSegment()

        then:
        1 * mockGameManager.getFizzBuzzIntentResponse(intent, mockSession) >> mockSpeechletResponse
    }
}
