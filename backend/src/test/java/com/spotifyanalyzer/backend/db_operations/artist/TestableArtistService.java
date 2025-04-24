package com.spotifyanalyzer.backend.db_operations.artist;

import org.springframework.web.client.RestTemplate;

class TestableArtistService extends ArtistServiceImplementation {
    private final RestTemplate mockTemplate;

    public TestableArtistService(RestTemplate mockTemplate) {
        this.mockTemplate = mockTemplate;
    }

    // Override to use mocked RestTemplate
    @Override
    RestTemplate createRestTemplate() {
        return mockTemplate;
    }
}
