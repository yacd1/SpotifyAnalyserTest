package com.spotifyanalyzer.backend.db_operations.user;

import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImplementation implements UserService
{
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Synchronized
    public void registerUser(String username, String spotifyId){
        // (ben) check if the user already exists by spotifyId (more reliable than username)
        User existingUser = userRepository.findBySpotifyId(spotifyId);
        if (existingUser != null) {
            // user already exists, update username if it changed
            if (!existingUser.getSpotifyUsername().equals(username)) {
                existingUser.setSpotifyUsername(username);
                userRepository.save(existingUser);
            }
            return;
        }
        User user = new User();
        user.setSpotifyUsername(username);
        user.setSpotifyId(spotifyId);
        user.setArtistsMinigameBestTimeInSeconds(null);
        user.setTracksMinigameBestTimeInSeconds(null);
        userRepository.save(user);
    }

    @Override
    public List<User> getRegisteredUsers() throws Exception
    {
        List<User>users=userRepository.findAll();
        if(!users.isEmpty())
        {
            return users;
        }
        throw new Exception("User is null");
    }

    @Override
    public List<User> getTopMinigamePlayers() throws Exception
    {
        List<User> users = userRepository.findTop5ByOrderByArtistsMinigameBestTimeInSecondsAsc();
        if (users != null)
        {
            return users;
        }
        throw new Exception("No top minigame players found");
    }

    @Override
    public boolean updateMinigameTimeById(String spotifyId, long newTime, String typeOfGame) throws Exception {
        User user = getUserFromSpotifyId(spotifyId);
        if (user == null) return false;

        if (typeOfGame.equals("artists")) {
            if (user.getArtistsMinigameBestTimeInSeconds() == null || newTime < user.getArtistsMinigameBestTimeInSeconds()) {
                user.setArtistsMinigameBestTimeInSeconds(newTime);
                userRepository.save(user);
                return true;
            }
        } else if (typeOfGame.equals("tracks")) {
            if (user.getTracksMinigameBestTimeInSeconds() == null || newTime < user.getTracksMinigameBestTimeInSeconds()) {
                user.setTracksMinigameBestTimeInSeconds(newTime);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteBothMinigameScoresById(String spotifyId) throws Exception {
        User user = getUserFromSpotifyId(spotifyId);
        if (user == null) return false;
        user.setArtistsMinigameBestTimeInSeconds(null);
        user.setTracksMinigameBestTimeInSeconds(null);
        userRepository.save(user);
        return true;
    }

    @Override
    public Long getUserArtistMinigameTimeById(String spotifyId) throws Exception {
        User user = getUserFromSpotifyId(spotifyId);
        if (user == null) return null;
        return user.getArtistsMinigameBestTimeInSeconds();
    }

    @Override
    public Long getUserTrackMinigameTimeById(String spotifyId) throws Exception {
        User user = getUserFromSpotifyId(spotifyId);
        if (user == null) return null;
        return user.getTracksMinigameBestTimeInSeconds();
    }

    @Override
    public boolean deleteArtistMinigameScoreById(String spotifyId) throws Exception {
        User user = getUserFromSpotifyId(spotifyId);
        if (user == null) return false;
        user.setArtistsMinigameBestTimeInSeconds(null);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean deleteTrackMinigameScoreById(String spotifyId) throws Exception {
        User user = getUserFromSpotifyId(spotifyId);
        if (user == null) return false;
        user.setTracksMinigameBestTimeInSeconds(null);
        userRepository.save(user);
        return true;
    }

    private User getUserFromSpotifyId(String spotifyId) {
        return userRepository.findBySpotifyId(spotifyId);
    }
}
