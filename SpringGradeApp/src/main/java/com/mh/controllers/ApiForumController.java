package com.mh.controllers;

import com.mh.pojo.Classroom;
import com.mh.pojo.ForumPost;
import com.mh.pojo.ForumReply;
import com.mh.pojo.User;
import com.mh.pojo.dto.ForumPostDTO;
import com.mh.pojo.dto.ForumPostDetailDTO;
import com.mh.pojo.dto.ForumReplyDTO;
import com.mh.services.ClassroomService;
import com.mh.services.ForumPostService;
import com.mh.services.ForumReplyService;
import com.mh.services.UserService;
import com.mh.utils.PageSize;
import com.mh.validators.WebAppValidator;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author leoma
 */
@RestController
@RequestMapping("/api/secure/forums")
@CrossOrigin
public class ApiForumController {

    @Autowired
    private UserService userService;

    @Autowired
    private ForumPostService forumPostService;

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

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/{forumPostId}")
    public ResponseEntity<?> getDetailForumsPost(@PathVariable(value = "forumPostId") int forumPostId, @RequestParam Map<String, String> params) {
        User user = this.userService.getCurrentUser();

        boolean check = forumPostService.checkForumPostPermission(user.getId(),
                this.classroomService.getClassroomByForumPostId(forumPostId).getId());
        if (!check) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không có quyền truy cập");
        }

        String page = params.get("page");

        if (page == null || page.isEmpty()) {
            params.put("page", "1");
        }

        ForumPost forumPost = this.forumPostService.getForumPostById(forumPostId);
        List<ForumReply> replies = this.forumReplyService.getForumRepliesByForumPostId(forumPostId, params);
        int totalPages = (int) Math.ceil((double) this.forumReplyService.countForumRepliesByForumPostId(forumPostId, params)
                / PageSize.FORUM_REPLY_PAGE_SIZE.getSize());

        forumPost.setForumReplySet(new LinkedHashSet<>(replies));

