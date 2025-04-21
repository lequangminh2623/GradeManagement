/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.mh.utils;

/**
 *
 * @author leoma
 */
public enum PageSize {
    COURSE_PAGE_SIZE(10),
    YEAR_PAGE_SIZE(5),
    USER_PAGE_SIZE(10),
    CLASSROOM_PAGE_SIZE(10),
    FORUM_POST_PAGE_SIZE(10),
    FORUM_REPLY_PAGE_SIZE(10);

    private final int size;

    PageSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
