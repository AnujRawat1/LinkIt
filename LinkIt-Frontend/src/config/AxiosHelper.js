import axios from 'axios';

export const baseURL_PROD = 'https://linkit-latest.onrender.com'; 
export const baseURL_DEV = ' http://localhost:8080';  

export const httpClient = axios.create({
    baseURL: baseURL_DEV,
});