package com.spotifyanalyzer.backend;

import com.spotifyanalyzer.backend.authservice.SpotifyService;
import com.spotifyanalyzer.backend.config.SpotifyConfig;
import com.spotifyanalyzer.backend.exceptions.SpotifyAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SpotifyServiceTest {

    @Mock
    private SpotifyConfig spotifyConfig;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SpotifyService spotifyService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // testing getUserProfile() from SpotifyService
    @Test
    public void testGetUserProfileSuccess() {
        // setting up some mock data - fake access token and endpoint(which is real)
        String token = "fake-valid-token";
        String url = "https://api.spotify.com/v1/me";


        //fake correct response in a response entity (to stimulate fake successfull 200 HTTP)
        Map<String, Object> expectedResponse = Map.of("display_name", "testuser");
        ResponseEntity<Map> mockResponse = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        //stimulating real method
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        //setting up correct mock response - so we don't actually make an api call to spotify
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(mockResponse);

        Map<String, Object> result = spotifyService.getUserProfile(token);

        // check it passes the test and the token is sent and user is returned
        assertEquals("testuser", result.get("display_name"));
        verify(restTemplate).exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    public void testGetAuthorisationUrl() {
        // Arrange
        String mockClientId = "test-client-id";
        when(spotifyConfig.getClientId()).thenReturn(mockClientId);

        // Act
        String url = spotifyService.getAuthorisationUrl();

        // Assert
        assertNotNull(url);
        assertTrue(url.contains("client_id=" + mockClientId));
        assertTrue(url.contains("https://accounts.spotify.com/authorize"));
        assertTrue(url.contains("response_type=code"));
        assertTrue(url.contains("scope=")); // scopes are also included
        assertTrue(url.contains("redirect_uri=")); // ensure redirect is present
    }


    @Test
    public void testGetUserProfileFailsWithBadStatus() {
        // Arrange
        String token = "bad-token";
        ResponseEntity<Map> badResponse = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(badResponse);

        // Act & Assert
        assertThrows(SpotifyAuthException.class, () -> spotifyService.getUserProfile(token));
    }
}
