/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;

import com.mh.pojo.User;
import com.mh.repositories.UserRepository;

/**
 *
 * @author Le Quang Minh
 */
public class UserServiceImpl {
    private UserRepository userRepo;
    
    public User getUserByUsername(String username) {
        return this.userRepo.getUserByUsername(username);
    }
}
