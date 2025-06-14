package com.mh.controllers;

import com.mh.pojo.Classroom;
import com.mh.pojo.ForumPost;
import com.mh.pojo.User;
import com.mh.pojo.dto.ClassroomDTO;
import com.mh.pojo.dto.ForumPostDTO;
import com.mh.pojo.dto.GradeDTO;
import com.mh.pojo.dto.TranscriptDTO;
import com.mh.services.ClassroomService;
import com.mh.services.ForumPostService;
import com.mh.services.GradeDetailService;
import com.mh.services.UserService;
import com.mh.utils.PageSize;
import com.mh.validators.WebAppValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/secure/classrooms")
@CrossOrigin
public class ApiClassroomController {

    @Autowired
    private GradeDetailService gradeDetailService;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private UserService userService;

    @Autowired
    private ForumPostService forumPostService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    @Qualifier("webAppValidator")
    private WebAppValidator webAppValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(webAppValidator);
    }

    @GetMapping("/{classroomId}/grades")
    public ResponseEntity<?> getGradeSheetForClassroom(@PathVariable("classroomId") Integer classroomId, @RequestParam Map<String, String> params) {
        if (!classroomService.checkLecturerPermission(classroomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không phải giảng viên phụ trách lớp này.");
        }
        TranscriptDTO gradeSheet;
        try {
            gradeSheet = gradeDetailService.getTranscriptForClassroom(classroomId, params);
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Lỗi: " + e.getMessage());
        }

        return ResponseEntity.ok(gradeSheet);
    }

    @PatchMapping("/{classroomId}/lock")
    public ResponseEntity<?> lockTranscript(@PathVariable("classroomId") Integer classroomId) {
        if (!classroomService.checkLecturerPermission(classroomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không phải giảng viên phụ trách lớp này.");
        }
        if (classroomService.isLockedClassroom(classroomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bảng điểm đã được khóa trước đó.");
        }
        if (!classroomService.lockClassroomGrades(classroomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Chưa nhập đủ điểm cho tất cả sinh viên.");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                .body("Điểm của lớp " + classroomId + " khóa thành công!");
    }

    @PostMapping("/{classroomId}/grades")
    public ResponseEntity<String> saveGrades(
            @PathVariable("classroomId") Integer classroomId,
            @RequestBody @Valid List<GradeDTO> gradeRequests,
            BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Lỗi: " + "Điểm phải nằm trong khoảng từ 0 đến 10");
        }
        if (!classroomService.checkLecturerPermission(classroomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không phải giảng viên phụ trách lớp này.");
        }
        if (classroomService.isLockedClassroom(classroomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bảng điểm đã khóa.");
        }
        try {
            gradeDetailService.updateGradesForClassroom(classroomId, gradeRequests);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Lỗi: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                .body("Lưu điểm thành công!");
    }

    @PostMapping("/{classroomId}/grades/import")
    public ResponseEntity<String> importCsv(
            @PathVariable("classroomId") Integer classroomId,
            @RequestParam("file") MultipartFile file) {
        if (!classroomService.checkLecturerPermission(classroomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không phải giảng viên phụ trách lớp này.");
        }
        if (classroomService.isLockedClassroom(classroomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bảng điểm đã khóa.");
        }

        try {
            gradeDetailService.uploadGradesFromCsv(classroomId, file);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Lưu điểm thành công!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Lỗi: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/{classroomId}/grades/export/csv")
    public ResponseEntity<String> exportGradesToCsv(
            @PathVariable("classroomId") Integer classroomId,
            HttpServletResponse response) throws IOException {

        if (!classroomService.checkLecturerPermission(classroomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không phải giảng viên phụ trách lớp này.");
        }
        if (!classroomService.isLockedClassroom(classroomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bảng điểm chưa khóa.");
        }
        try {
            classroomService.exportGradesToCsv(classroomId, response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Lỗi: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{classroomId}/grades/export/pdf")
    public ResponseEntity<String> exportGradesToPdf(
            @PathVariable("classroomId") Integer classroomId,
            HttpServletResponse response) throws IOException {

        if (!classroomService.checkLecturerPermission(classroomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không phải giảng viên phụ trách lớp này.");
        }
        if (!classroomService.isLockedClassroom(classroomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bảng điểm chưa khóa.");
        }
        try {
            classroomService.exportGradesToPdf(classroomId, response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Lỗi: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("")
    public ResponseEntity<?> getClassrooms(@RequestParam Map<String, String> params) {
        User user = this.userService.getCurrentUser();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String page = params.get("page");

        if (page == null || page.isEmpty()) {
            params.put("page", "1");
        }

        List<Classroom> classrooms = classroomService.getClassroomsByUser(user, params);
        List<ClassroomDTO> classroomsDto = classrooms.stream().map(ClassroomDTO::new).collect(Collectors.toList());
        int totalPages = (int) Math.ceil((double) this.classroomService.countClassroomsByUser(user, params)
                / PageSize.CLASSROOM_PAGE_SIZE.getSize());
        return ResponseEntity.ok(Map.of("content", classroomsDto,
                "totalPages", totalPages
        ));
    }

    @GetMapping("/{classroomId}/forums")
    public ResponseEntity<?> getForumsPosts(@RequestParam Map<String, String> params, @PathVariable(value = "classroomId") int classroomId) {
        User user = this.userService.getCurrentUser();

        boolean check = this.forumPostService.checkForumPostPermission(user.getId(), classroomId);
        if (!check) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không có quyền truy cập");
        }

        String page = params.get("page");

        if (page == null || page.isEmpty()) {
            params.put("page", "1");
        }

        params.put("classroom", String.valueOf(classroomId));

        List<ForumPost> forumPosts = this.forumPostService.getForumPosts(params);
        int totalPages = (int) Math.ceil((double) this.forumPostService.countForumPosts(params)
                / PageSize.FORUM_POST_PAGE_SIZE.getSize());

        return ResponseEntity.ok(Map.of("content", forumPosts,
                "totalPages", totalPages
        ));
    }

    @PostMapping("/{classroomId}/forums")
    public ResponseEntity<?> addForumsPost(@ModelAttribute @Valid ForumPostDTO forumPostDTO, BindingResult result,
            @PathVariable(value = "classroomId") int classroomId) {
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

        boolean check = this.forumPostService.checkForumPostPermission(user.getId(), classroomId);
        if (!check) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                    .body("Bạn không có quyền truy cập");
        }

        ForumPost forumPost = new ForumPost();
        forumPost.setTitle(forumPostDTO.getTitle());
        forumPost.setContent(forumPostDTO.getContent());
        forumPost.setFile(forumPostDTO.getFile());
        forumPost.setUser(user);
        forumPost.setClassroom(this.classroomService.getClassroomById(classroomId));

        return ResponseEntity.status(HttpStatus.CREATED).body(this.forumPostService.saveForumPost(forumPost));
    }

}
