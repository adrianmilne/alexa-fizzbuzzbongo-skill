package com.adrianmilne.fizzbuzz.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.adrianmilne.fizzbuzz.domain.GameState;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Converter to marshall/unmarshall GameState to/from JSON when accessing DynamoDB.
 * Could model this as individual attributes, rather than embedded JSON, but decided to
 * do it this way as we will only ever retrieve all GameState data every time.
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@DynamoDBTypeConverted(converter = GameStateFormat.Converter.class)
public @interface GameStateFormat {

    public static class Converter implements DynamoDBTypeConverter<String, GameState> {

        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        @Override
        public String convert(final GameState gameState) {
            try {
                return OBJECT_MAPPER.writeValueAsString(gameState);
            } catch (final JsonProcessingException e) {
                throw new IllegalStateException("Unable to marshall game data", e);
            }
        }

        @Override
        public GameState unconvert(final String json) {
            try {
                return OBJECT_MAPPER.readValue(json, new TypeReference<GameState>() {
                /* empty */});
            } catch (final Exception e) {
                throw new IllegalStateException("Unable to unmarshall game data value", e);
            }
        }

    }
}
