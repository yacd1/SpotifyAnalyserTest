package com.spotifyanalyzer.backend.db_operations.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDatabaseControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserDatabaseController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() throws Exception {
        String username = "testUser";
        String spotifyId = "spotify123";

        doNothing().when(userService).registerUser(username, spotifyId);

        ResponseEntity<?> response = controller.register(username, spotifyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.singletonMap("message", "User registered"), response.getBody());
        verify(userService, times(1)).registerUser(username, spotifyId);
    }

    @Test
    void testGetRegisteredUser_Success() throws Exception {
        List<User> mockUsers = Arrays.asList(new User(), new User());
        when(userService.getRegisteredUsers()).thenReturn(mockUsers);

        ResponseEntity<List<User>> response = controller.getRegisteredUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUsers, response.getBody());
        verify(userService, times(1)).getRegisteredUsers();
    }

    @Test
    void testGetRegisteredUser_Error() throws Exception {
        when(userService.getRegisteredUsers()).thenReturn(null);

        ResponseEntity<List<User>> response = controller.getRegisteredUser();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetTopMinigamePlayers_Success() throws Exception {
        List<User> mockUsers = Arrays.asList(new User(), new User());
        when(userService.getTopMinigamePlayers()).thenReturn(mockUsers);

        ResponseEntity<List<User>> response = controller.getTopMinigamePlayers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUsers, response.getBody());
        verify(userService, times(1)).getTopMinigamePlayers();
    }

    @Test
    void testGetTopMinigamePlayers_Error() throws Exception {
        when(userService.getTopMinigamePlayers()).thenReturn(null);

        ResponseEntity<List<User>> response = controller.getTopMinigamePlayers();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetUserArtistMinigameTimeById_Success() throws Exception {
        String spotifyId = "spotify123";
        Long expectedTime = 1000L;
        when(userService.getUserArtistMinigameTimeById(spotifyId)).thenReturn(expectedTime);

        ResponseEntity<?> response = controller.getUserArtistMinigameTimeById(spotifyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Map.of("artistMinigameTime", expectedTime), response.getBody());
    }

    @Test
    void testGetUserArtistMinigameTimeById_NotFound() throws Exception {
        String spotifyId = "spotify123";
        when(userService.getUserArtistMinigameTimeById(spotifyId)).thenReturn(null);

        ResponseEntity<?> response = controller.getUserArtistMinigameTimeById(spotifyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Map.of("artistMinigameTime", false), response.getBody());
    }

    @Test
    void testUpdateArtistMinigameTimeById_Success() throws Exception {
        String spotifyId = "spotify123";
        long newTime = 1000L;
        when(userService.updateMinigameTimeById(spotifyId, newTime, "artists")).thenReturn(true);

        ResponseEntity<?> response = controller.updateArtistMinigameTimeById(spotifyId, newTime);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Map.of("updated", true), response.getBody());
    }

    @Test
    void testDeleteBothMinigameScoresById_Success() throws Exception {
        String spotifyId = "spotify123";
        when(userService.deleteBothMinigameScoresById(spotifyId)).thenReturn(true);

        ResponseEntity<?> response = controller.deleteMinigameScoreById(spotifyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Map.of("message", "Minigame scores deleted successfully"), response.getBody());
    }

    @Test
    void testDeleteArtistMinigameScoreById_Success() throws Exception {
        String spotifyId = "spotify123";
        when(userService.deleteArtistMinigameScoreById(spotifyId)).thenReturn(true);

        ResponseEntity<?> response = controller.deleteArtistMinigameScoreById(spotifyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Map.of("message", "Artist minigame score deleted successfully"), response.getBody());
    }

    @Test
    void testDeleteTrackMinigameScoreById_Success() throws Exception {
        String spotifyId = "spotify123";
        when(userService.deleteTrackMinigameScoreById(spotifyId)).thenReturn(true);

        ResponseEntity<?> response = controller.deleteTrackMinigameScoreById(spotifyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Map.of("message", "Track minigame score deleted successfully"), response.getBody());
    }
}