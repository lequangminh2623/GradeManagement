import axios from "axios";
import cookie from 'react-cookies'

const BASE_URL = 'http://localhost:8080/SpringGradeApp/api/';

export const endpoints = {
    'classrooms': '/secure/classrooms',
    'register': '/users',
    'login': '/login',
    'profile': '/secure/profile',
}

export const authApis = () => {
    return axios.create({
        baseURL: BASE_URL,
        headers: {
            'Authorization': `Bearer ${cookie.load('token')}`
        }
    })
}

export default axios.create({
    baseURL: BASE_URL
});