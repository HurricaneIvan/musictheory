package com.example.musictheory.repositories;

import com.example.musictheory.models.Question;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizRepository extends MongoRepository<Question, ObjectId> {

    Optional<Question> findQuestionByUid(String uid);

    void deleteByUid(String uid);
}
