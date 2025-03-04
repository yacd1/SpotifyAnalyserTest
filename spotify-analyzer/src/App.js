import React, {useEffect} from 'react';
import {
    BrowserRouter,
    Routes,
    Route,
    Navigate
} from "react-router-dom";

import './App.css';

import { apiService } from './services/api';

import Layout from './components/Layout';
import Home from './components/Home';
import Minigames from './components/Minigames';
import Settings from './components/Settings';

function App() {
    useEffect(() => {
        apiService.checkStatus();
    }, []);

    return (
        <BrowserRouter>
            <Layout>
                <Routes>
                    <Route path="/" element={<Navigate to="/home" />} />
                    <Route path="/home" element={<Home />} />
                    <Route path="/minigames" element={<Minigames />} />
                    <Route path="/settings" element={<Settings />} />
                </Routes>
            </Layout>
        </BrowserRouter>
    );
}

export default App;
