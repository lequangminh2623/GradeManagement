export const checkPermission = (objectUserId, userId) => {
    return objectUserId === userId;
};

export const checkCanEdit = (date) => {
    const createdTime = new Date(date);
    const now = new Date();
    const minutesSincePost = (now - createdTime) / 60000;
    return minutesSincePost <= 30;
};

export const formatVietnamTime = (utcDateStr) => {
    const utcDate = new Date(utcDateStr);
    return new Intl.DateTimeFormat('vi-VN', {
        timeZone: 'Asia/Ho_Chi_Minh',
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
    }).format(utcDate);
};