package com.spotifyanalyzer.backend.db_operations.user;

import java.util.List;

public interface UserService
{
    public void registerUser(String username, String spotifyId) throws Exception;
    public List<User> getRegisteredUsers() throws Exception;
    public List<User> getTopMinigamePlayers() throws Exception;

    public boolean updateMinigameTimeById(String spotifyId, long newTime, String typeOfGame) throws Exception;
    public boolean deleteBothMinigameScoresById(String spotifyId) throws Exception;
    public Long getUserArtistMinigameTimeById(String spotifyId) throws Exception;
    public Long getUserTrackMinigameTimeById(String spotifyId) throws Exception;
    public boolean deleteArtistMinigameScoreById(String spotifyId) throws Exception;
    public boolean deleteTrackMinigameScoreById(String spotifyId) throws Exception;
}

