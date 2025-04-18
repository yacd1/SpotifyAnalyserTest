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
    public User registerUser(User user) throws Exception {
        if(user == null) {
            throw new Exception("User is null");
        }
        List<User> existingUsers = userRepository.findAllBySpotifyUsername(user.getSpotifyUsername());

        if (!existingUsers.isEmpty()) {
            User existingUser = existingUsers.get(0);
            if (existingUsers.size() > 1) {
                System.out.println("Found " + existingUsers.size() + " duplicate users with username: " +
                        user.getSpotifyUsername() + ". Using first one and removing others.");

                for (int i = 1; i < existingUsers.size(); i++) {
                    userRepository.delete(existingUsers.get(i));
                }
            }

            existingUser.setSongsMinigameBestTimeInSeconds(null);
            existingUser.setArtistsMinigameBestTimeInSeconds(null);
            return userRepository.save(existingUser);
        } else {
            return userRepository.save(user);
        }
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
    public boolean deleteMinigameScore(String username) throws Exception {
        List<User> users = userRepository.findAllBySpotifyUsername(username);
        if (users.isEmpty()) {
            return false;
        }
        for (User user : users) {
            userRepository.delete(user);
        }
        return true;
    }

    @Override
    public boolean updateMinigameTime(String username, long newTime, String typeOfGame) throws Exception {
        List<User> users = userRepository.findAllBySpotifyUsername(username);
        if (users.isEmpty()) {
            return false;
        }
        User user = users.get(0);
        if (typeOfGame.equals("artists")) {
            if (user.getArtistsMinigameBestTimeInSeconds() == null || newTime < user.getArtistsMinigameBestTimeInSeconds()) {
                user.setArtistsMinigameBestTimeInSeconds(newTime);
                userRepository.save(user);
            }
        } else if (typeOfGame.equals("songs")) {
            if (user.getSongsMinigameBestTimeInSeconds() == null || newTime < user.getSongsMinigameBestTimeInSeconds()) {
                user.setSongsMinigameBestTimeInSeconds(newTime);
                userRepository.save(user);
            }
        }
        return false;
    }
}
