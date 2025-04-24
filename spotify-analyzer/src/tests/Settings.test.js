jest.mock('../services/api');

import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { ThemeProvider } from '../services/Theme';
import Settings from '../components/Settings';
import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';
import { act } from 'react';
import { apiService } from '../services/api';

beforeAll(() => {
    window.matchMedia = window.matchMedia || function () {
        return {
            matches: false,
            addListener: () => {},
            removeListener: () => {},
        };
    };
});

beforeEach(() => {
    apiService.checkSpotifyStatus.mockResolvedValue({
        authenticated: false,
    });
    apiService.getUserArtistMinigameTimeById.mockResolvedValue({ artistMinigameTime: 100 });
    apiService.getUserTrackMinigameTimeById.mockResolvedValue({ trackMinigameTime: 200 });
    apiService.getTopMinigamePlayers.mockResolvedValue([
        { spotifyUsername: 'u1', minigameBestTimeInSeconds: 10 }
    ]);
    apiService.deleteArtistMinigameScoreById.mockResolvedValue({ success: true });
    apiService.deleteTrackMinigameScoreById.mockResolvedValue({ success: true });
    apiService.deleteBothMinigameScoresById.mockResolvedValue({ success: true });
});

describe('Settings Component', () => {
    it('renders the page without errors', () => {
        render(
            <ThemeProvider>
                <Settings />
            </ThemeProvider>
        );
    });

    it('toggles dark mode on and off', async () => {
        render(
            <ThemeProvider>
                <Settings />
            </ThemeProvider>
        );

        const darkModeSwitch = screen.getByRole('checkbox');

        expect(darkModeSwitch).not.toBeChecked();

        await act(async () => {
            userEvent.click(darkModeSwitch);
        });

        expect(darkModeSwitch).toBeChecked();

        await act(async () => {
            userEvent.click(darkModeSwitch);
        });

        expect(darkModeSwitch).not.toBeChecked();
    });

    it('displays user profile and high scores when logged in', async () => {
        const mockProfile = { display_name: 'Test User', id: 'test-user-id' };
        const mockArtistHighScore = 100;
        const mockTrackHighScore = 200;

        apiService.checkSpotifyStatus.mockResolvedValue({ authenticated: true, profile: mockProfile });
        apiService.getUserArtistMinigameTimeById.mockResolvedValue({ artistMinigameTime: mockArtistHighScore });
        apiService.getUserTrackMinigameTimeById.mockResolvedValue({ trackMinigameTime: mockTrackHighScore });

        render(
            <ThemeProvider>
                <Settings />
            </ThemeProvider>
        );

        await waitFor(() => screen.getByText(/Logged in as: Test User/));

        expect(screen.getByText(/Logged in as: Test User/)).toBeInTheDocument();
        expect(screen.getByText(/Artist Game Best Time: 100 seconds/)).toBeInTheDocument();
        expect(screen.getByText(/Track Game Best Time: 200 seconds/)).toBeInTheDocument();
    });

    it('shows login prompt when not logged in', async () => {
        apiService.checkSpotifyStatus.mockResolvedValue({ authenticated: false });

        render(
            <ThemeProvider>
                <Settings />
            </ThemeProvider>
        );

        expect(screen.getByText(/Login with Spotify to manage your minigame scores/)).toBeInTheDocument();
    });

    it('handles artist score deletion process', async () => {
        const mockProfile = { display_name: 'Test User', id: 'test-user-id' };
        const mockArtistHighScore = 100;

        apiService.checkSpotifyStatus.mockResolvedValue({ authenticated: true, profile: mockProfile });
        apiService.getUserArtistMinigameTimeById.mockResolvedValue({ artistMinigameTime: mockArtistHighScore });
        apiService.deleteArtistMinigameScoreById.mockResolvedValue({ success: true });

        render(
            <ThemeProvider>
                <Settings />
            </ThemeProvider>
        );

        await waitFor(() => screen.getByText(/Logged in as: Test User/));

        const deleteButton = screen.getByRole('button', { name: /Reset Artist Score/i });

        await act(async () => {
            userEvent.click(deleteButton);
        });

        expect(apiService.deleteArtistMinigameScoreById).toHaveBeenCalledWith(mockProfile.id);
        expect(screen.getByText(/Artist game high score deleted successfully!/)).toBeInTheDocument();
    });

    it('handles track score deletion process', async () => {
        const mockProfile = { display_name: 'Test User', id: 'test-user-id' };
        const mockTrackHighScore = 200;

        apiService.checkSpotifyStatus.mockResolvedValue({ authenticated: true, profile: mockProfile });
        apiService.getUserTrackMinigameTimeById.mockResolvedValue({ trackMinigameTime: mockTrackHighScore });
        apiService.deleteTrackMinigameScoreById.mockResolvedValue({ success: true });

        render(
            <ThemeProvider>
                <Settings />
            </ThemeProvider>
        );

        await waitFor(() => screen.getByText(/Logged in as: Test User/));

        const deleteButton = screen.getByRole('button', { name: /Reset Track Score/i });

        await act(async () => {
            userEvent.click(deleteButton);
        });

        expect(apiService.deleteTrackMinigameScoreById).toHaveBeenCalledWith(mockProfile.id);
        expect(screen.getByText(/Track game high score deleted successfully!/)).toBeInTheDocument();
    });

    it('handles both artist and track score deletion process', async () => {
        const mockProfile = { display_name: 'Test User', id: 'test-user-id' };
        const mockArtistHighScore = 100;
        const mockTrackHighScore = 200;

        apiService.checkSpotifyStatus.mockResolvedValue({ authenticated: true, profile: mockProfile });
        apiService.getUserArtistMinigameTimeById.mockResolvedValue({ artistMinigameTime: mockArtistHighScore });
        apiService.getUserTrackMinigameTimeById.mockResolvedValue({ trackMinigameTime: mockTrackHighScore });
        apiService.deleteBothMinigameScoresById.mockResolvedValue({ success: true });

        render(
            <ThemeProvider>
                <Settings />
            </ThemeProvider>
        );

        await waitFor(() => screen.getByText(/Logged in as: Test User/));

        const deleteButton = screen.getByRole('button', { name: /Reset All Scores/i });

        await act(async () => {
            userEvent.click(deleteButton);
        });

        expect(apiService.deleteBothMinigameScoresById).toHaveBeenCalledWith(mockProfile.id);
        expect(screen.getByText(/All high scores deleted successfully!/)).toBeInTheDocument();
    });
});