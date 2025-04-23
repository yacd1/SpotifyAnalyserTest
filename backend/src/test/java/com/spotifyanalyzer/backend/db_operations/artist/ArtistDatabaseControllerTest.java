package com.spotifyanalyzer.backend.db_operations.artist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArtistDatabaseControllerTest {

    @Mock
    private ArtistService artistService;

    @InjectMocks
    private ArtistDatabaseController artistDatabaseController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchArtistSummary_Success() throws Exception {
        String artistName = "The Beatles";
        String summary = "The Beatles are arguably the most famous rock band of all time.";
        when(artistService.fetchArtistSummary(artistName)).thenReturn(summary);

        ResponseEntity<Map<String, String>> response = artistDatabaseController.fetchArtistSummary(artistName);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Map.of("ArtistSummary", summary), response.getBody());
        verify(artistService, times(1)).fetchArtistSummary(artistName);
    }

    @Test
    void testFetchArtistSummary_BadRequest() throws Exception {
        String artistName = null;

        ResponseEntity<Map<String, String>> response = artistDatabaseController.fetchArtistSummary(artistName);

        assertEquals(400, response.getStatusCodeValue());
        verify(artistService, never()).fetchArtistSummary(anyString());
    }

    @Test
    void testFetchArtistSummary_InternalServerError() throws Exception {
        String artistName = "The Beatles";
        when(artistService.fetchArtistSummary(artistName)).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<Map<String, String>> response = artistDatabaseController.fetchArtistSummary(artistName);

        assertEquals(500, response.getStatusCodeValue());
        verify(artistService, times(1)).fetchArtistSummary(artistName);
    }
}
