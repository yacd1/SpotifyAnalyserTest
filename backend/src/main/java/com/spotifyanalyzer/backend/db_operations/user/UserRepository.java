package com.spotifyanalyzer.backend.db_operations.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

    User findBySpotifyId(String spotifyId);

    // Custom query to find top minigame players
    List<User> findTop5ByOrderByArtistsMinigameBestTimeInSecondsAsc();

}

