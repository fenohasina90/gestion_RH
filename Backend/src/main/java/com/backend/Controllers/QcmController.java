package com.backend.Controllers;

import com.backend.entities.*;
import com.backend.services.QcmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/qcm")
@CrossOrigin(origins = "*")
public class QcmController {

    @Autowired
    private QcmService qcmService;

    // Get all QCM tests available for a candidate
    @GetMapping("/candidat/{candidatId}")
    public ResponseEntity<List<Qcmtest>> getQcmTestsForCandidate(@PathVariable Integer candidatId) {
        try {
            List<Qcmtest> tests = qcmService.getQcmTestsForCandidate(candidatId);
            return ResponseEntity.ok(tests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get QCM test details with statistics
    @GetMapping("/test/{testId}/candidat/{candidatId}")
    public ResponseEntity<Map<String, Object>> getQcmTestDetails(@PathVariable Integer testId, @PathVariable Integer candidatId) {
        try {
            Optional<Qcmtest> test = qcmService.getQcmTestById(testId);
            if (test.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Long totalQuestions = qcmService.countQuestionsByTestId(testId);
            Integer totalPoints = qcmService.getTotalPointsByTestId(testId);
            Long answeredQuestions = qcmService.countAnsweredQuestions(candidatId, testId);
            Integer currentScore = qcmService.getCandidateScoreForTest(candidatId, testId);
            boolean isCompleted = qcmService.isTestCompletedByCandidate(candidatId, testId);
            boolean hasStarted = qcmService.hasCandidateStartedTest(candidatId, testId);

            Map<String, Object> response = new HashMap<>();
            response.put("test", test.get());
            response.put("totalQuestions", totalQuestions);
            response.put("totalPoints", totalPoints);
            response.put("answeredQuestions", answeredQuestions);
            response.put("currentScore", currentScore);
            response.put("isCompleted", isCompleted);
            response.put("hasStarted", hasStarted);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get next question for candidate
    @GetMapping("/test/{testId}/candidat/{candidatId}/next-question")
    public ResponseEntity<Map<String, Object>> getNextQuestion(@PathVariable Integer testId, @PathVariable Integer candidatId) {
        try {
            Optional<Qcmquestion> nextQuestion = qcmService.getNextQuestionForCandidate(candidatId, testId);
            
            if (nextQuestion.isEmpty()) {
                // Test completed
                Map<String, Object> response = new HashMap<>();
                response.put("completed", true);
                response.put("finalScore", qcmService.getCandidateScoreForTest(candidatId, testId));
                response.put("totalPoints", qcmService.getTotalPointsByTestId(testId));
                return ResponseEntity.ok(response);
            }

            List<Qcmchoix> choices = qcmService.getChoicesByQuestionId(nextQuestion.get().getId());
            Long answeredQuestions = qcmService.countAnsweredQuestions(candidatId, testId);
            Long totalQuestions = qcmService.countQuestionsByTestId(testId);

            Map<String, Object> response = new HashMap<>();
            response.put("question", nextQuestion.get());
            response.put("choices", choices);
            response.put("questionNumber", answeredQuestions + 1);
            response.put("totalQuestions", totalQuestions);
            response.put("completed", false);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Submit answer for a question
    @PostMapping("/answer")
    public ResponseEntity<Map<String, Object>> submitAnswer(@RequestBody Map<String, Integer> answerData) {
        try {
            System.out.println("Received answer data: " + answerData);
            
            Integer candidatId = answerData.get("candidatId");
            Integer testId = answerData.get("testId");
            Integer questionId = answerData.get("questionId");
            Integer choixId = answerData.get("choixId");

            System.out.println("Parsed values - candidatId: " + candidatId + ", testId: " + testId + 
                             ", questionId: " + questionId + ", choixId: " + choixId);

            if (candidatId == null || testId == null || questionId == null || choixId == null) {
                System.out.println("Missing required parameters");
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Paramètres manquants: candidatId, testId, questionId, choixId requis");
                return ResponseEntity.badRequest().body(error);
            }

            Qcmreponse response = qcmService.submitAnswer(candidatId, testId, questionId, choixId);
            
            // Get updated statistics
            Integer currentScore = qcmService.getCandidateScoreForTest(candidatId, testId);
            Long answeredQuestions = qcmService.countAnsweredQuestions(candidatId, testId);
            boolean isCompleted = qcmService.isTestCompletedByCandidate(candidatId, testId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("pointsObtained", response.getPointsobtenus());
            result.put("currentScore", currentScore);
            result.put("answeredQuestions", answeredQuestions);
            result.put("isCompleted", isCompleted);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/admin/results")
    public ResponseEntity<Map<String, Object>> getAllResults() {
        try {
            List<Map<String, Object>> results = qcmService.getAllQcmResults();
            List<Map<String, Object>> tests = qcmService.getAllQcmTests();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("results", results);
            response.put("tests", tests);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get candidate's results for a completed test
    @GetMapping("/test/{testId}/candidat/{candidatId}/results")
    public ResponseEntity<Map<String, Object>> getTestResults(@PathVariable Integer testId, @PathVariable Integer candidatId) {
        try {
            if (!qcmService.isTestCompletedByCandidate(candidatId, testId)) {
                return ResponseEntity.badRequest().build();
            }

            Optional<Qcmtest> test = qcmService.getQcmTestById(testId);
            if (test.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Integer finalScore = qcmService.getCandidateScoreForTest(candidatId, testId);
            Integer totalPoints = qcmService.getTotalPointsByTestId(testId);
            Long totalQuestions = qcmService.countQuestionsByTestId(testId);
            List<Qcmreponse> responses = qcmService.getCandidateResponsesForTest(candidatId, testId);

            double percentage = totalPoints > 0 ? (double) finalScore / totalPoints * 100 : 0;

            Map<String, Object> results = new HashMap<>();
            results.put("test", test.get());
            results.put("finalScore", finalScore);
            results.put("totalPoints", totalPoints);
            results.put("totalQuestions", totalQuestions);
            results.put("percentage", Math.round(percentage * 100.0) / 100.0);
            results.put("responses", responses);

            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get all questions for a test (for admin purposes)
    @GetMapping("/test/{testId}/questions")
    public ResponseEntity<List<Qcmquestion>> getTestQuestions(@PathVariable Integer testId) {
        try {
            List<Qcmquestion> questions = qcmService.getQuestionsByTestId(testId);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get choices for a specific question
    @GetMapping("/question/{questionId}/choices")
    public ResponseEntity<List<Qcmchoix>> getQuestionChoices(@PathVariable Integer questionId) {
        try {
            List<Qcmchoix> choices = qcmService.getChoicesByQuestionId(questionId);
            return ResponseEntity.ok(choices);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Admin: create a full QCM (test + questions + choices) with DB-only fields
    @PostMapping("/admin/create")
    public ResponseEntity<?> createQcm(@RequestBody Map<String, Object> payload) {
        try {
            String nom = String.valueOf(payload.get("nom"));
            Object profilIdObj = payload.get("profilId");
            Integer profilId = profilIdObj == null ? null : Integer.parseInt(String.valueOf(profilIdObj));
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> questions = (List<Map<String, Object>>) payload.get("questions");

            Qcmtest created = qcmService.createFullTest(nom, profilId, questions);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("id", created.getId());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }
}
