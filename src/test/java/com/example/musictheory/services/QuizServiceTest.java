package com.example.musictheory.services;

import com.example.musictheory.models.Question;
import com.example.musictheory.repositories.QuizRepository;
import com.example.musictheory.utils.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizServiceTest {
    @Mock
    private QuizRepository quizRepository;

    @Mock
    private Util util;

    @InjectMocks
    private QuizService quizService;

    @Test
    public void testFindAllQuestion_sunny_day() {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        Question expectedQuestion = new Question("PARTY", "Who dun it?", "image", options, "Butler with the candle stick", "beginner");
        List<Question> questionList = new ArrayList<>();
        questionList.add(expectedQuestion);
        when(quizRepository.findAll()).thenReturn(questionList);

        List<Question> actual = quizService.findAllQuestions();

        assertEquals(questionList, actual);
    }

    @Test
    public void testFindQuestionByUid_sunny_day() throws FileNotFoundException {
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        Optional<Question> expectedQuestion = Optional.of(new Question("PARTY", "Who dun it?", "image", options, "Butler with the candle stick", "beginner"));

        when(quizRepository.findQuestionByUid("PARTY")).thenReturn(expectedQuestion);

        Optional<Question> actualQuestion = quizService.findQuestionByUid("PARTY");

        assertEquals(expectedQuestion, actualQuestion);
    }

    @Test
    public void testFindQuestionByUid_invalid_Uid() {
        assertThrows(FileNotFoundException.class, () -> quizService.findQuestionByUid("PARTY"));
    }
    @Test
    public void testFindQuestionByUid_empty_Uid() {
        assertThrows(FileNotFoundException.class, () -> quizService.findQuestionByUid(""));
    }
    @Test
    public void testFindQuestionByUid_null_Uid() {
        assertThrows(FileNotFoundException.class, () -> quizService.findQuestionByUid(null));
    }
    @Test
    public void testFindQuestionByUid_emptySpaces_Uid() {
        assertThrows(FileNotFoundException.class, () -> quizService.findQuestionByUid("     "));
    }
    @Test
    public void testFindQuestionByUid_Uid_with_space() {
        assertThrows(FileNotFoundException.class, () -> {
            quizService.findQuestionByUid("PAR Y");
        });
    }

    @Test
    public void testCreateQuestion_sunny_day() {
        String expectedUid = "PARTY";

        doReturn(expectedUid).when(util).generateUid();

        String generatedUid = util.generateUid();
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        Question expected = new Question(expectedUid, "Who dun it?", "image", options, "Butler with the candle stick", "beginner");

        when(quizRepository.findQuestionByUid(expectedUid)).thenReturn(Optional.empty());
        when(quizRepository.save(expected)).thenReturn(expected);

        Question actualQuestion = new Question(generatedUid, "Who dun it?", "image", options, "Butler with the candle stick", "beginner");
        List<Question> list = new ArrayList<>();
        list.add(actualQuestion);
        List<Question> actual = quizService.createQuestion(list);

        assertEquals(expectedUid, generatedUid);
        assertEquals(expected, actual.get(0));
    }

    @Test
    public void testCreateQuestion_rainy_day_existing_uid() {
        String existingUid = "PARTY";
        String newUid = "rainy";

        when(util.generateUid()).thenReturn(existingUid, newUid);
        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        Question expected = new Question(newUid, "Who dun it?", "image", options, "Butler with the candle stick", "beginner");
        //noinspection unchecked
        when(quizRepository.findQuestionByUid(Mockito.any())).thenReturn(Optional.of(mock(Question.class)), Optional.empty());
        when(quizRepository.save(expected)).thenReturn(expected);

        Question actualQuestion = new Question(newUid, "Who dun it?", "image", options, "Butler with the candle stick", "beginner");
        List<Question> list = new ArrayList<>();
        list.add(actualQuestion);
        List<Question> actual = quizService.createQuestion(list);

        assertEquals(expected, actual.get(0));
    }

    @Test
    public void testCreateQuestion_rainy_day_with_null_image() {
        String expectedUid = "PARTY";
        doReturn(expectedUid).when(util).generateUid();
        String generatedUid = util.generateUid();

        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        Question expected = new Question(expectedUid, "Who dun it?", null, options, "Butler with the candle stick", "beginner");

        when(quizRepository.save(expected)).thenReturn(expected);

        Question actualQuestion = new Question(generatedUid, "Who dun it?", null, options, "Butler with the candle stick", "beginner");
        List<Question> list = new ArrayList<>();
        list.add(actualQuestion);
        List<Question> actual = quizService.createQuestion(list);

        assertEquals(expectedUid, generatedUid);
        assertEquals(expected, actual.get(0));
    }

    @Test
    public void testCreateQuestion_rainy_day_with_blank_image() {
        String expectedUid = "PARTY";
        doReturn(expectedUid).when(util).generateUid();
        String generatedUid = util.generateUid();

        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        Question expected = new Question(expectedUid, "Who dun it?", null, options, "Butler with the candle stick", "beginner");

        when(quizRepository.save(expected)).thenReturn(expected);

        Question actualQuestion = new Question(generatedUid, "Who dun it?", "", options, "Butler with the candle stick", "beginner");
        List<Question> list = new ArrayList<>();
        list.add(actualQuestion);
        List<Question> actual = quizService.createQuestion(list);

        assertEquals(expectedUid, generatedUid);
        assertEquals(expected, actual.get(0));
    }

    @Test
    public void testCreateQuestion_rainy_day_with_empty_image() {
        String expectedUid = "PARTY";
        doReturn(expectedUid).when(util).generateUid();
        String generatedUid = util.generateUid();

        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        Question expected = new Question(expectedUid, "Who dun it?", null, options, "Butler with the candle stick", "beginner");

        when(quizRepository.save(expected)).thenReturn(expected);

        Question actualQuestion = new Question(generatedUid, "Who dun it?", "   ", options, "Butler with the candle stick", "beginner");
        List<Question> list = new ArrayList<>();
        list.add(actualQuestion);
        List<Question> actual = quizService.createQuestion(list);

        assertEquals(expectedUid, generatedUid);
        assertEquals(expected, actual.get(0));
    }

    @Test
    public void testCreateQuestion_rainy_day_with_null_proficiency() {
        String expectedUid = "PARTY";
        doReturn(expectedUid).when(util).generateUid();
        String generatedUid = util.generateUid();

        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        Question expected = new Question(expectedUid, "Who dun it?", "image", options, "Butler with the candle stick", null);

        when(quizRepository.save(expected)).thenReturn(expected);

        Question actualQuestion = new Question(generatedUid, "Who dun it?", "image", options, "Butler with the candle stick", null);
        List<Question> list = new ArrayList<>();
        list.add(actualQuestion);
        List<Question> actual = quizService.createQuestion(list);

        assertEquals(expectedUid, generatedUid);
        assertEquals(expected, actual.get(0));
    }

    @Test
    public void testCreateQuestion_rainy_day_with_blank_proficiency() {
        String expectedUid = "PARTY";
        doReturn(expectedUid).when(util).generateUid();
        String generatedUid = util.generateUid();

        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        Question expected = new Question(expectedUid, "Who dun it?", "image", options, "Butler with the candle stick", null);

        when(quizRepository.save(expected)).thenReturn(expected);

        Question actualQuestion = new Question(generatedUid, "Who dun it?", "image", options, "Butler with the candle stick", "");
        List<Question> list = new ArrayList<>();
        list.add(actualQuestion);
        List<Question> actual = quizService.createQuestion(list);

        assertEquals(expectedUid, generatedUid);
        assertEquals(expected, actual.get(0));
    }

    @Test
    public void testCreateQuestion_rainy_day_with_empty_proficiency() {
        String expectedUid = "PARTY";
        doReturn(expectedUid).when(util).generateUid();
        String generatedUid = util.generateUid();

        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        Question expected = new Question(expectedUid, "Who dun it?", "image", options, "Butler with the candle stick", null);

        when(quizRepository.save(expected)).thenReturn(expected);

        Question actualQuestion = new Question(generatedUid, "Who dun it?", "image", options, "Butler with the candle stick", " ");
        List<Question> list = new ArrayList<>();
        list.add(actualQuestion);
        List<Question> actual = quizService.createQuestion(list);

        assertEquals(expectedUid, generatedUid);
        assertEquals(expected, actual.get(0));
    }

    @Test
    public void testUpdateQuestion_sunny_day_question() throws IOException {
        String expectedUid = "PARTY";

        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        List<String> actualOptions = Arrays.asList("Butler", "Gardener", "Nephew", "Aunt");
        Question expected = new Question(expectedUid, "Who dun it?", "image", options, "Butler with the candle stick", "beginner");
        Question actualQuestion = new Question(expectedUid, "Find who commited the crime?", "image2", actualOptions, "electric", "beginner");

        when(quizRepository.findQuestionByUid(expectedUid)).thenReturn(Optional.of(expected));
        when(quizRepository.save(expected)).thenReturn(expected);
        Question actual = quizService.updateQuestion(actualQuestion);

        assertEquals(expected.getImage(), actual.getImage());
        assertEquals(expected.getQuestion(), actual.getQuestion());
    }

    @Test
    public void testUpdateQuestion_sunny_day_image() throws IOException {
        String expectedUid = "PARTY";

        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        Question expected = new Question(expectedUid, "Who dun it?", "image2", options, "Butler with the candle stick", "beginner");
        Question actualQuestion = new Question(expectedUid, "", "faulty", options, " ", "");

        when(quizRepository.findQuestionByUid(expectedUid)).thenReturn(Optional.of(expected));
        when(quizRepository.save(expected)).thenReturn(expected);
        Question actual = quizService.updateQuestion(actualQuestion);

        assertEquals(expected.getImage(), actual.getImage());
        assertEquals(expected.getQuestion(), actual.getQuestion());
    }

    @Test
    public void testUpdateQuestion_uid_not_found() {
        String expectedUid = "PARTY";

        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        Question actualQuestion = new Question(expectedUid, "", "faulty", options, " ", "");

        when(quizRepository.findQuestionByUid(expectedUid)).thenReturn(Optional.empty());
        assertThrows(FileNotFoundException.class, () -> quizService.updateQuestion(actualQuestion));
    }

    @Test
    public void testSoftDeleteQuestion_sunny_day() throws FileNotFoundException {
        String expectedUid = "PARTY";

        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        Question question = new Question(expectedUid, "Who dun it?", "image", options, "Butler with the candle stick", "beginner");

        when(quizRepository.findQuestionByUid(expectedUid)).thenReturn(Optional.of(question));

        quizService.softDeleteQuestion(expectedUid);
    }

    @Test
    public void testSoftDeleteQuestion_uid_not_found() {
        String expectedUid = "PARTY";
        when(quizRepository.findQuestionByUid(expectedUid)).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class, () -> {
            quizService.softDeleteQuestion(expectedUid);
        });
    }

    @Test
    public void testDeleteQuestion_sunny_day() throws FileNotFoundException {
        String expectedUid = "PARTY";

        List<String> options = Arrays.asList("Butler with the candle stick", "Gardener in the foyer", "Drunk Nephew", "Jealous Aunt");
        Question question = new Question(expectedUid, "Who dun it?", "image", options, "Butler with the candle stick", "beginner");

        when(quizRepository.findQuestionByUid(expectedUid)).thenReturn(Optional.of(question));

        quizService.deleteQuestion(expectedUid);
    }

    @Test
    public void testDeleteQuestion_uid_not_found() {
        String expectedUid = "PARTY";
        when(quizRepository.findQuestionByUid(expectedUid)).thenReturn(Optional.empty());
        assertThrows(FileNotFoundException.class, () -> {
            quizService.deleteQuestion(expectedUid);
        });
    }
}
