package com.example.musictheory.controllers;

import com.example.musictheory.dtos.QuestionDto;
import com.example.musictheory.models.Question;
import com.example.musictheory.services.QuizService;
import com.example.musictheory.utils.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        verify(quizService, times(1)).findAllQuestions();
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
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
        verify(quizService, times(1)).findQuestionByUid("PARTY");
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testGetQuestion_invalidUID() throws Exception {
        doThrow(new FileNotFoundException("Question object does not exist in database. Retry with existing uid.")).when(quizService).findQuestionByUid("EMPTY");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("EMPTY"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        verify(quizService, times(1)).findQuestionByUid("EMPTY");
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
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
        verify(quizService, times(1)).createQuestion(validated);
        assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
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
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testCreateQuestion_NullOptions() throws Exception {
        QuestionDto dto = new QuestionDto("", "Who dun it?", "image", null, "Butler with the candle stick", "beginner");

        doThrow(new IOException("Options is missing")).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
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
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testCreateQuestion_NullImage() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("", "Who dun it?", "", options, "Butler with the candle stick", "beginner");
        Question validated = new Question("", "Who dun it?", "", options, "Butler with the candle stick", "beginner");

        doReturn(validated).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
        verify(quizService, times(1)).createQuestion(validated);
        assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testCreateQuestion_NullProficiency() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("", "Who dun it?", "image", options, "Butler with the candle stick", "");
        Question validated = new Question("", "Who dun it?", "image", options, "Butler with the candle stick", "");

        doReturn(validated).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
        verify(quizService, times(1)).createQuestion(validated);
        assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testUpdateQuestion() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("PARTY", "Who dun it?", "image", options, "Butler with the candle stick", "beginner");
        Question validated = new Question("PARTY", "Who dun it?", "image", options, "Butler with the candle stick", "beginner");

        doReturn(validated).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        verify(quizService, times(1)).updateQuestion(validated);
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

    }

    @Test
    void testUpdateQuestion_NullRequestBody() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testUpdateQuestion_EmptyRequestBody() throws Exception {
        QuestionDto dto = new QuestionDto();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testUpdateQuestion_EmptyUid() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("", "Who dun it?", "image", options, "Butler with the candle stick", "beginner");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testUpdateQuestion_InvalidUid() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("P@rt7", "Who dun it?", "image", options, "Butler with the candle stick", "beginner");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testUpdateQuestion_UidNotFound() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("PARTY", "Wh0(%34 dun it?", "image", options, "Butler with the candle stick", "beginner");
        Question validated = new Question("rainy", "Who dun it?", "image", options, "Butler with the candle stick", "beginner");

        doReturn(validated).when(util).questionValidator(dto);
        doThrow(new FileNotFoundException()).when(quizService).updateQuestion(validated);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testUpdateQuestion_InvalidQuestion() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("PARTY", "Wh0(%34 dun it?", "image", options, "Butler with the candle stick", "beginner");

        doThrow(new IOException()).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testUpdateQuestion_EmptyQuestion() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("PARTY", "", "image", options, "Butler with the candle stick", "beginner");
        Question validated = new Question("PARTY", "", "image", options, "Butler with the candle stick", "beginner");

        doReturn(validated).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        verify(quizService, times(1)).updateQuestion(validated);
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testUpdateQuestion_EmptyImage() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("PARTY", "Who dun it?", "", options, "Butler with the candle stick", "beginner");
        Question validated = new Question("PARTY", "Who dun it?", "", options, "Butler with the candle stick", "beginner");

        doReturn(validated).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        verify(quizService, times(1)).updateQuestion(validated);
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testUpdateQuestion_EmptyOptions() throws Exception {
        QuestionDto dto = new QuestionDto("PARTY", "Who dun it?", "", null, "Butler with the candle stick", "beginner");
        Question validated = new Question("PARTY", "Who dun it?", "", null, "Butler with the candle stick", "beginner");

        doReturn(validated).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        verify(quizService, times(1)).updateQuestion(validated);
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testUpdateQuestion_EmptyAnswer() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("PARTY", "Who dun it?", "", options, "", "beginner");
        Question validated = new Question("PARTY", "Who dun it?", "", options, "", "beginner");

        doReturn(validated).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        verify(quizService, times(1)).updateQuestion(validated);
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    void testUpdateQuestion_EmptyProficiency() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("PARTY", "Who dun it?", "image", options, "Butler with the candle stick", "");
        Question validated = new Question("PARTY", "Who dun it?", "image", options, "Butler with the candle stick", "");

        doReturn(validated).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        verify(quizService, times(1)).updateQuestion(validated);
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }

}
