package com.escaperfecto.repository;

import com.escaperfecto.model.Prize;

import java.util.List;

public interface PrizeRepository {
    List<Prize> findAll();

    void add(Prize prize);

    void update(Prize prize);

    void deleteById(int id);
}

