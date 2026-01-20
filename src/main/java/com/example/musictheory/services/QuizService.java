package com.example.musictheory.services;

import com.example.musictheory.models.Question;
import com.example.musictheory.repositories.QuizRepository;
import com.example.musictheory.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
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

    public Question createQuestion(Question question) {
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

        return quizRepository.save(newQuestion);
    }

    public Question updateQuestion(Question updatedQuestion) throws IOException {

        Optional<Question> question = quizRepository.findQuestionByUid(updatedQuestion.getUid());
        if (question.isPresent()) {
            if (!updatedQuestion.getQuestion().isBlank()) {
                question.get().setQuestion(updatedQuestion.getQuestion());
            }
            if (!updatedQuestion.getImage().isBlank()) {
                question.get().setImage(updatedQuestion.getImage());
            }
            if (!updatedQuestion.getOptions().isEmpty()) {
                question.get().setOptions(updatedQuestion.getOptions());
            }
            if (!updatedQuestion.getAnswer().isBlank()) {
                question.get().setAnswer(updatedQuestion.getAnswer());
            }
            if (!updatedQuestion.getProficiency().isBlank()) {
                question.get().setProficiency(updatedQuestion.getProficiency());
            }

            return quizRepository.save(question.get());

        } else {
            throw new FileNotFoundException("Question object does not exist in database. Retry with existing uid.");
        }
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
