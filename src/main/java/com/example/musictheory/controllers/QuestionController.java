package com.example.musictheory.controllers;

import com.example.musictheory.dtos.QuestionDto;
import com.example.musictheory.models.Question;
import com.example.musictheory.services.QuizService;
import com.example.musictheory.utils.Util;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class QuestionController {

    private static final Logger log = LogManager.getLogger(QuestionController.class);
    @Autowired
    private QuizService quizService;

    @Autowired
    private Util util;

/*
    1) POST questions parameters (number of questions to return, proficiency)
    2) POST questions parameters (question Obj)
    3) Update questions parameters (question obj)
    4) DELETE questions
    2-4 requires permission or admin rights?
 */

    @GetMapping(value = "/questions")
    public ResponseEntity<List<Question>> getAllQuestions() {

        return new ResponseEntity<>(quizService.findAllQuestions(), HttpStatus.OK);
    }

    @GetMapping(value = "/question")
    public ResponseEntity<Optional<Question>> getQuestion(@RequestBody String uid){
        try {
            if (util.isValidUid(uid)) {
                return new ResponseEntity<>(quizService.findQuestionByUid(uid), HttpStatus.OK);
            } else{
                throw new IOException("Invalid Uid. Retry with valid uid.");
            }
        } catch (FileNotFoundException e) {
            log.error("e: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            log.error("e: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/question")
    public ResponseEntity<Question> createQuestion(@Valid @RequestBody QuestionDto questionDto) { // input is a sub-model: question, options, answer, uid is generated internally(not public)

        Question question;
        try {
            question = util.questionValidator(questionDto);
            return new ResponseEntity<>(quizService.createQuestion(question), HttpStatus.CREATED);

        } catch (IOException e) {
            log.error("e: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @PatchMapping(value = "/question")
    public ResponseEntity<Question> updateQuestion(@RequestBody QuestionDto question) { // input is a sub-model: question, options, answer, uid is generated internally(not public)
        try {
            Question validated;
            if (question != null && util.isValidUid(question.getUid())) {
                validated = util.questionValidator(question);
            } else{
                throw new IOException("Invalid Input :: Missing Question Object and/or uid");
            }

            return new ResponseEntity<>(quizService.updateQuestion(validated), HttpStatus.OK);

        } catch (FileNotFoundException e) {
            log.error("e: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            log.error("e: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Soft Delete
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/question")
    public ResponseEntity<String> deleteSoftQuestion(@RequestBody String uid) {
        try {
            if (util.isValidUid(uid)) {
                quizService.softDeleteQuestion(uid);
                return new ResponseEntity<>("Question Removed", HttpStatus.OK);
            } else {
                throw new IOException("Invalid Uid. Retry with valid uid.");
            }
        } catch (FileNotFoundException e) {
            log.error("e: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            log.error("e: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Delete
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/delete")
    public ResponseEntity<String> deleteQuestion(@RequestBody String uid) {
        try{
            if (util.isValidUid(uid)) {
                quizService.deleteQuestion(uid);
                return new ResponseEntity<>("Question Removed", HttpStatus.OK);
            } else {
                throw new IOException("Invalid Uid. Retry with valid uid.");
            }

        } catch (FileNotFoundException e) {
            log.error("e: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }  catch (IOException e) {
            log.error("e: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
