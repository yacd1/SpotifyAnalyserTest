package com.spotifyanalyzer.backend.db_operations.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplementationTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImplementation userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_NewUser() {
        String username = "testUser";
        String spotifyId = "spotify123";

        when(userRepository.findBySpotifyId(spotifyId)).thenReturn(null);

        userService.registerUser(username, spotifyId);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_ExistingUser_UpdateUsername() {
        String username = "newUsername";
        String spotifyId = "spotify123";
        User existingUser = new User();
        existingUser.setSpotifyId(spotifyId);
        existingUser.setSpotifyUsername("oldUsername");

        when(userRepository.findBySpotifyId(spotifyId)).thenReturn(existingUser);

        userService.registerUser(username, spotifyId);

        assertEquals(username, existingUser.getSpotifyUsername());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testGetRegisteredUsers_Success() throws Exception {
        List<User> mockUsers = List.of(new User());
        when(userRepository.findAll()).thenReturn(mockUsers);

        List<User> result = userService.getRegisteredUsers();

        assertEquals(mockUsers, result);
    }

    @Test
    void testGetRegisteredUsers_EmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(Exception.class, () -> userService.getRegisteredUsers());
        assertEquals("User is null", exception.getMessage());
    }

    @Test
    void testGetTopMinigamePlayers_Success() {
        User user1 = new User();
        user1.setSpotifyId("testId1");
        user1.setSpotifyUsername("testUser1");
        user1.setArtistsMinigameBestTimeInSeconds(100L);

        User user2 = new User();
        user2.setSpotifyId("testId2");
        user2.setSpotifyUsername("testUser2");
        user2.setArtistsMinigameBestTimeInSeconds(200L);

        List<User> mockUsers = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(mockUsers);

        List<User> result = userService.getTopMinigamePlayers();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    void testGetTopMinigamePlayers_NoPlayers() {
        when(userRepository.findTop5ByOrderByArtistsMinigameBestTimeInSecondsAsc()).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> userService.getTopMinigamePlayers());
        assertEquals("No top minigame players found", exception.getMessage());
    }

    @Test
    void testUpdateMinigameTimeById_ArtistsGame_BetterTime() {
        String spotifyId = "spotify123";
        User user = new User();
        user.setArtistsMinigameBestTimeInSeconds(100L);

        when(userRepository.findBySpotifyId(spotifyId)).thenReturn(user);

        boolean result = userService.updateMinigameTimeById(spotifyId, 90L, "artists");

        assertTrue(result);
        assertEquals(90L, user.getArtistsMinigameBestTimeInSeconds());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateMinigameTimeById_TracksGame_FirstTime() {
        String spotifyId = "spotify123";
        User user = new User();

        when(userRepository.findBySpotifyId(spotifyId)).thenReturn(user);

        boolean result = userService.updateMinigameTimeById(spotifyId, 150L, "tracks");

        assertTrue(result);
        assertEquals(150L, user.getTracksMinigameBestTimeInSeconds());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteBothMinigameScoresById_Success() {
        String spotifyId = "spotify123";
        User user = new User();
        user.setArtistsMinigameBestTimeInSeconds(100L);
        user.setTracksMinigameBestTimeInSeconds(150L);

        when(userRepository.findBySpotifyId(spotifyId)).thenReturn(user);

        boolean result = userService.deleteBothMinigameScoresById(spotifyId);

        assertTrue(result);
        assertNull(user.getArtistsMinigameBestTimeInSeconds());
        assertNull(user.getTracksMinigameBestTimeInSeconds());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetUserArtistMinigameTimeById_Success() {
        String spotifyId = "spotify123";
        User user = new User();
        user.setArtistsMinigameBestTimeInSeconds(100L);

        when(userRepository.findBySpotifyId(spotifyId)).thenReturn(user);

        Long result = userService.getUserArtistMinigameTimeById(spotifyId);

        assertEquals(100L, result);
    }

    @Test
    void testDeleteArtistMinigameScoreById_Success() {
        String spotifyId = "spotify123";
        User user = new User();
        user.setArtistsMinigameBestTimeInSeconds(100L);

        when(userRepository.findBySpotifyId(spotifyId)).thenReturn(user);

        boolean result = userService.deleteArtistMinigameScoreById(spotifyId);

        assertTrue(result);
        assertNull(user.getArtistsMinigameBestTimeInSeconds());
        verify(userRepository, times(1)).save(user);
    }
}