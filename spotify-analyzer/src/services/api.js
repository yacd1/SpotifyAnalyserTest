import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

export const apiService = {
    checkStatus: async () => {
        try {
            const response = await axios.get(`${API_BASE_URL}/status`);
            console.log('API connection status:', response.data.status, 
                        '\nServer time:', response.data.timestamp);
        } catch (error) {
            console.error('Error checking API status:', error);
            throw error;
        }
    }
};