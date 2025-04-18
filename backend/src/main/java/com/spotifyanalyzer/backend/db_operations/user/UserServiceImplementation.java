package com.spotifyanalyzer.backend.db_operations.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImplementation implements UserService
{
    @Autowired
    private UserRepository userRepository;

    @Override
    public void registerUser(String username) throws Exception {
        // Check if the user already exists
        User existingUser = userRepository.findBySpotifyUsername(username);
        if (existingUser != null) {
            // User already exists, no need to register again
            return;
        }
        User user = new User();
        user.setSpotifyUsername(username);
        user.setArtistsMinigameBestTimeInSeconds(null);
        user.setSongsMinigameBestTimeInSeconds(null);
        userRepository.save(user);
    }

    @Override
    public List<User> getRegisteredUsers() throws Exception
    {
        List<User>users=userRepository.findAll();
        if(users!=null)
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
    public boolean deleteBothMinigameScores(String username) throws Exception {
        List<User> users = userRepository.findAllBySpotifyUsername(username);
        if (users.isEmpty()) {
            return false;
        }
        User user = users.get(0);
        user.setArtistsMinigameBestTimeInSeconds(null);
        user.setSongsMinigameBestTimeInSeconds(null);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean updateMinigameTime(String username, long newTime, String typeOfGame) throws Exception {
        List<User> users = userRepository.findAllBySpotifyUsername(username);
        System.out.println("Update minigame time for user: " + username);
        if (users.isEmpty()) {
            return false;
        }
        User user = users.get(0);
        if (typeOfGame.equals("artists")) {
            if (user.getArtistsMinigameBestTimeInSeconds() == null || newTime < user.getArtistsMinigameBestTimeInSeconds()) {
                user.setArtistsMinigameBestTimeInSeconds(newTime);
                System.out.println(user.getArtistsMinigameBestTimeInSeconds());
                System.out.println(user.getSpotifyUsername());
                userRepository.save(user);
                return true;
            }
        } else if (typeOfGame.equals("songs")) {
            if (user.getSongsMinigameBestTimeInSeconds() == null || newTime < user.getSongsMinigameBestTimeInSeconds()) {
                user.setSongsMinigameBestTimeInSeconds(newTime);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public Long getUserArtistMinigameTime(String username) throws Exception {
        List<User> users = userRepository.findAllBySpotifyUsername(username);
        if (users.isEmpty()) {
            return null;
        }
        User user = users.get(0);
        return user.getArtistsMinigameBestTimeInSeconds();
        
    }

    @Override
    public Long getUserSongMinigameTime(String username) throws Exception {
        List<User> users = userRepository.findAllBySpotifyUsername(username);
        if (users.isEmpty()) {
            return null;
        }
        User user = users.get(0);
        System.out.println(user.getSongsMinigameBestTimeInSeconds());
        return user.getSongsMinigameBestTimeInSeconds();
    }

    @Override
    public boolean deleteArtistMinigameScore(String username) throws Exception {
        List<User> users = userRepository.findAllBySpotifyUsername(username);
        if (users.isEmpty()) {
            return false;
        }
        User user = users.get(0);
        user.setArtistsMinigameBestTimeInSeconds(null);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean deleteSongMinigameScore(String username) throws Exception {
        List<User> users = userRepository.findAllBySpotifyUsername(username);
        if (users.isEmpty()) {
            return false;
        }
        User user = users.get(0);
        user.setSongsMinigameBestTimeInSeconds(null);
        userRepository.save(user);
        return true;
    }
}
