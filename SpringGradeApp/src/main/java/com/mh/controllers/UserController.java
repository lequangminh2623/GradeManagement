/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.controllers;

import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author Le Quang Minh
 */
public class UserController {
    @GetMapping("/login")
    public String loginView() {
        return "login";
    }
}
