package com.mh.controllers;

import com.mh.pojo.GradeDetail;
import com.mh.pojo.Semester;
import com.mh.pojo.User;
import com.mh.pojo.dto.SemesterAnalysisResult;
import com.mh.services.ClassroomService;
import com.mh.services.GradeDetailService;
import com.mh.services.SemesterService;
import com.mh.services.UserService;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/secure/ai")
public class AIController {

    @Autowired
    private GradeDetailService gradeDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private SemesterService semesterService;

    @GetMapping("/analysis/{semesterId}")
    public Map<String, Object> clusterStudents(@PathVariable("semesterId") Integer semesterId) {
        if (semesterId == null) {
            semesterId = 1;
        }
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User lecturer = userService.getUserByEmail(email);
        System.out.println(lecturer.getId());

        List<GradeDetail> gradeDetails = gradeDetailService.getGradeDetailsByLecturerAndSemester(lecturer.getId(), semesterId);
        System.out.println(gradeDetails);
        SemesterAnalysisResult result = gradeDetailService.analyzeSemester(gradeDetails);

        List<Semester> allSemesters = semesterService.getSemesters(null);

        Map<String, Object> response = new HashMap<>();
        response.put("analysisResult", result);
        response.put("semesters", allSemesters);

        return response;
    }

    @PostMapping("/ask")
    public String askAI(@RequestBody Map<String, String> payload) throws Exception {
        String userQuery = payload.get("query");
        if (userQuery == null || userQuery.isEmpty()) {
            throw new IllegalArgumentException("Query cannot be null or empty");
        }

        String apiUrl = "http://localhost:11434/api/chat";

        Map<String, Object> requestBody = Map.of(
                "model", "gemma2:2b",
                "messages", List.of(
                        Map.of("role", "user", "content", userQuery)
                ),
                "stream", false
        );

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the HTTP entity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Initialize RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Retry mechanism for handling empty responses
        String content = null;
        int maxRetries = 5; // Maximum number of retries
        int retryInterval = 1000; // Interval between retries in milliseconds

        for (int i = 0; i < maxRetries; i++) {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, requestEntity, Map.class);

            // Extract the chatbot's reply
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("message")) {
                Map<String, Object> message = (Map<String, Object>) responseBody.get("message");
                content = (String) message.get("content");

                // Return the response if it's not empty
                if (content != null && !content.trim().isEmpty()) {
                    return content.trim();
                }
            }

            // Wait before retrying
            Thread.sleep(retryInterval);
        }

        // Fallback response if the content is empty after retries
        return "I'm sorry, I couldn't process your query. Please try again.";
    }
}
