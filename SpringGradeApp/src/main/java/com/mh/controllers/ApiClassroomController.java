package com.mh.controllers;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
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
import com.mh.validators.WebAppValidator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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

    private void checkLecturerPermission(Integer classroomId) {
        Classroom classroom = classroomService.getClassroomById(classroomId);

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String lecturerEmail = classroom.getLecturer().getEmail();
        if (!lecturerEmail.equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không phải giảng viên phụ trách lớp này.");
        }
    }

    private void checkExportPermission(Integer classroomId) {
        Classroom classroom = classroomService.getClassroomById(classroomId);

        if (!"LOCKED".equalsIgnoreCase(classroom.getGradeStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bảng điểm chưa khóa, không thể xuất file.");
        }

        checkLecturerPermission(classroomId);
    }

    @GetMapping("/{classroomId}/grades")
    public ResponseEntity<TranscriptDTO> getGradeSheetForClassroom(@PathVariable("classroomId") Integer classroomId) {
        checkLecturerPermission(classroomId);

        TranscriptDTO gradeSheet = gradeDetailService.getTranscriptForClassroom(classroomId);
        return ResponseEntity.ok(gradeSheet);
    }

    @PatchMapping("/{classroomId}/lock")
    public ResponseEntity<?> lockTranscript(@PathVariable("classroomId") Integer classroomId) {
        checkLecturerPermission(classroomId);

        classroomService.lockClassroomGrades(classroomId);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType("text/plain; charset=UTF-8"))
                .body("Điểm của lớp " + classroomId + " khóa thành công!");
    }

    @PostMapping("/{classroomId}/grades")
    public ResponseEntity<String> saveGrades(
            @PathVariable("classroomId") Integer classroomId,
            @RequestBody List<GradeDTO> gradeRequests) {
        checkLecturerPermission(classroomId);
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
    public ResponseEntity<String> uploadCsv(
            @PathVariable("classroomId") Integer classroomId,
            @RequestParam("file") MultipartFile file) {
        checkLecturerPermission(classroomId);

        try {
            gradeDetailService.uploadGradesFromCsv(classroomId, file);
            return ResponseEntity.ok("Lưu điểm thành công!");
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
    public void exportGradesToCsv(
            @PathVariable("classroomId") Integer classroomId,
            HttpServletResponse response) throws IOException {

        checkExportPermission(classroomId);

        response.setContentType("text/csv");
        String filename = "grades_classroom_" + classroomId + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<GradeDTO> gradeList = gradeDetailService.getGradesByClassroom(classroomId);
        int maxExtra = gradeList.stream()
                .mapToInt(g -> g.getExtraGrades() != null ? g.getExtraGrades().size() : 0)
                .max().orElse(0);

        try (PrintWriter writer = response.getWriter()) {
            writer.print("studentId,midtermGrade,finalGrade");
            for (int i = 1; i <= maxExtra; i++) {
                writer.print(",extra" + i);
            }
            writer.println();
            
            for (GradeDTO grade : gradeList) {
                writer.print(grade.getStudentId() + ","
                        + grade.getMidtermGrade() + ","
                        + grade.getFinalGrade());
                
                List<Double> extras = grade.getExtraGrades();
                if (extras == null) {
                    extras = new ArrayList<>();
                }
                for (int i = 0; i < maxExtra; i++) {
                    if (i < extras.size()) {
                        writer.print("," + extras.get(i));
                    } else {
                        writer.print(",");
                    }
                }
                writer.println();
            }

            writer.flush();
        }
    }

    @GetMapping("/{classroomId}/grades/export/pdf")
    public void exportGradesToPdf(
            @PathVariable("classroomId") Integer classroomId,
            HttpServletResponse response) throws IOException {

        checkExportPermission(classroomId);

        response.setContentType("application/pdf");
        String filename = "grades_classroom_" + classroomId + ".pdf";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<GradeDTO> gradeList = gradeDetailService.getGradesByClassroom(classroomId);
        int maxExtra = gradeList.stream()
                .mapToInt(g -> g.getExtraGrades() != null ? g.getExtraGrades().size() : 0)
                .max().orElse(0);

        Classroom classroom = classroomService.getClassroomById(classroomId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        try (Document document = new Document(pdfDoc)) {
            PdfFont font = PdfFontFactory.createFont("static/fonts/times.ttf", PdfEncodings.IDENTITY_H);
            document.setFont(font);

            document.add(new Paragraph("BẢNG ĐIỂM")
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18));

            document.add(new Paragraph("\n"));

            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
            infoTable.setMarginBottom(10);

            infoTable.addCell(new Cell().add(new Paragraph("Môn: " + classroom.getCourse().getName()).setFontSize(12)).setBorder(Border.NO_BORDER));
            infoTable.addCell(new Cell().add(new Paragraph("Lớp: " + classroom.getName()).setFontSize(12)).setBorder(Border.NO_BORDER));

            String lecturerName = classroom.getLecturer().getLastName() + " " + classroom.getLecturer().getFirstName();
            infoTable.addCell(new Cell().add(new Paragraph("Giảng viên: " + lecturerName).setFontSize(12)).setBorder(Border.NO_BORDER));
            infoTable.addCell(new Cell().add(new Paragraph("Học kỳ: " + classroom.getSemester().getSemesterType()
                    + " " + classroom.getSemester().getAcademicYear().getYear()).setFontSize(12)).setBorder(Border.NO_BORDER));

            document.add(infoTable);

            List<Float> columnWidths = new ArrayList<>();
            columnWidths.add(30f); // STT
            columnWidths.add(150f); // Họ và tên
            for (int i = 0; i < maxExtra; i++) {
                columnWidths.add(60f); // Extra points
            }
            columnWidths.add(60f); // Midterm
            columnWidths.add(60f); // Final

            Table table = new Table(columnWidths.stream().map(UnitValue::createPointValue).toArray(UnitValue[]::new));
            table.setWidth(UnitValue.createPercentValue(100));

            // Header
            table.addHeaderCell(new Cell().add(new Paragraph("STT").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Họ và tên").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            for (int i = 1; i <= maxExtra; i++) {
                table.addHeaderCell(new Cell().add(new Paragraph("Bổ sung " + i).setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            }
            table.addHeaderCell(new Cell().add(new Paragraph("Giữa kỳ").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Cuối kỳ").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Nội dung bảng
            int stt = 1;
            for (GradeDTO grade : gradeList) {
                table.addCell(new Paragraph(String.valueOf(stt++)));
                table.addCell(new Paragraph(grade.getFullName() != null ? grade.getFullName() : ""));

                List<Double> extras = grade.getExtraGrades() != null ? grade.getExtraGrades() : new ArrayList<>();
                for (int i = 0; i < maxExtra; i++) {
                    String extraValue = (i < extras.size() && extras.get(i) != null) ? extras.get(i).toString() : "";
                    table.addCell(new Paragraph(extraValue));
                }

                table.addCell(new Paragraph(grade.getMidtermGrade() != null ? grade.getMidtermGrade().toString() : ""));
                table.addCell(new Paragraph(grade.getFinalGrade() != null ? grade.getFinalGrade().toString() : ""));
            }

            document.add(table);
        }

        response.getOutputStream().write(baos.toByteArray());
        response.getOutputStream().flush();
    }

    @GetMapping("")
    public ResponseEntity<List<ClassroomDTO>> getClassrooms(@RequestParam Map<String, String> params) {
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

        return ResponseEntity.ok(classroomsDto);
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

        return ResponseEntity.ok(forumPosts);
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
