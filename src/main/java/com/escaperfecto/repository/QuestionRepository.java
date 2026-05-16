package com.escaperfecto.repository;

import com.escaperfecto.model.Question;

import java.util.List;

public interface QuestionRepository {
    List<Question> findAll();

    void add(Question question);

    void update(Question question);

    void deleteById(int id);
}

