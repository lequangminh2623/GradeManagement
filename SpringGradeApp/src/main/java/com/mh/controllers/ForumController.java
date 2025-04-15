/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.controllers;

import com.mh.pojo.Classroom;
import com.mh.pojo.ForumPost;
import com.mh.pojo.Student;
import com.mh.services.ClassroomService;
import com.mh.services.ForumPostService;
import com.mh.services.UserService;
import com.mh.utils.ExceptionUtils;
import com.mh.utils.PageSize;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author leoma
 */
@Controller
public class ForumController {
    
    @Autowired
    private ForumPostService forumPostService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ClassroomService classroomService;
    
    @ModelAttribute
    public void commonResponse(Model model) {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_LECTURER");
        roles.add("ROLE_STUDENT");
        
        model.addAttribute("users", this.userService.getUserByRole(roles));
        model.addAttribute("classrooms", this.classroomService.getClassrooms(null));
    }
    
    @GetMapping("/forums")
    public String getAcademicYears(Model model, @RequestParam Map<String, String> params) {
        String page = params.get("page");
        
        if (page == null || page.isEmpty()) {
            params.put("page", "1");
        }
        
        List<ForumPost> forumPosts = this.forumPostService.getForumPosts(params);
        model.addAttribute("forumPosts", forumPosts);
        model.addAttribute("currentPage", Integer.parseInt(params.get("page")));
        model.addAttribute("totalPages", (int) Math.ceil((double) this.forumPostService.countForumPosts(params) / PageSize.FORUM_POST_PAGE_SIZE.getSize()));
        model.addAttribute("kw", params.get("kw"));
        
        return "/forum/forum-post-list";
    }
    
    @GetMapping("/forums/add")
    public String addForumPost(Model model) {
        ForumPost forumPost = new ForumPost();
        model.addAttribute("forumPost", forumPost);
        return "/forum/forum-post-form";
    }
    
    @GetMapping("/forums/{id}")
    public String updateForumPost(Model model, @PathVariable(value = "id") int id) {
        ForumPost forumPost = this.forumPostService.getForumPostById(id);
        model.addAttribute("forumPost", forumPost);
        return "/forum/forum-post-form";
    }
    
    @PostMapping("/forums")
    public String saveForumPost(@ModelAttribute("forumPost") ForumPost forumPost) {
        this.forumPostService.saveForumPost(forumPost);
        return "redirect:/forums";
    }
    
    @DeleteMapping("/forums/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") int id) {
        this.forumPostService.deleteForumPostById(id);
    }
    
}
