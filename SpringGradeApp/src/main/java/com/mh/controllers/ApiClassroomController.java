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
import com.mh.pojo.dto.GradeDTO;
import com.mh.pojo.dto.TranscriptDTO;
import com.mh.services.ClassroomService;
import com.mh.services.GradeDetailService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

        PrintWriter writer = response.getWriter();

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
        writer.close();
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

}
