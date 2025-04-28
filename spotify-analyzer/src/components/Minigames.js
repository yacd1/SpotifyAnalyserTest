import React, { useState, useEffect, useContext, useCallback } from 'react';
import {
    fetchTopArtists,
    searchArtists,
    fetchTopTracks,
    searchTracks
} from '../services/minigameHandler';
import { apiService } from '../services/api';
import { Theme } from '../services/Theme';

import '../styles/App.css';

function Minigames() {
    const { isDarkMode } = useContext(Theme);

    const [mode, setMode] = useState("artists");
    const [artists, setArtists] = useState([]);
    const [tracks, setTracks] = useState([]);
    const [guess, setGuess] = useState("");
    const [suggestion, setSuggestion] = useState(null);
    const [suggestionClicked, setSuggestionClicked] = useState(false);

    const [userProfile, setUserProfile] = useState(null);
    const [artistBestTime, setArtistBestTime] = useState(null);
    const [trackBestTime, setTrackBestTime] = useState(null);
    const [topPlayers, setTopPlayers] = useState([]);

    const [startTime, setStartTime] = useState(null);
    const [elapsedTime, setElapsedTime] = useState(0);
    const [timerActive, setTimerActive] = useState(false);
    const [gameComplete, setGameComplete] = useState(false);
    const [scoreMessage, setScoreMessage] = useState("");

    const [showLeaderboard, setShowLeaderboard] = useState(false);

    const showLeaderboardHandler = async () => {
        try {
            const leaderboard = await apiService.getTopMinigamePlayers();
            console.log("Full leaderboard response:", JSON.stringify(leaderboard, null, 2));

            if (leaderboard && leaderboard.length > 0) {
                console.log("First player in leaderboard:", leaderboard[0]);
                console.log("Available properties:", Object.keys(leaderboard[0]));
            } else {
                console.log("Leaderboard is empty or null");
            }

            setTopPlayers(leaderboard || []);
        } catch (err) {
            console.error("Error fetching leaderboard:", err);
        }
        setShowLeaderboard(true);
    };

    useEffect(() => {
        const init = async () => {
            try {
                const status = await apiService.checkSpotifyStatus();

                if (status.authenticated && status.profile) {
                    const artistTimeData = await apiService.getUserArtistMinigameTimeById(status.profile.id);
                    if (artistTimeData?.artistMinigameTime !== undefined) {
                        setArtistBestTime(artistTimeData.artistMinigameTime);
                    } else {
                        setArtistBestTime(false);
                    }

                    const trackTimeData = await apiService.getUserTrackMinigameTimeById(status.profile.id);
                    if (trackTimeData?.trackMinigameTime !== undefined) {
                        setTrackBestTime(trackTimeData.trackMinigameTime);
                    } else {
                        setTrackBestTime(false);
                    }

                    const leaderboard = await apiService.getTopMinigamePlayers();
                    setTopPlayers(leaderboard.filter(player => player.artistsMinigameBestTimeInSeconds !== null));
                }
            } catch (err) {
                console.error("Init error:", err);
            }
        };

        init();
    }, []);

    useEffect(() => {
        const fetchUserProfile = async () => {
            try {
                const status = await apiService.checkSpotifyStatus();
                if (status.authenticated && status.profile) {
                    setUserProfile(status.profile);
                }
            } catch (error) {
                console.error("error fetching user's profile:", error);
            }
        };

        fetchUserProfile();
    }, []);

    useEffect(() => {
        const loadData = async () => {
            setStartTime(null);
            setElapsedTime(0);
            setTimerActive(false);
            setGameComplete(false);
            setScoreMessage("");
            setGuess("");
            setSuggestion(null);

            if (mode === "artists") {
                const result = await fetchTopArtists();
                setArtists(result.map(name => ({ name, hidden: true })));
            } else {
                const result = await fetchTopTracks();
                setTracks(result.map(name => ({ name, hidden: true })));
            }
        };

        loadData();
    }, [mode]);

    useEffect(() => {
        if (!timerActive || !startTime) return;

        const interval = setInterval(() => {
            setElapsedTime(Math.floor((Date.now() - startTime) / 1000));
        }, 1000);

        return () => clearInterval(interval);
    }, [timerActive, startTime]);

    const saveHighScore = useCallback(async (gameType) => {
        if (!userProfile || !userProfile.id) {
            setScoreMessage("Login to save your score!");
            return;
        }

        try {
            let response;
            if (gameType === "artists") {
                response = await apiService.updateArtistMinigameTimeById(userProfile.id, elapsedTime);
                if (response.updated) {
                    setScoreMessage(`New high score: ${elapsedTime}s!`);
                    setArtistBestTime(elapsedTime);
                } else {
                    const time = await apiService.getUserArtistMinigameTimeById(userProfile.id);
                    setArtistBestTime(time?.artistMinigameTime ?? false);
                    setScoreMessage(`Your best artist score: ${time?.artistMinigameTime}s`);
                }
            } else {
                response = await apiService.updateTrackMinigameTimeById(userProfile.id, elapsedTime);
                if (response.updated) {
                    setScoreMessage(`New high score: ${elapsedTime}s!`);
                    setTrackBestTime(elapsedTime);
                } else {
                    const time = await apiService.getUserTrackMinigameTimeById(userProfile.id);
                    setTrackBestTime(time?.trackMinigameTime ?? false);
                    setScoreMessage(`Your best track score: ${time?.trackMinigameTime}s`);
                }
            }
        } catch (error) {
            console.error("error saving high score:", error);
            setScoreMessage("Error saving your score. Try again later.");
        }
    }, [userProfile, elapsedTime, setScoreMessage, setArtistBestTime, setTrackBestTime]);

    useEffect(() => {
        const items = mode === "artists" ? artists : tracks;
        const allGuessed = items.length === 10 && items.every(item => !item.hidden);

        if (allGuessed) {
            setTimerActive(false);
            setGameComplete(true);
            localStorage.setItem('gameCompleted', 'true');

            if (userProfile && userProfile.id) {
                saveHighScore(mode);
            }
        }
    }, [artists, tracks, mode, timerActive, userProfile, saveHighScore]);

    const handleGuess = () => {
        if (!guess.trim()) return;

        if (!timerActive && !startTime) {
            setStartTime(Date.now());
            setTimerActive(true);
        }

        const updateList = (listSetter) => {
            listSetter(prev =>
                prev.map(item =>
                    item.name.toLowerCase() === guess.toLowerCase()
                        ? { ...item, hidden: false }
                        : item
                )
            );
        };

        if (mode === "artists") updateList(setArtists);
        if (mode === "tracks") updateList(setTracks);

        setGuess("");
        setSuggestion(null);
    };

    const handleKeyDown = (e) => {
        if (e.key === "Enter") handleGuess();
    };

    useEffect(() => {
        let cancelled = false;

        const autoComplete = async () => {
            if (guess.trim() === "" || suggestionClicked) {
                setSuggestion(null);
                return;
            }

            const results = mode === "artists"
                ? await searchArtists(guess.toLowerCase(), "3")
                : await searchTracks(guess.toLowerCase(), "3");

            if (!cancelled) setSuggestion(results);
        };

        autoComplete();

        return () => {
            cancelled = true;
        };
    }, [guess, mode, suggestionClicked]);

    const handleSuggestionClick = (item) => {
        setGuess(item);
        setSuggestionClicked(true);
        setTimeout(() => setSuggestionClicked(false), 100);
    };

    const itemsToDisplay = mode === "artists" ? artists : tracks;

    return (
        <div className={`Minigames ${isDarkMode ? 'dark' : 'light'}`}>
            <div className="modeToggle">
                <button onClick={() => setMode("artists")} className={mode === "artists" ? "active" : ""}>
                    Artists
                </button>
                <button onClick={() => setMode("tracks")} className={mode === "tracks" ? "active" : ""}>
                    Tracks
                </button>
            </div>

            <h1>Guess {mode === "artists" ? "Artists" : "Tracks"} Rankings</h1>
            <p>Can you guess your recent top ten {mode === "artists" ? "artists" : "tracks"}?</p>

            <div className="timer">
                {startTime ? <p>Time: {elapsedTime}s</p> : <p>Guess to begin!</p>}
                {gameComplete && <p className="completion-message">You guessed them all!</p>}
            </div>

            <ol className="artistList">
                <div className="leftColumn">
                    {itemsToDisplay.slice(0, 5).map((item, i) => (
                        <li key={i}>
                            <span className="number">{i + 1}</span>
                            <button className={`artistPill ${item.hidden ? "hidden" : "shown"}`}>
                                {item.name}
                            </button>
                        </li>
                    ))}
                </div>

                <div className="rightColumn">
                    {itemsToDisplay.slice(5, 10).map((item, i) => (
                        <li key={i + 5}>
                            <span className="number">{i + 6}</span>
                            <button className={`artistPill ${item.hidden ? "hidden" : "shown"}`}>
                                {item.name}
                            </button>
                        </li>
                    ))}
                </div>
            </ol>

            <div className="guessSection">
                <input
                    type="text"
                    placeholder={`Enter ${mode === "artists" ? "artist" : "track"} name`}
                    value={guess}
                    onChange={(e) => setGuess(e.target.value)}
                    onKeyDown={handleKeyDown}
                    className="inputField"
                />
                <button onClick={handleGuess} className="guessButton">Guess</button>

                {suggestion && (
                    <ul className="suggestionsList">
                        {suggestion.map((item, index) => (
                            <li
                                key={index}
                                onClick={() => handleSuggestionClick(item)}
                                className="suggestionItem"
                            >
                                {item}
                            </li>
                        ))}
                    </ul>
                )}
            </div>

            <div className="scoreSection">
                {scoreMessage && <p className="scoreMessage">{scoreMessage}</p>}
                <button className="leaderboardToggleBtn" onClick={showLeaderboardHandler}>
                    Show Leaderboard
                </button>
            </div>

            {showLeaderboard && (
                <div className="leaderboardOverlay" onClick={() => setShowLeaderboard(false)}>
                    <div className="leaderboardContent" onClick={(e) => e.stopPropagation()}>
                        <button className="closeLeaderboard" onClick={() => setShowLeaderboard(false)}>✖</button>
                        <h3>Top Players</h3>
                        <ol>
                            {topPlayers.length > 0 ? (
                                topPlayers.map((player, index) => (
                                    <li key={index}>
                                        {player.spotifyUsername}: {player.artistsMinigameBestTimeInSeconds}s
                                    </li>
                                ))
                            ) : (
                                <p>No scores available yet</p>
                            )}
                        </ol>

                        {userProfile && (
                            <div className="scoreInfo">
                                <h3>Your Best Times:</h3>
                                <p><strong>Artists:</strong> {artistBestTime !== false ? `${artistBestTime}s` : "No score yet"}</p>
                                <p><strong>Tracks:</strong> {trackBestTime !== false ? `${trackBestTime}s` : "No score yet"}</p>
                            </div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}

export default Minigames;