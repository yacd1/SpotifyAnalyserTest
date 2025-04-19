import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { apiService } from '../services/api';
import '../styles/Home.css';

const Login = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const location = useLocation();
    const isProcessingCodeRef = useRef(false);
    const hasRedirectedRef = useRef(false); // 💡 new: prevent flicker by locking redirects

    useEffect(() => {
        const checkAuthCode = async () => {
            console.log("Login mounted at:", location.pathname);

            if (hasRedirectedRef.current) return;

            try {
                const authStatus = await apiService.checkSpotifyStatus();
                if (authStatus.authenticated) {
                    hasRedirectedRef.current = true;
                    navigate('/home');
                    return;
                }
            } catch (err) {
                // Continue to next step
            }

            if (location.pathname === '/callback') {
                const params = new URLSearchParams(window.location.search);
                const code = params.get('code');

                // Clean up URL
                window.history.replaceState({}, document.title, '/login');

                if (code && !isProcessingCodeRef.current) {
                    isProcessingCodeRef.current = true;
                    setLoading(true);

                    try {
                        const data = await apiService.exchangeCodeForToken(code);

                        if (data && data.access_token) {
                            sessionStorage.setItem('spotify_access_token', data.access_token);

                            // Wait to allow backend to process
                            setTimeout(async () => {
                                try {
                                    const status = await apiService.checkSpotifyStatus();

                                    if (status.authenticated) {
                                        hasRedirectedRef.current = true;
                                        navigate('/home');
                                    } else {
                                        setError("Authentication failed. Please try again.");
                                    }
                                } catch (statusErr) {
                                    setError("Authentication failed. Please try again.");
                                } finally {
                                    setLoading(false);
                                }
                            }, 700); // wait slightly longer
                        } else {
                            throw new Error("No access token received");
                        }
                    } catch (err) {
                        console.error("Authentication error:", err);
                        setError("Failed to authenticate with Spotify. Please try again.");
                        setLoading(false);
                        isProcessingCodeRef.current = false;
                    }
                } else {
                    setLoading(false);
                }
            } else {
                setLoading(false);
            }
        };

        checkAuthCode();
    }, [navigate, location.pathname]);

    const handleLogin = async () => {
        try {
            setLoading(true);
            setError(null);

            const data = await apiService.getSpotifyAuthUrl();

            window.location.href = data.authUrl;
        } catch (err) {
            setError("Failed to connect to Spotify. Please try again.");
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="loading-container">
                {location.pathname === '/callback'
                    ? "Processing Spotify authorisation..."
                    : "Loading..."}
            </div>
        );
    }

    return (
        <div className="login-container">
            <h1>Spotify Analyzer</h1>
            <p>Connect with Spotify to see your top artists</p>

            {error && <div className="error-message">{error}</div>}

            <button
                className="spotify-login-btn"
                onClick={handleLogin}
                disabled={loading}
            >
                Login with Spotify
            </button>
        </div>
    );
};

export default Login;
