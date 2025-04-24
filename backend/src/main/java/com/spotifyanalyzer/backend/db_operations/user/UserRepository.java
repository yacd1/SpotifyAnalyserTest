package com.spotifyanalyzer.backend.db_operations.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

    User findBySpotifyUsername(String spotifyUsername);

    User findBySpotifyId(String spotifyId);

    @Query(value = "{ 'spotify_username': ?0 }")
    List<User> findAllBySpotifyUsername(String spotifyUsername);

    @Query(value = "{ 'spotify_id': ?0 }")
    List<User> findAllBySpotifyId(String spotifyId);

    // Custom query to find top minigame players
    List<User> findTop5ByOrderByArtistsMinigameBestTimeInSecondsAsc();

}

