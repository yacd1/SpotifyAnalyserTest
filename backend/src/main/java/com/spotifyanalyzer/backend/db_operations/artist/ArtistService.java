package com.spotifyanalyzer.backend.db_operations.artist;
import java.util.List;

public interface ArtistService
{
    public Artist addArtist(Artist artist) throws Exception;
    public List<Artist> getRegisteredArtists() throws Exception;
    public String fetchArtistSummary(String artistName) throws Exception;
    public Artist getArtistByName(String artistName) throws Exception;
}

