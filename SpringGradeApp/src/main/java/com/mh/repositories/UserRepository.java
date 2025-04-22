/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mh.repositories;

import com.mh.pojo.User;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Le Quang Minh
 */
public interface UserRepository {

    User getUserByEmail(String email);

    boolean authenticate(String email, String password);

    List<User> getUsers(Map<String, String> params);

    User getUserById(Integer id);

    void deleteUser(Integer id);

    User saveUser(User user);

    int countUser(Map<String, String> params);

    List<User> getUserByRole(List<String> roles);
    
    boolean existsByEmail(String email, Integer excludeId);
}
