package com.backend.services;

import com.backend.entities.*;
import com.backend.repositories.*;
import com.backend.repositories.ProfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class QcmService {

    @Autowired
    private QcmtestRepository qcmtestRepository;

    @Autowired
    private QcmquestionRepository qcmquestionRepository;

    @Autowired
    private QcmchoixRepository qcmchoixRepository;

    @Autowired
    private QcmreponseRepository qcmreponseRepository;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private StatutcandidatRepository statutcandidatRepository;

    @Autowired
    private ProfilRepository profilRepository;

    // Get all QCM tests available for a candidate based on their applications
    public List<Qcmtest> getQcmTestsForCandidate(Integer candidatId) {
        return qcmtestRepository.findQcmTestsByCandidatApplications(candidatId);
    }

    // Get a specific QCM test by ID
    public Optional<Qcmtest> getQcmTestById(Integer testId) {
        return qcmtestRepository.findById(testId);
    }

    // Get all questions for a specific test
    public List<Qcmquestion> getQuestionsByTestId(Integer testId) {
        return qcmquestionRepository.findByIdtest_IdOrderByNumero(testId);
    }

    // Get a specific question by ID
    public Optional<Qcmquestion> getQuestionById(Integer questionId) {
        return qcmquestionRepository.findById(questionId);
    }

    // Get all choices for a specific question
    public List<Qcmchoix> getChoicesByQuestionId(Integer questionId) {
        return qcmchoixRepository.findByIdquestion_Id(questionId);
    }

    // Get correct choices for a specific question
    public List<Qcmchoix> getCorrectChoicesByQuestionId(Integer questionId) {
        return qcmchoixRepository.findByIdquestion_IdAndEstcorrectTrue(questionId);
    }

    // Count total questions in a test
    public Long countQuestionsByTestId(Integer testId) {
        return qcmquestionRepository.countQuestionsByTestId(testId);
    }

    // Get total points for a test
    public Integer getTotalPointsByTestId(Integer testId) {
        return qcmquestionRepository.getTotalPointsByTestId(testId);
    }

    // Submit an answer for a question
    public Qcmreponse submitAnswer(Integer candidatId, Integer testId, Integer questionId, Integer choixId) {
        Optional<Candidat> candidat = candidatRepository.findById(candidatId);
        Optional<Qcmtest> test = qcmtestRepository.findById(testId);
        Optional<Qcmquestion> question = qcmquestionRepository.findById(questionId);
        Optional<Qcmchoix> choix = qcmchoixRepository.findById(choixId);

        if (candidat.isEmpty() || test.isEmpty() || question.isEmpty() || choix.isEmpty()) {
            throw new RuntimeException("Invalid data provided for answer submission");
        }

        // Check if answer already exists for this question
        Optional<Qcmreponse> existingResponse = qcmreponseRepository.findByIdcandidat_IdAndIdquestion_Id(candidatId, questionId);
        
        Qcmreponse response;
        if (existingResponse.isPresent()) {
            // Update existing response
            response = existingResponse.get();
        } else {
            // Create new response
            response = new Qcmreponse();
            response.setIdcandidat(candidat.get());
            response.setIdtest(test.get());
            response.setIdquestion(question.get());
        }

        response.setIdchoix(choix.get());
        
        // Calculate points based on whether the choice is correct
        int pointsObtained = choix.get().getEstcorrect() ? question.get().getPoints() : 0;
        response.setPointsobtenus(pointsObtained);

        Qcmreponse savedResponse = qcmreponseRepository.save(response);
        
        // Check if this was the last question of the test and update candidate status if needed
        checkAndUpdateCandidateStatus(candidatId, testId);
        
        return savedResponse;
    }

    // Get candidate's responses for a specific test
    public List<Qcmreponse> getCandidateResponsesForTest(Integer candidatId, Integer testId) {
        return qcmreponseRepository.findByIdcandidat_IdAndIdtest_Id(candidatId, testId);
    }

    // Get candidate's total score for a test
    public Integer getCandidateScoreForTest(Integer candidatId, Integer testId) {
        Integer score = qcmreponseRepository.getTotalScoreByCandidatAndTest(candidatId, testId);
        return score != null ? score : 0;
    }
    
    // Check if candidate completed the test and update status if score >= 50%
    private void checkAndUpdateCandidateStatus(Integer candidatId, Integer testId) {
        try {
            // Get total questions in the test
            Long totalQuestions = countQuestionsByTestId(testId);
            
            // Get candidate's responses for this test
            List<Qcmreponse> responses = getCandidateResponsesForTest(candidatId, testId);
            
            // Check if candidate has answered all questions
            if (responses.size() >= totalQuestions) {
                // Calculate final score percentage
                Integer totalScore = getCandidateScoreForTest(candidatId, testId);
                Integer totalPoints = getTotalPointsByTestId(testId);
                
                if (totalPoints != null && totalPoints > 0) {
                    double scorePercentage = ((double) totalScore / totalPoints) * 100;
                    
                    // If score >= 50%, update candidate status to "Entretien"
                    if (scorePercentage >= 50.0) {
                        Optional<Statutcandidat> entretienStatus = statutcandidatRepository.findByNom("Entretien");
                        if (entretienStatus.isPresent()) {
                            Optional<Candidat> candidat = candidatRepository.findById(candidatId);
                            if (candidat.isPresent()) {
                                Candidat candidatEntity = candidat.get();
                                candidatEntity.setIdstatut(entretienStatus.get());
                                candidatRepository.save(candidatEntity);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Log error but don't fail the response submission
            System.err.println("Error updating candidate status: " + e.getMessage());
        }
    }

    // Count answered questions for a candidate in a test
    public Long countAnsweredQuestions(Integer candidatId, Integer testId) {
        return qcmreponseRepository.countAnsweredQuestionsByCandidatAndTest(candidatId, testId);
    }

    // Check if candidate has started a test
    public boolean hasCandidateStartedTest(Integer candidatId, Integer testId) {
        return qcmreponseRepository.existsByIdcandidat_IdAndIdtest_Id(candidatId, testId);
    }

    // Get next question for candidate (first unanswered question)
    public Optional<Qcmquestion> getNextQuestionForCandidate(Integer candidatId, Integer testId) {
        List<Qcmquestion> allQuestions = getQuestionsByTestId(testId);
        List<Qcmreponse> candidateResponses = getCandidateResponsesForTest(candidatId, testId);
        
        // Find first question not answered by candidate
        for (Qcmquestion question : allQuestions) {
            boolean answered = candidateResponses.stream()
                .anyMatch(response -> response.getIdquestion().getId().equals(question.getId()));
            if (!answered) {
                return Optional.of(question);
            }
        }
        
        return Optional.empty(); // All questions answered
    }

    // Check if test is completed by candidate
    public boolean isTestCompletedByCandidate(Integer candidatId, Integer testId) {
        Long totalQuestions = countQuestionsByTestId(testId);
        Long answeredQuestions = countAnsweredQuestions(candidatId, testId);
        return totalQuestions.equals(answeredQuestions);
    }

    public List<Map<String, Object>> getAllQcmResults() {
        List<Map<String, Object>> results = new ArrayList<>();
        
        // Récupérer tous les résultats QCM avec les informations des candidats
        List<Qcmreponse> responses = qcmreponseRepository.findAll();
        
        // Grouper par candidat et test
        Map<String, Map<String, Object>> groupedResults = new HashMap<>();
        
        for (Qcmreponse response : responses) {
            String key = response.getIdcandidat().getId() + "_" + response.getIdtest().getId();
            
            if (!groupedResults.containsKey(key)) {
                Map<String, Object> result = new HashMap<>();
                
                // Récupérer les informations du candidat
                Optional<Candidat> candidat = candidatRepository.findById(response.getIdcandidat().getId());
                Optional<Qcmtest> test = qcmtestRepository.findById(response.getIdtest().getId());
                
                if (candidat.isPresent() && test.isPresent()) {
                    result.put("id", key);
                    result.put("candidatId", response.getIdcandidat().getId());
                    result.put("candidateName", candidat.get().getNom() + " " + candidat.get().getPrenom());
                    result.put("candidateEmail", candidat.get().getIdcomptecandidat() != null ? 
                        candidat.get().getIdcomptecandidat().getEmail() : "N/A");
                    result.put("testId", response.getIdtest().getId());
                    result.put("testTitle", test.get().getNom());
                    result.put("candidat", candidat.get()); // Ajouter l'objet candidat complet
                    
                    // Ajouter l'ID de l'annonce si le candidat a une candidature
                    if (candidat.get().getIdannonce() != null) {
                        result.put("annonceId", candidat.get().getIdannonce().getId());
                    }
                    
                    // Calculer le score total
                    Integer totalScore = getCandidateScoreForTest(response.getIdcandidat().getId(), response.getIdtest().getId());
                    Integer totalPoints = getTotalPointsByTestId(response.getIdtest().getId());
                    Long totalQuestions = countQuestionsByTestId(response.getIdtest().getId());
                    Long answeredQuestions = countAnsweredQuestions(response.getIdcandidat().getId(), response.getIdtest().getId());
                    
                    // Calculer le pourcentage correct
                    int scorePercentage = 0;
                    if (totalPoints != null && totalPoints > 0) {
                        scorePercentage = Math.round(((float) (totalScore != null ? totalScore : 0) / totalPoints) * 100);
                    }
                    
                    result.put("score", scorePercentage);
                    result.put("totalQuestions", totalQuestions);
                    result.put("answeredQuestions", answeredQuestions);
                    result.put("correctAnswers", answeredQuestions); // Approximation
                    result.put("passed", scorePercentage >= 70); // Seuil par défaut
                    result.put("passingScore", 70);
                    result.put("completedAt", response.getDatereponse());
                    result.put("duration", 1800); // Durée par défaut
                    
                    groupedResults.put(key, result);
                }
            }
        }
        
        results.addAll(groupedResults.values());
        return results;
    }

    public List<Map<String, Object>> getAllQcmTests() {
        List<Map<String, Object>> tests = new ArrayList<>();
        
        List<Qcmtest> qcmTests = qcmtestRepository.findAll();
        
        for (Qcmtest test : qcmTests) {
            Map<String, Object> testInfo = new HashMap<>();
            testInfo.put("id", test.getId());
            testInfo.put("title", test.getNom());
            testInfo.put("description", test.getNom()); // Using nom as description since no description field exists
            tests.add(testInfo);
        }
        
        return tests;
    }

    // Create a full QCM test with questions and choices using only DB fields
    public Qcmtest createFullTest(String nom, Integer profilId, List<Map<String, Object>> questionsPayload) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du test est requis");
        }
        Optional<Profil> profilOpt = profilRepository.findById(profilId);
        if (profilOpt.isEmpty()) {
            throw new IllegalArgumentException("Profil introuvable: id=" + profilId);
        }

        Qcmtest test = new Qcmtest();
        test.setNom(nom);
        test.setIdprofil(profilOpt.get());
        Qcmtest savedTest = qcmtestRepository.save(test);

        if (questionsPayload != null) {
            for (Map<String, Object> q : questionsPayload) {
                // numero, question, points, choix[]
                Integer numero = (Integer) q.getOrDefault("numero", 0);
                Object questionObj = q.get("question");
                Object pointsObj = q.get("points");

                if (questionObj == null || pointsObj == null) {
                    throw new IllegalArgumentException("Chaque question doit contenir 'question' et 'points'");
                }

                Qcmquestion question = new Qcmquestion();
                question.setIdtest(savedTest);
                question.setNumero(numero != null ? numero : 0);
                question.setQuestion(String.valueOf(questionObj));
                question.setPoints(pointsObj instanceof Number ? ((Number) pointsObj).intValue() : Integer.parseInt(pointsObj.toString()));
                Qcmquestion savedQuestion = qcmquestionRepository.save(question);

                Object choixList = q.get("choix");
                if (choixList instanceof List<?>) {
                    for (Object ch : (List<?>) choixList) {
                        if (ch instanceof Map) {
                            Map<?,?> cm = (Map<?,?>) ch;
                            Object texteObj = cm.get("texte");
                            Object estcorrectObj = cm.get("estcorrect");
                            if (texteObj == null) continue;
                            Qcmchoix choix = new Qcmchoix();
                            choix.setIdquestion(savedQuestion);
                            choix.setTexte(String.valueOf(texteObj));
                            choix.setEstcorrect(estcorrectObj != null && Boolean.parseBoolean(String.valueOf(estcorrectObj)));
                            qcmchoixRepository.save(choix);
                        }
                    }
                }
            }
        }

        return savedTest;
    }
}
