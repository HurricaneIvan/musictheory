package com.example.musictheory.controllers;

import com.example.musictheory.dtos.QuestionDto;
import com.example.musictheory.models.Question;
import com.example.musictheory.services.QuizService;
import com.example.musictheory.utils.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private QuestionController questionController;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private QuizService quizService;

    @Mock
    private Util util;


//    @BeforeEach
//    public void setUp(){
//        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
//        question = new Question("PARTY", "Who dun it?", "image", options, "Butler with the candle stick", "beginner");
//
//    }

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(questionController).build();
    }

    @Test
    void testGetAllQuestions() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        Question expectedQuestion = new Question("PARTY", "Who dun it?", "image", options, "Butler with the candle stick", "beginner");
        List<Question> questionList = new ArrayList<>();
        questionList.add(expectedQuestion);
        when(quizService.findAllQuestions()).thenReturn(questionList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/questions"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Mockito.verify(quizService, Mockito.times(1)).findAllQuestions();
        Assertions.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testGetQuestion() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        Question expected = new Question("PARTY", "Who dun it?", "image", options, "Butler with the candle stick", "beginner");

        when(quizService.findQuestionByUid("PARTY")).thenReturn(Optional.of(expected));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("PARTY"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Mockito.verify(quizService, Mockito.times(1)).findQuestionByUid("PARTY");
        Assertions.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testGetQuestion_invalidUID() throws Exception {
        doThrow(new FileNotFoundException("Question object does not exist in database. Retry with existing uid.")).when(quizService).findQuestionByUid("EMPTY");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("EMPTY"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        Mockito.verify(quizService, Mockito.times(1)).findQuestionByUid("EMPTY");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testCreateQuestion() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("", "Who dun it?", "image", options, "Butler with the candle stick", "beginner");
        Question validated = new Question("", "Who dun it?", "image", options, "Butler with the candle stick", "beginner");

        doReturn(validated).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
        Mockito.verify(quizService, Mockito.times(1)).createQuestion(validated);
        Assertions.assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testCreateQuestion_NullQuestion() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("", "", "image", options, "Butler with the candle stick", "beginner");

        doThrow(new IOException("Question is missing")).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testCreateQuestion_NullOptions() throws Exception {
        QuestionDto dto = new QuestionDto("", "Who dun it?", "image", null, "Butler with the candle stick", "beginner");

        doThrow(new IOException("Options is missing")).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testCreateQuestion_NullAnswer() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("", "Who dun it?", "image", options, " ", "beginner");

        doThrow(new IOException("Answer is missing")).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

}
