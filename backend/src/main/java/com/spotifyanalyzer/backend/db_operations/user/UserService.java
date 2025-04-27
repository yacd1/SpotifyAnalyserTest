package com.spotifyanalyzer.backend.db_operations.user;

import java.util.List;

public interface UserService
{
    void registerUser(String username, String spotifyId);
    List<User> getRegisteredUsers() throws Exception;
    List<User> getTopMinigamePlayers() throws Exception;

    boolean updateMinigameTimeById(String spotifyId, long newTime, String typeOfGame);
    boolean deleteBothMinigameScoresById(String spotifyId);
    Long getUserArtistMinigameTimeById(String spotifyId);
    Long getUserTrackMinigameTimeById(String spotifyId);
    boolean deleteArtistMinigameScoreById(String spotifyId);
    boolean deleteTrackMinigameScoreById(String spotifyId);
}

