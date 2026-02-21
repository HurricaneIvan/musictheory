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

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(questionController).build();
    }

    @Test
    public void testGetAllQuestions() throws Exception {
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
    public void testGetQuestion_sunny_day() throws Exception {
        doReturn(true).when(util).isValidUid("PARTY");

        Question mockQuestion = mock(Question.class);
        when(quizService.findQuestionByUid(Mockito.anyString())).thenReturn(Optional.ofNullable(mockQuestion));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("PARTY"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        verify(quizService, times(1)).findQuestionByUid("PARTY");
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testGetQuestion_NotFoundUid() throws Exception {
        doReturn(true).when(util).isValidUid("EMPTY");
        doThrow(new FileNotFoundException("Question object does not exist in database. Retry with existing uid.")).when(quizService).findQuestionByUid("EMPTY");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("EMPTY"))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
        verify(quizService, times(1)).findQuestionByUid("EMPTY");
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testGetQuestion_invalidUID() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("E#P7Y"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testCreateQuestion() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("", "Who dun it?", "image", options, "Butler with the candle stick", "beginner");
        List<QuestionDto> list = new ArrayList<>();
        list.add(dto);
        Question validated = new Question("PARTY", "Who dun it?", "image", options, "Butler with the candle stick", "beginner");
        List<Question> validList = new ArrayList<>();
        validList.add(validated);
        doReturn(validated).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(list)))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
        verify(quizService, times(1)).createQuestion(validList);
        assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testCreateQuestion_NullQuestion() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("", "", "image", options, "Butler with the candle stick", "beginner");
        List<QuestionDto> list = new ArrayList<>();
        list.add(dto);

        doThrow(new IOException("Question null")).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(list)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testCreateQuestion_NullOptions() throws Exception {
        QuestionDto dto = new QuestionDto("", "Who dun it?", "image", null, "Butler with the candle stick", "beginner");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testCreateQuestion_NullAnswer() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("", "Who dun it?", "image", options, " ", "beginner");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testCreateQuestion_NullImage() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("", "Who dun it?", "", options, "Butler with the candle stick", "beginner");
        List<QuestionDto> list = new ArrayList<>();
        list.add(dto);
        Question validated = new Question("", "Who dun it?", "", options, "Butler with the candle stick", "beginner");
        List<Question> validList = new ArrayList<>();
        validList.add(validated);
        doReturn(validated).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(list)))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
        verify(quizService, times(1)).createQuestion(validList);
        assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testCreateQuestion_NullProficiency() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("", "Who dun it?", "image", options, "Butler with the candle stick", "");
        List<QuestionDto> list = new ArrayList<>();
        list.add(dto);
        Question validated = new Question("", "Who dun it?", "", options, "Butler with the candle stick", "");
        List<Question> validList = new ArrayList<>();
        validList.add(validated);
        doReturn(validated).when(util).questionValidator(dto);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(list)))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
        verify(quizService, times(1)).createQuestion(validList);
        assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testUpdateQuestion() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("PARTY", "Who dun it?", "image", options, "Butler with the candle stick", "beginner");
        Question validated = new Question("PARTY", "Who dun it?", "image", options, "Butler with the candle stick", "beginner");

        doReturn(true).when(util).isValidUid("PARTY");
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
    public void testUpdateQuestion_NullRequestBody() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testUpdateQuestion_EmptyRequestBody() throws Exception {
        QuestionDto dto = new QuestionDto();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testUpdateQuestion_EmptyUid() throws Exception {
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
    public void testUpdateQuestion_InvalidUid() throws Exception {
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
    public void testUpdateQuestion_UidNotFound() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("PARTY", "Wh0(%34 dun it?", "image", options, "Butler with the candle stick", "beginner");
        Question validated = new Question("rainy", "Who dun it?", "image", options, "Butler with the candle stick", "beginner");

        doReturn(true).when(util).isValidUid("PARTY");
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
    public void testUpdateQuestion_InvalidQuestion() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("PARTY", "Wh0(%34 dun it?", "image", options, "Butler with the candle stick", "beginner");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testUpdateQuestion_EmptyQuestion() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("PARTY", "", "image", options, "Butler with the candle stick", "beginner");
        Question validated = new Question("PARTY", "", "image", options, "Butler with the candle stick", "beginner");

        doReturn(true).when(util).isValidUid("PARTY");
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
    public void testUpdateQuestion_EmptyImage() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("PARTY", "Who dun it?", "", options, "Butler with the candle stick", "beginner");
        Question validated = new Question("PARTY", "Who dun it?", "", options, "Butler with the candle stick", "beginner");
        doReturn(true).when(util).isValidUid("PARTY");
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
    public void testUpdateQuestion_EmptyOptions() throws Exception {
        QuestionDto dto = new QuestionDto("PARTY", "Who dun it?", "", null, "Butler with the candle stick", "beginner");
        Question validated = new Question("PARTY", "Who dun it?", "", null, "Butler with the candle stick", "beginner");
        doReturn(true).when(util).isValidUid("PARTY");
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
    public void testUpdateQuestion_EmptyAnswer() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("PARTY", "Who dun it?", "", options, "", "beginner");
        Question validated = new Question("PARTY", "Who dun it?", "", options, "", "beginner");
        doReturn(true).when(util).isValidUid("PARTY");
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
    public void testUpdateQuestion_EmptyProficiency() throws Exception {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        QuestionDto dto = new QuestionDto("PARTY", "Who dun it?", "image", options, "Butler with the candle stick", "");
        Question validated = new Question("PARTY", "Who dun it?", "image", options, "Butler with the candle stick", "");
        doReturn(true).when(util).isValidUid("PARTY");
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
    public void testSoftDeleteQuestion_sunny_day() throws Exception {
        doReturn(true).when(util).isValidUid("PAR3Y");
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("PAR3Y"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        verify(quizService, times(1)).softDeleteQuestion("PAR3Y");
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testSoftDeleteQuestion_NotFoundUid() throws Exception {
        doReturn(true).when(util).isValidUid("PARTY");
        doThrow(new FileNotFoundException("Uid not Found. Retry with valid uid")).when(quizService).softDeleteQuestion("PARTY");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("PARTY"))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testSoftDeleteQuestion_InvalidUid() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("P@R^9"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testDeleteQuestion_sunny_day() throws Exception {
        doReturn(true).when(util).isValidUid("PAR3Y");
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("PAR3Y"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        verify(quizService, times(1)).deleteQuestion("PAR3Y");
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testDeleteQuestion_NotFoundUid() throws Exception {
        doReturn(true).when(util).isValidUid("PARTY");
        doThrow(new FileNotFoundException("Uid not Found. Retry with valid uid")).when(quizService).deleteQuestion("PARTY");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("PARTY"))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testDeleteQuestion_InvalidUid() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("P@R^9"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }
}
