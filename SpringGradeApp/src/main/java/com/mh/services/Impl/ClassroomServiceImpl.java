/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mh.services.Impl;

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
import com.mh.pojo.ExtraGrade;
import com.mh.pojo.GradeDetail;
import com.mh.pojo.Student;
import com.mh.pojo.User;
import com.mh.pojo.dto.GradeDTO;
import com.mh.repositories.ClassroomRepository;
import com.mh.services.ClassroomService;
import com.mh.services.GradeDetailService;
import com.mh.utils.MailUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Le Quang Minh
 */
@Service
public class ClassroomServiceImpl implements ClassroomService {

    @Autowired
    private ClassroomRepository classroomRepo;

    @Autowired
    private GradeDetailService gradeDetailService;

    @Autowired
    private MailUtils mailUtils;

    @Override
    public Classroom saveClassroom(Classroom classroom) {
        return this.classroomRepo.saveClassroom(classroom);
    }

    @Override
    public Classroom getClassroomById(Integer id) {
        return this.classroomRepo.getClassroomById(id);
    }

    @Override
    public void deleteClassroom(Integer id) {
        if (!classroomRepo.getClassroomWithStudents(id).getStudentSet().isEmpty()) {
            throw new IllegalStateException("Cannot delete classroom with enrolled students.");
        }
        this.classroomRepo.deleteClassroom(id);
    }

    @Override
    public List<Classroom> getClassrooms(Map<String, String> params) {
        return this.classroomRepo.getClassrooms(params);
    }

    @Override
    public Classroom getClassroomWithStudents(Integer id) {
        return this.classroomRepo.getClassroomWithStudents(id);
    }

    @Override
    public int countClassroom(Map<String, String> params) {
        return this.classroomRepo.countClassroom(params);
    }

    @Override
    public void removeStudentFromClassroom(int classroomId, int studentId) {
        this.classroomRepo.removeStudentFromClassroom(classroomId, studentId);
    }

    @Override
    public boolean existsDuplicateClassroom(String name, Integer semesterId, Integer courseId, Integer excludeId) {
        return this.classroomRepo.existsDuplicateClassroom(name, semesterId, courseId, excludeId);
    }

    @Override
    public boolean existsStudentInOtherClassroom(int studentId, int semesterId, int courseId, Integer excludeClassroomId) {
        return this.classroomRepo.existsStudentInOtherClassroom(
                studentId, semesterId, courseId, excludeClassroomId
        );
    }

    @Override
    public boolean existUserInClassroom(int userId, int classRoomId) {
        return this.classroomRepo.existUserInClassroom(userId, classRoomId);
    }

    @Override
    public boolean lockClassroomGrades(Integer classroomId) {
        Classroom classroom = this.getClassroomWithStudents(classroomId);
        Set<Student> students = classroom.getStudentSet();

        for (Student student : students) {
            Map<String, Integer> params = new HashMap<>();
            params.put("classroomId", classroomId);
            params.put("studentId", student.getId());
            List<GradeDetail> gradeDetails = gradeDetailService.getGradeDetail(params);
            if (gradeDetails == null) {
                return true;
            }
            GradeDetail gradeDetail = gradeDetails.get(0);
            if (gradeDetail == null
                    || gradeDetail.getMidtermGrade() == null
                    || gradeDetail.getFinalGrade() == null) {
                return false;
            }

            if (gradeDetail.getExtraGradeSet() != null) {
                for (ExtraGrade extra : gradeDetail.getExtraGradeSet()) {
                    if (extra.getGrade() == null) {
                        return false;
                    }
                }
            }
        }

        classroom.setGradeStatus("LOCKED");
        this.saveClassroom(classroom);

        // Gửi email thông báo bất đồng bộ cho từng sinh viên
        for (Student student : students) {
            User user = student.getUser();
            if (user != null && user.getEmail() != null) {
                String subject = "Thông báo khóa điểm lớp " + classroom.getName();
                String body = String.format("Chào %s %s,\n\nBảng điểm lớp %s đã được công bố.\nVui lòng kiểm tra điểm của bạn trên hệ thống.",
                        user.getLastName(), user.getFirstName(), classroom.getName());
                mailUtils.sendEmailAsync(user.getEmail(), subject, body);
            }
        }
        return true;
    }

    @Override
    public List<Classroom> getClassroomsByUser(User user, Map<String, String> params) {
        return this.classroomRepo.getClassroomsByUser(user, params);
    }

    @Override
    public Classroom getClassroomByForumPostId(int id) {
        return this.classroomRepo.getClassroomByForumPostId(id);
    }

    public void exportGradesToCsv(Integer classroomId, HttpServletResponse response) throws IOException {
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

    @Override
    public void exportGradesToPdf(Integer classroomId, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        String filename = "grades_classroom_" + classroomId + ".pdf";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        List<GradeDTO> gradeList = gradeDetailService.getGradesByClassroom(classroomId);
        int maxExtra = gradeList.stream()
                .mapToInt(g -> g.getExtraGrades() != null ? g.getExtraGrades().size() : 0)
                .max().orElse(0);

        Classroom classroom = this.getClassroomById(classroomId);

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

    @Override
    public boolean checkLecturerPermission(Integer classroomId) {
        Classroom classroom = this.getClassroomById(classroomId);

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String lecturerEmail = classroom.getLecturer().getEmail();
        if (!lecturerEmail.equalsIgnoreCase(email)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isLockedClassroom(Integer classroomId) {
        Classroom classroom = this.getClassroomById(classroomId);

        if ("LOCKED".equalsIgnoreCase(classroom.getGradeStatus())) {
            return true;
        }
        return false;
    }

    @Override
    public List<Student> getStudentsInClassroom(Integer classroomId, Map<String, String> params) {
        return this.classroomRepo.getStudentsInClassroom(classroomId, params);
    }

    @Override
    public int countClassroomsByUser(User user, Map<String, String> params) {
        return this.classroomRepo.countClassroomsByUser(user, params);
    }

}
