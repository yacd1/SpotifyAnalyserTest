package com.spotifyanalyzer.backend.db_operations.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/db/users")
public class UserDatabaseController
{
    @Autowired
    private UserService userService;


    //Registers the users
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws Exception
    {
        if(user!=null)
        {
            userService.registerUser(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Get all the registered users.
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>>getRegisteredUser() throws Exception
    {
        List<User>users=userService.getRegisteredUsers();
        if(users!=null)
        {
            return new ResponseEntity<>(users,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/topMinigamePlayers")
    public ResponseEntity<List<User>> getTopMinigamePlayers() throws Exception
    {
        List<User> users = userService.getTopMinigamePlayers();
        if (users != null)
        {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //update user minigame time
    @PutMapping("/updateArtistMinigameTime")
    public ResponseEntity<?> updateArtistMinigameTime(@RequestParam String username, @RequestParam long newTime) {
        try {
            boolean wasUpdated = userService.updateMinigameTime(username, newTime, "artists");

            return getResponseMessage(wasUpdated);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    Map.of("error", "Error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PutMapping("/updateSongMinigameTime")
    public ResponseEntity<?> updateSongMinigameTime(@RequestParam String username, @RequestParam long newTime) {
        try {
            boolean wasUpdated = userService.updateMinigameTime(username, newTime, "songs");

            return getResponseMessage(wasUpdated);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    Map.of("error", "Error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private static ResponseEntity<Map<String, String>> getResponseMessage(boolean wasUpdated) {
        if (wasUpdated) {
            return new ResponseEntity<>(
                    Map.of("message", "Minigame time updated successfully"),
                    HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(
                    Map.of("message", "Couldn't find user or minigame time not updated"),
                    HttpStatus.OK
            );
        }
    }

    @DeleteMapping("/deleteMinigameScore")
    public ResponseEntity<?> deleteMinigameScore(@RequestParam String username) {
        try {
            boolean deleted = userService.deleteMinigameScore(username);

            if (deleted) {
                return new ResponseEntity<>(
                        Map.of("message", "Minigame score deleted successfully"),
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(
                        Map.of("message", "No score found to delete"),
                        HttpStatus.OK
                );
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                    Map.of("error", "Error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}


