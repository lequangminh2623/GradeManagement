package com.mh.controllers;

import com.mh.pojo.ForumPost;
import com.mh.pojo.ForumReply;
import com.mh.services.ForumPostService;
import com.mh.services.ForumReplyService;
import com.mh.services.UserService;
import com.mh.utils.PageSize;
import com.mh.validators.WebAppValidator;
import jakarta.validation.Valid;
import java.util.ArrayList;
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

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author leoma
 */
@Controller
public class ForumReplyController {

    @Autowired
    private ForumReplyService forumReplyService;

    @Autowired
    private UserService userService;

    @Autowired
    private ForumPostService forumPostService;

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
        model.addAttribute("forumPosts", this.forumPostService.getForumPosts(null));
    }

    @GetMapping("/replies")
    public String getReplies(Model model, @RequestParam Map<String, String> params) {
        String page = params.get("page");

        if (page == null || page.isEmpty()) {
            params.put("page", "1");
        }

        List<ForumReply> replies = this.forumReplyService.getAllForumReplys(params);
        model.addAttribute("replies", replies);
        model.addAttribute("currentPage", Integer.parseInt(params.get("page")));
        model.addAttribute("totalPages", (int) Math.ceil((double) this.forumReplyService.countForumReplies(params) / PageSize.FORUM_REPLY_PAGE_SIZE.getSize()));
        model.addAttribute("kw", params.get("kw"));

        return "/forum/reply-list";
    }

    @GetMapping("/replies/add")
    public String addReply(Model model) {
        ForumReply reply = new ForumReply();
        List<ForumReply> replies = this.forumReplyService.getAllForumReplys(null);
        model.addAttribute("reply", reply);
        model.addAttribute("replies", replies);

        return "/forum/reply-form";
    }

    @GetMapping("/replies/{id}")
    public String updateReply(Model model, @PathVariable(value = "id") int id) {
        ForumReply reply = this.forumReplyService.getForumReplyById(id);
        List<ForumReply> replies = this.forumReplyService.getAllForumReplys(null);
        model.addAttribute("reply", reply);
        model.addAttribute("replies", replies);

        return "/forum/reply-form";
    }

    @PostMapping("/replies")
    public String saveReply(@ModelAttribute("reply") @Valid ForumReply reply, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<ForumReply> replies = this.forumReplyService.getAllForumReplys(null);
            model.addAttribute("replies", replies);
            model.addAttribute("errorMessage", "Có lỗi xảy ra");
            return "/forum/reply-form";
        }

        this.forumReplyService.saveForumReply(reply);
        return "redirect:/replies";
    }

    @DeleteMapping("/replies/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReply(@PathVariable("id") int id) {
        this.forumReplyService.deleteReplyById(id);
    }
}
