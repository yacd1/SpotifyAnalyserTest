module.exports = {
  __esModule: true,
  apiService: {
    checkSpotifyStatus: jest.fn().mockResolvedValue({
      profile: { display_name: "Mock User", id: "mock_user_id" },
      artistBestTime: 42,
      trackBestTime: 99,
      topPlayers: [{ spotifyUsername: "u1", minigameBestTimeInSeconds: 10 }],
    }),
    getUserArtistMinigameTimeById: jest.fn().mockResolvedValue({ artistMinigameTime: 100 }),
    getUserTrackMinigameTimeById: jest.fn().mockResolvedValue({ trackMinigameTime: 200 }),
    getTopMinigamePlayers: jest.fn().mockResolvedValue([{ spotifyUsername: "u1", minigameBestTimeInSeconds: 10 }]),
    deleteArtistMinigameScoreById: jest.fn(),
    deleteTrackMinigameScoreById: jest.fn(),
    deleteBothMinigameScoresById: jest.fn(),

    registerUser: jest.fn(),
    updateArtistMinigameTimeById: jest.fn().mockResolvedValue({ updated: true }),
    updateTrackMinigameTimeById: jest.fn().mockResolvedValue({ updated: true }),
  }
};