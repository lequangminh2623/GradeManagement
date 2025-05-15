import axios from "axios";
import cookie from 'react-cookies'

const BASE_URL = 'http://localhost:8080/SpringGradeApp/api/';

export const endpoints = {
    'classrooms': '/secure/classrooms',
    'register': '/users',
    'login': '/login',
    'login-google': '/auth/google',
    'profile': '/secure/profile',
    'classroom-details': (classroomId) => `/secure/classrooms/${classroomId}/grades`,
    'classroom-import': (classroomId) => `/secure/classrooms/${classroomId}/grades/import`,
    'classroom-lock': (classroomId) => `/secure/classrooms/${classroomId}/lock`,
    'export-csv': (classroomId) => `/secure/classrooms/${classroomId}/grades/export/csv`,
    'export-pdf': (classroomId) => `/secure/classrooms/${classroomId}/grades/export/pdf`,
    'users': '/users',
    'student-grades': '/secure/grades/student',
    'ask': '/secure/ai/ask',
    'analysis': (semesterId) => `/secure/ai/analysis/${semesterId}`,
    'forum-posts': (classroomId) => `/secure/classrooms/${classroomId}/forums`,
    'forum-post-detail': (postId) => `/secure/forums/${postId}`,
    'forum-reply': (postId) => `/secure/forums/${postId}/replies`,
    'forum-reply-detail': (postId, replyId) => `/secure/forums/${postId}/replies/${replyId}`,
    'forum-child-replies': (postId, replyId) => `/secure/forums/${postId}/replies/${replyId}/child-replies`,
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