/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mh.pojo.User;
import com.mh.repositories.UserRepository;
import com.mh.services.UserService;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Le Quang Minh
 */
@Service("userDetailsService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public User getUserByEmail(String email) {
        return this.userRepo.getUserByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = this.getUserByEmail(email);
        if (u == null) {
            throw new UsernameNotFoundException("Invalid email!");
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(u.getRole()));

        return new org.springframework.security.core.userdetails.User(
                u.getEmail(), u.getPassword(), authorities);
    }

    @Override
    public boolean authenticate(String email, String password) {
        return this.userRepo.authenticate(email, password);
    }

    @Override
    public User saveUser(User user) {
        if (user.getPassword() == null) {
            user.setPassword(this.passwordEncoder.encode("1"));
        } else {
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        }
        if (user.getCreatedDate() == null) {
            user.setCreatedDate(new Date());
        }
        if (user.getRole() == null) {
            user.setRole("ROLE_STUDENT");
        }
        if (user.getFile() != null && !user.getFile().isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(user.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto", "folder", "GradeManagement"));
                user.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        user.setUpdatedDate(new Date());
        return userRepo.saveUser(user);
    }

    @Override
    public User getUserById(Integer id) {
        return userRepo.getUserById(id);
    }

    @Override
    public void deleteUser(Integer id) {
        userRepo.deleteUser(id);
    }

    @Override
    public List<User> getUsers(Map<String, String> params) {
        return userRepo.getUsers(params);
    }

    @Override
    public int countUser(Map<String, String> params) {
        return this.userRepo.countUser(params);
    }

    @Override
    public List<User> getUserByRole(List<String> roles) {
        return this.userRepo.getUserByRole(roles);
    }

    @Override
    public boolean existsByEmail(String email, Integer excludeId) {
        return this.userRepo.existsByEmail(email, excludeId);
    }

    @Override
    public User getCurrentUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = this.userRepo.getUserByEmail(email);
        return user;
    }

}
