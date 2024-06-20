package com.example.springexample.services.lib;

import java.util.List;

public interface CRUDService<T> {
    T getById(Long id);
    List<T> getTop(Long item, Integer count);
    void create (T item);
    void update(T item);
    void delete(Long id);
}
