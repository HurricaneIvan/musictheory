//package com.example.musictheory.repositories;
//
//import com.example.musictheory.models.Question;
//import com.example.musictheory.models.Score;
////import com.example.musictheory.utils.MongoDBConnection;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//import org.bson.Document;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class QuizRepositoryInpl implements QuizRepository {
//
//
//    public QuizRepositoryInpl() {
//        super();
////        this.database = null;//MongoDBConnection.getDatabase();
//    }
//
//
////    public void addQuestion(Question question) {
////        MongoCollection<Document> collection = database.getCollection("questions");
////        Document newDoc = new Document();
////        newDoc.put("uuid", question.getUuid());
////        newDoc.put("question", question.getQuestion());
////        newDoc.put("image", question.image);
////        newDoc.put("options", question.options);
////        newDoc.put("answer", question.answer);
////        newDoc.put("proficiency", question.proficiency);
////        collection.insertOne(newDoc);
////    }
////
//    public void updateQuestion(String uid, Question question) {
//        if (findQuestionByUid(uid)
//        existing.replace("question", question.getQuestion());
//        existing.replace("image", question.image);
//        existing.replace("options", question.options);
//        existing.replace("answer", question.answer);
//        existing.replace("proficiency", question.proficiency);
//    }
//
////    public void saveScore(Score score) {
////        MongoCollection<Document> collection = database.getCollection("scores");
////        Document scoreboard = new Document("user", score.getUser())
////                .append("score", score.getScore())
////                .append("date", score.getDate());
////        collection.insertOne(scoreboard);
////
////    }
//}
