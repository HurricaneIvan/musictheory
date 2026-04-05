package com.example.musictheory.services;

import com.example.musictheory.models.Question;
import com.example.musictheory.repositories.QuizRepository;
import com.example.musictheory.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private Util util;

    public List<Question> findAllQuestions(){
        return quizRepository.findAll();
    }

    public Optional<Question> findQuestionByUid(String uid) throws FileNotFoundException {
        Optional<Question> question = quizRepository.findQuestionByUid(uid);
        return Optional.ofNullable(question.orElseThrow(() -> new FileNotFoundException("Question object does not exist in database. Retry with existing uid.")));

    }

    public List<Question> createQuestion(List<Question> questions) {
        List<Question> response = new ArrayList<>();
        for (Question question : questions){
            String uid;
            do {
                uid = util.generateUid();
            } while (quizRepository.findQuestionByUid(uid).isPresent());

            System.out.println("UID :: " + uid);

            Question newQuestion = new Question();
            newQuestion.setUid(uid);
            newQuestion.setQuestion(question.getQuestion());
            newQuestion.setOptions(question.getOptions());
            newQuestion.setAnswer(question.getAnswer());
            if(question.getImage() != null && !question.getImage().isBlank()){
                newQuestion.setImage(question.getImage());
            }
            if(question.getProficiency() != null && !question.getProficiency().isBlank()){
                newQuestion.setProficiency(question.getProficiency());
            }
            System.out.println(newQuestion.toString());
            response.add(quizRepository.save(newQuestion));
        }
        return response;
    }

    public Question updateQuestion(Question updatedQuestion) throws IOException {

        Question question = quizRepository.findQuestionByUid(updatedQuestion.getUid())
            .orElseThrow(() -> new FileNotFoundException("Question object does not exist in database. Retry with existing uid."));

        if (!updatedQuestion.getQuestion().isBlank()) {
            question.setQuestion(updatedQuestion.getQuestion());
        }
        if (!updatedQuestion.getImage().isBlank()) {
            question.setImage(updatedQuestion.getImage());
        }
        if (!updatedQuestion.getOptions().isEmpty()) {
            question.setOptions(updatedQuestion.getOptions());
        }
        if (!updatedQuestion.getAnswer().isBlank()) {
            question.setAnswer(updatedQuestion.getAnswer());
        }
        if (!updatedQuestion.getProficiency().isBlank()) {
            question.setProficiency(updatedQuestion.getProficiency());
        }

        return quizRepository.save(question);
    }

    public void softDeleteQuestion(String uid) throws FileNotFoundException {

        Optional<Question> question = quizRepository.findQuestionByUid(uid);
        if (question.isPresent()) {
            quizRepository.delete(question.get());
        } else {
            throw new FileNotFoundException("Uid not Found. Retry with valid uid");
        }

    }

    public void deleteQuestion(String uid) throws FileNotFoundException {

        Optional<Question> question = quizRepository.findQuestionByUid(uid);
        question.orElseThrow(()  -> new FileNotFoundException("Question object does not exist in database. Retry with existing uid."));

        quizRepository.deleteByUid(uid);
    }
}
