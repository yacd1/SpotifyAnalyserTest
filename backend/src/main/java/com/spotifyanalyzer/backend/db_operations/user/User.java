package com.spotifyanalyzer.backend.db_operations.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Document(collection = "UserCollection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    // (ben) refactored 28/4 to use Lombok

    @Id
    private String id;

    @Field("spotify_username")
    private String spotifyUsername;

    @Field("spotify_id")
    private String spotifyId;

    @Field("artists_minigame_best_time_in_seconds")
    private Long artistsMinigameBestTimeInSeconds;

    @Field("tracks_minigame_best_time_in_seconds")
    private Long tracksMinigameBestTimeInSeconds;
}