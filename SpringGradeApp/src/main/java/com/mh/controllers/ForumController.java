/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.controllers;

import com.mh.pojo.ForumPost;
import com.mh.pojo.ForumReply;
import com.mh.pojo.dto.ForumReplyDTO;
import com.mh.services.ClassroomService;
import com.mh.services.ForumPostService;
import com.mh.services.ForumReplyService;
import com.mh.services.UserService;
import com.mh.utils.ExceptionUtils;
import com.mh.utils.PageSize;
import com.mh.validators.WebAppValidator;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private ForumReplyService forumReplyService;

    @Autowired
    @Qualifier("webAppValidator")
    private WebAppValidator webAppValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(webAppValidator);
    }

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
    public String saveForumPost(@ModelAttribute("forumPost") @Valid ForumPost forumPost, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra");
            return "/forum/forum-post-form";
        }

        this.forumPostService.saveForumPost(forumPost);
        return "redirect:/forums";
    }

    @DeleteMapping("/forums/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteForumPost(@PathVariable("id") int id) {
        this.forumPostService.deleteForumPostById(id);
    }

    // Forum reply
    @GetMapping("/forums/{id}/replies")
    public String getForumReplies(Model model, @PathVariable(value = "id") int id, @RequestParam Map<String, String> params) {
        ForumPost forumPost = this.forumPostService.getForumPostById(id);
        if (forumPost != null) {
            String page = params.get("page");

            if (page == null || page.isEmpty()) {
                params.put("page", "1");
            }

            List<ForumReply> replies = this.forumReplyService.getForumRepliesByForumPostId(id, params);
            model.addAttribute("replies", replies);
            model.addAttribute("forumPost", forumPost);
            model.addAttribute("kw", params.get("kw"));
            model.addAttribute("currentPage", Integer.parseInt(params.get("page")));
            model.addAttribute("totalPages", (int) Math.ceil((double) this.forumReplyService.countForumRepliesByForumPostId(id, params) / PageSize.FORUM_REPLY_PAGE_SIZE.getSize()));
        }
        return "/forum/forum-reply-list";
    }

    @GetMapping("/forums/{postId}/replies/{replyId}/child-replies")
    public ResponseEntity<List<ForumReplyDTO>> getReplies(@PathVariable(value = "postId") int postId, @PathVariable(value = "replyId") int replyId) {
        List<ForumReply> replies = this.forumReplyService.getForumRepliesByForumPostIdAndForumReplyId(postId, replyId);
        List<ForumReplyDTO> repliesDto = replies.stream().map(ForumReplyDTO::new).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(repliesDto);
    }

}
