/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mh.services;

import com.mh.pojo.User;

/**
 *
 * @author Le Quang Minh
 */
public interface UserService {
    User getUserByUsername(String username);
}
