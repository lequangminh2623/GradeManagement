/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.controllers;

import com.mh.pojo.Student;
import com.mh.pojo.User;
import com.mh.services.UserService;
import com.mh.utils.PageSize;
import com.mh.validators.WebAppValidator;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author Le Quang Minh
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("webAppValidator")
    private WebAppValidator webAppValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(webAppValidator);
    }

    @GetMapping("/login")
    public String loginView() {
        return "login";
    }

    @GetMapping("/users")
    public String listUsers(Model model, @RequestParam Map<String, String> params) {
        String page = params.get("page");

        if (page == null || page.isEmpty()) {
            params.put("page", "1");
        }

        List<User> users = this.userService.getUsers(params);
        model.addAttribute("users", users);
        model.addAttribute("currentPage", Integer.valueOf(params.get("page")));
        model.addAttribute("totalPages", (int) Math.ceil((double) this.userService.countUser(params) / PageSize.USER_PAGE_SIZE.getSize()));
        model.addAttribute("kw", params.get("kw"));
        return "/user/user-list";
    }

    @GetMapping("/users/add")
    public String addUser(Model model) {
        User user = new User();
        user.setStudent(new Student());
        model.addAttribute("user", user);
        return "/user/user-form";
    }

    @PostMapping("/users")
    public String saveUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        if ("ROLE_STUDENT".equals(user.getRole())) {
            Student s = user.getStudent();
            s.setUser(user);
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra");
            model.addAttribute("user", user);
            return "/user/user-form";
        }

        if (!"ROLE_STUDENT".equals(user.getRole())) {
            user.setStudent(null);
        }

        userService.saveUser(user);

        model.addAttribute("user", user);
        return "redirect:/users";
    }

    @GetMapping("/users/{id}")
    public String updateUser(@PathVariable("id") Integer id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);

        return "/user/user-form";
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUser(id);
    }
}
