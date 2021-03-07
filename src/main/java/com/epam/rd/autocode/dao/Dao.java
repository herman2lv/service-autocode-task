package com.epam.rd.autocode.dao;

import java.util.List;

public interface Dao<T, Id> {
    T getById(Id id);

    List<T> getAll();

    T save(T t);

    void delete(T t);
}
