package com.spotifyanalyzer.backend.db_operations.artist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/db/artists")
public class ArtistDatabaseController
{
    @Autowired
    private ArtistService artistService;

    //update artist summary
    @PutMapping("/fetchArtistSummary")
    public ResponseEntity<Map<String,String>> fetchArtistSummary(@RequestParam String artistName)
    {
        try
        {
            if (artistName == null)
            {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            else {
                String summary = artistService.fetchArtistSummary(artistName);
                return new ResponseEntity<>(Map.of("ArtistSummary", summary), HttpStatus.OK);
            }
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}