        return ResponseEntity.ok(Map.of("content", new ForumPostDetailDTO(forumPost),
                "totalPages", totalPages
        ));
    }

    @PatchMapping("/{forumPostId}")
    public ResponseEntity<?> updateForumsPost(@ModelAttribute @Valid ForumPostDTO forumPostDTO, BindingResult result,
            @PathVariable(value = "forumPostId") int forumPostId) {
        if (result.hasErrors()) {
            List<Map<String, String>> errors = result.getFieldErrors().stream()
                    .map(error -> {
                        Map<String, String> err = new HashMap<>();
                        err.put("field", error.getField());
                        err.put("message", error.getDefaultMessage() != null ? error.getDefaultMessage()
                                : messageSource.getMessage(error.getCode(), null, Locale.ITALY));
                        return err;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(errors);
        }

        User user = this.userService.getCurrentUser();
        Classroom classroom = this.classroomService.getClassroomByForumPostId(forumPostId);

        if (!this.forumPostService.checkForumPostPermission(user.getId(), classroom.getId())
                || !this.forumPostService.checkOwnerForumPostPermission(user.getId(), forumPostId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không có quyền truy cập");
        }

        if (!this.forumPostService.isPostStillEditable(forumPostId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không thể chỉnh sửa được nữa");
        }

        ForumPost forumPost = this.forumPostService.getForumPostById(forumPostId);
        forumPost.setTitle(forumPostDTO.getTitle());
        forumPost.setContent(forumPostDTO.getContent());
        forumPost.setFile(forumPostDTO.getFile());

        return ResponseEntity.ok(this.forumPostService.saveForumPost(forumPost));
    }

    @DeleteMapping("/{forumPostId}")
    public ResponseEntity<?> deleteForumsPost(@PathVariable(value = "forumPostId") int forumPostId) {
        User user = this.userService.getCurrentUser();
        Classroom classroom = this.classroomService.getClassroomByForumPostId(forumPostId);

        if (!this.forumPostService.checkForumPostPermission(user.getId(), classroom.getId())
                || !this.forumPostService.checkOwnerForumPostPermission(user.getId(), forumPostId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không có quyền truy cập");
        }

        if (!this.forumPostService.isPostStillEditable(forumPostId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không thể chỉnh sửa được nữa");
        }

        this.forumPostService.deleteForumPostById(forumPostId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{forumPostId}/replies/{replyId}/child-replies")
    public ResponseEntity<?> getReplies(@PathVariable(value = "forumPostId") int forumPostId, @PathVariable(value = "replyId") int replyId,
            @RequestParam Map<String, String> params) {
        User user = this.userService.getCurrentUser();

        boolean check = forumPostService.checkForumPostPermission(user.getId(),
                this.classroomService.getClassroomByForumPostId(forumPostId).getId());
        if (!check) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không có quyền truy cập");
        }

        String page = params.get("page");

        if (page == null || page.isEmpty()) {
            params.put("page", "1");
        }

        List<ForumReply> replies = this.forumReplyService.getForumRepliesByForumPostIdAndForumReplyId(forumPostId, replyId, params);
        int totalPages = (int) Math.ceil((double) this.forumReplyService.countForumRepliesByForumPostIdAndForumReplyId(forumPostId, replyId, params)
                / PageSize.FORUM_REPLY_PAGE_SIZE.getSize());

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("content", replies,
                "totalPages", totalPages
        ));
    }

    @PostMapping("/{forumPostId}/replies")
    public ResponseEntity<?> addReplies(@ModelAttribute @Valid ForumReplyDTO forumReplyDTO, BindingResult result,
            @PathVariable(value = "forumPostId") int forumPostId) {
        if (result.hasErrors()) {
            List<Map<String, String>> errors = result.getFieldErrors().stream()
                    .map(error -> {
                        Map<String, String> err = new HashMap<>();
                        err.put("field", error.getField());
                        err.put("message", error.getDefaultMessage() != null ? error.getDefaultMessage()
                                : messageSource.getMessage(error.getCode(), null, Locale.ITALY));
                        return err;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(errors);
        }

        User user = this.userService.getCurrentUser();

        boolean check = forumPostService.checkForumPostPermission(user.getId(),
                this.classroomService.getClassroomByForumPostId(forumPostId).getId());
        if (!check) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không có quyền truy cập");
        }

        ForumReply reply = new ForumReply();
        reply.setContent(forumReplyDTO.getContent());
        reply.setFile(forumReplyDTO.getFile());
        reply.setForumPost(this.forumPostService.getForumPostById(forumPostId));
        reply.setParent(this.forumReplyService.getForumReplyById(forumReplyDTO.getParentId()));
        reply.setUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.forumReplyService.saveForumReply(reply));
    }

    @PatchMapping("/{forumPostId}/replies/{replyId}")
    public ResponseEntity<?> updateForumsReply(@ModelAttribute @Valid ForumReplyDTO forumReplyDTO, BindingResult result,
            @PathVariable(value = "forumPostId") int forumPostId, @PathVariable(value = "replyId") int replyId) {
        if (result.hasErrors()) {
            List<Map<String, String>> errors = result.getFieldErrors().stream()
                    .map(error -> {
                        Map<String, String> err = new HashMap<>();
                        err.put("field", error.getField());
                        err.put("message", error.getDefaultMessage() != null ? error.getDefaultMessage()
                                : messageSource.getMessage(error.getCode(), null, Locale.ITALY));
                        return err;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(errors);
        }

        User user = this.userService.getCurrentUser();
        Classroom classroom = this.classroomService.getClassroomByForumPostId(forumPostId);

        if (!this.forumPostService.checkForumPostPermission(user.getId(), classroom.getId())
                || !this.forumReplyService.checkOwnerForumReplyPermission(user.getId(), replyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không có quyền truy cập");
        }

        if (!this.forumReplyService.isReplyStillEditable(replyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không thể chỉnh sửa được nữa");
        }

        ForumReply reply = this.forumReplyService.getForumReplyById(replyId);
        reply.setContent(forumReplyDTO.getContent());
        reply.setFile(forumReplyDTO.getFile());

        return ResponseEntity.ok(this.forumReplyService.saveForumReply(reply));
    }

    @DeleteMapping("/{forumPostId}/replies/{replyId}")
    public ResponseEntity<?> deleteForumsReply(@PathVariable(value = "forumPostId") int forumPostId, @PathVariable(value = "replyId") int replyId) {
        User user = this.userService.getCurrentUser();
        Classroom classroom = this.classroomService.getClassroomByForumPostId(forumPostId);

        if (!this.forumPostService.checkForumPostPermission(user.getId(), classroom.getId())
                || !this.forumReplyService.checkOwnerForumReplyPermission(user.getId(), replyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không có quyền truy cập");
        }

        if (!this.forumReplyService.isReplyStillEditable(replyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không thể chỉnh sửa được nữa");
        }

        this.forumReplyService.deleteReplyById(replyId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
