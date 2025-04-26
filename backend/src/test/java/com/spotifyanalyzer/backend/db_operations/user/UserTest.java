package com.spotifyanalyzer.backend.db_operations.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testGettersAndSetters() {
        User user = new User();

        // Set values
        user.setId("1");
        user.setSpotifyUsername("test_user");
        user.setSpotifyId("spotify_123");
        user.setArtistsMinigameBestTimeInSeconds(100L);
        user.setTracksMinigameBestTimeInSeconds(200L);

        // Assert values
        assertEquals("1", user.getId());
        assertEquals("test_user", user.getSpotifyUsername());
        assertEquals("spotify_123", user.getSpotifyId());
        assertEquals(100L, user.getArtistsMinigameBestTimeInSeconds());
        assertEquals(200L, user.getTracksMinigameBestTimeInSeconds());
    }

    @Test
    void testNullValues() {
        User user = new User();

        // Assert default values are null
        assertNull(user.getId());
        assertNull(user.getSpotifyUsername());
        assertNull(user.getSpotifyId());
        assertNull(user.getArtistsMinigameBestTimeInSeconds());
        assertNull(user.getTracksMinigameBestTimeInSeconds());
    }
}