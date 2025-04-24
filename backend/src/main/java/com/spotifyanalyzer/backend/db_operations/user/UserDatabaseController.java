package com.spotifyanalyzer.backend.db_operations.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/db/users")
public class UserDatabaseController
{
    @Autowired
    private UserService userService;

    private static ResponseEntity<Map<String, Boolean>> getResponseMessage(boolean wasUpdated) {
        return new ResponseEntity<>(
                Map.of("updated", wasUpdated),
                HttpStatus.OK
        );
    }

    //Registers the users
    @PutMapping("/register")
    public ResponseEntity<?> register(@RequestParam String username, @RequestParam String spotifyId) throws Exception
    {
        if(username!=null && spotifyId!=null)
        {
            userService.registerUser(username, spotifyId);
        }
        return ResponseEntity.ok(Collections.singletonMap("message", "User registered"));
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

    @GetMapping("/getUserArtistMinigameTimeById")
    public ResponseEntity<?> getUserArtistMinigameTimeById(@RequestParam String spotifyId) {
        try {
            Long time = userService.getUserArtistMinigameTimeById(spotifyId);
            if (time == null) {
                return new ResponseEntity<>(
                        Map.of("artistMinigameTime", false),
                        HttpStatus.OK
                );
            }
            return new ResponseEntity<>(
                    Map.of("artistMinigameTime", time),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    Map.of("error", "Error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/getUserTrackMinigameTimeById")
    public ResponseEntity<?> getUserTrackMinigameTimeById(@RequestParam String spotifyId) {
        try {
            Long time = userService.getUserTrackMinigameTimeById(spotifyId);
            if (time == null) {
                return new ResponseEntity<>(
                        Map.of("trackMinigameTime", false),
                        HttpStatus.OK
                );
            }
            return new ResponseEntity<>(
                    Map.of("trackMinigameTime", time),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    Map.of("error", "Error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PutMapping("/updateArtistMinigameTimeById")
    public ResponseEntity<?> updateArtistMinigameTimeById(@RequestParam String spotifyId, @RequestParam long newTime) {
        try {
            boolean wasUpdated = userService.updateMinigameTimeById(spotifyId, newTime, "artists");
            return getResponseMessage(wasUpdated);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    Map.of("error", "Error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PutMapping("/updateTrackMinigameTimeById")
    public ResponseEntity<?> updateTrackMinigameTimeById(@RequestParam String spotifyId, @RequestParam long newTime) {
        try {
            boolean wasUpdated = userService.updateMinigameTimeById(spotifyId, newTime, "tracks");
            return getResponseMessage(wasUpdated);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    Map.of("error", "Error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @DeleteMapping("/deleteBothMinigameScoresById")
    public ResponseEntity<?> deleteMinigameScoreById(@RequestParam String spotifyId) {
        try {
            boolean deleted = userService.deleteBothMinigameScoresById(spotifyId);
            if (deleted) {
                return new ResponseEntity<>(
                        Map.of("message", "Minigame scores deleted successfully"),
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(
                        Map.of("message", "Could not delete"),
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

    @DeleteMapping("/deleteArtistMinigameScoreById")
    public ResponseEntity<?> deleteArtistMinigameScoreById(@RequestParam String spotifyId) {
        try {
            boolean deleted = userService.deleteArtistMinigameScoreById(spotifyId);
            if (deleted) {
                return new ResponseEntity<>(
                        Map.of("message", "Artist minigame score deleted successfully"),
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(
                        Map.of("message", "No artist score found to delete"),
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

    @DeleteMapping("/deleteTrackMinigameScoreById")
    public ResponseEntity<?> deleteTrackMinigameScoreById(@RequestParam String spotifyId) {
        try {
            boolean deleted = userService.deleteTrackMinigameScoreById(spotifyId);
            if (deleted) {
                return new ResponseEntity<>(
                        Map.of("message", "Track minigame score deleted successfully"),
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(
                        Map.of("message", "No Track score found to delete"),
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


