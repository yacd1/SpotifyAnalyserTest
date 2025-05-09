package com.spotifyanalyzer.backend.controller;

import com.spotifyanalyzer.backend.authservice.PythonService;
import com.spotifyanalyzer.backend.authservice.SpotifyService;
import com.spotifyanalyzer.backend.dto.SpotifyAuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import jakarta.servlet.http.HttpSession;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpotifyControllerTest {

    @Mock
    private SpotifyService spotifyService;

    @Mock
    private PythonService pythonService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private SpotifyController spotifyController;

    private final String VALID_TOKEN = "valid_access_token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(spotifyController, "pythonServiceUrl", "http://localhost:5000");
        when(session.getAttribute("spotify_access_token")).thenReturn(VALID_TOKEN);
        when(session.getAttribute("spotify_token_expiry")).thenReturn(System.currentTimeMillis() + 3600000); // 1hr in future
    }

    @Test
    void checkAuthStatus() {
        Map<String, Object> profileInfo = Map.of(
                "id", "user123",
                "display_name", "Test User"
        );

        when(spotifyService.getUserProfile(VALID_TOKEN)).thenReturn(profileInfo);

        ResponseEntity<Map<String, Object>> response = spotifyController.checkAuthStatus(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("authenticated"));
        assertEquals(profileInfo, response.getBody().get("profile"));
    }

    @Test
    void getTopArtists() {
        Map<String, Object> mockResponse = new HashMap<>();
        List<Map<String, Object>> items = List.of(
                Map.of("id", "artist1", "name", "artist one"),
                Map.of("id", "artist2", "name", "artist two")
        );
        mockResponse.put("items", items);
        when(spotifyService.makeSpotifyRequest(anyString(), eq(HttpMethod.GET), eq(VALID_TOKEN), isNull(), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<?> response = spotifyController.getTopArtists("medium_term", 10, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void getTopTracks() {
        Map<String, Object> mockResponse = new HashMap<>();
        List<Map<String, Object>> items = List.of(
                Map.of("id", "track1", "name", "track one"),
                Map.of("id", "track2", "name", "track two")
        );
        mockResponse.put("items", items);
        when(spotifyService.makeSpotifyRequest(anyString(), eq(HttpMethod.GET), eq(VALID_TOKEN), isNull(), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<?> response = spotifyController.getTopTracks("medium_term", 10, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void getRecentlyPlayed() {
        Map<String, Object> mockResponse = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("played_at", "2025-04-20T12:00:00Z");
        item.put("track", Map.of("id", "track1", "name", "recent track"));
        items.add(item);
        mockResponse.put("items", items);

        when(spotifyService.makeSpotifyRequest(anyString(), eq(HttpMethod.GET), eq(VALID_TOKEN), isNull(), eq(Object.class)))
                .thenReturn(mockResponse);

        ResponseEntity<?> response = spotifyController.getRecentlyPlayed(5, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void getTopGenre() {
        Map<String, Object> topArtistsResponse = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();

        Map<String, Object> artist1 = new HashMap<>();
        artist1.put("genres", List.of("rock", "pop"));
        items.add(artist1);

        Map<String, Object> artist2 = new HashMap<>();
        artist2.put("genres", List.of("pop", "electronic"));
        items.add(artist2);

        topArtistsResponse.put("items", items);

        when(spotifyService.makeSpotifyRequest(anyString(), eq(HttpMethod.GET), eq(VALID_TOKEN), isNull(), eq(Map.class)))
                .thenReturn(topArtistsResponse);

        ResponseEntity<?> response = spotifyController.getTopGenre(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("pop", responseBody.get("topGenre"));
    }

    @Test
    void getFakeRecommendations() {
        // mock some recently played track for this test
        Map<String, Object> recentlyPlayedResponse = new HashMap<>();
        List<Map<String, Object>> recentItems = new ArrayList<>();
        Map<String, Object> recentItem = new HashMap<>();

        Map<String, Object> originalTrack = new HashMap<>();
        originalTrack.put("id", "track1");
        originalTrack.put("name", "original track");
        originalTrack.put("artists", List.of(Map.of("name", "test artist")));

        recentItem.put("track", originalTrack);
        recentItems.add(recentItem);
        recentlyPlayedResponse.put("items", recentItems);

        // mock whatever results are returned in the search
        Map<String, Object> searchResponse = new HashMap<>();
        Map<String, Object> tracksResult = new HashMap<>();
        List<Map<String, Object>> trackItems = new ArrayList<>();

        // adding a recommended track to it
        Map<String, Object> recommendedTrack = new HashMap<>();
        recommendedTrack.put("id", "track2");
        recommendedTrack.put("name", "recommended track");
        trackItems.add(recommendedTrack);

        tracksResult.put("items", trackItems);
        searchResponse.put("tracks", tracksResult);

        when(spotifyService.makeSpotifyRequest(contains("/me/player/recently-played"), any(), any(), any(), any()))
                .thenReturn(recentlyPlayedResponse);
        when(spotifyService.makeSpotifyRequest(contains("/search"), any(), any(), any(), any()))
                .thenReturn(searchResponse);

        ResponseEntity<?> response = spotifyController.getFakeRecommendations(session);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getSearch() {
        Map<String, Object> searchResponse = new HashMap<>();
        Map<String, Object> tracks = new HashMap<>();
        tracks.put("items", List.of(
                Map.of("id", "track1", "name", "search result 1")
        ));
        searchResponse.put("tracks", tracks);

        when(spotifyService.makeSpotifyRequest(anyString(), eq(HttpMethod.GET), eq(VALID_TOKEN), isNull(), eq(Object.class)))
                .thenReturn(searchResponse);

        ResponseEntity<?> response = spotifyController.getSearch("test query", "5", "track", session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(searchResponse, response.getBody());
    }

    @Test
    void getArtistInfo() {
        String artistId = "artist";
        Map<String, Object> artistResponse = Map.of(
                "id", artistId,
                "name", "test artist",
                "genres", List.of("pop", "rock"),
                "popularity", 85 // could be anything
        );

        when(spotifyService.makeSpotifyRequest(eq("/artists/" + artistId), any(), any(), any(), any()))
                .thenReturn(artistResponse);

        ResponseEntity<?> response = spotifyController.getArtistInfo(artistId, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(artistResponse, response.getBody());
    }

    @Test
    void logout() {
        ResponseEntity<Void> response = spotifyController.logout(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(session).removeAttribute("spotify_access_token");
        verify(session).removeAttribute("spotify_refresh_token");
        verify(session).removeAttribute("spotify_token_expiry");
    }
}