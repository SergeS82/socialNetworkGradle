package com.example.springexample.repository;

import com.example.springexample.entity.Author;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Author u SET u.isDeleted = true WHERE u.id = :id")
    @Override
    void deleteById(@Param("id") Long id);

}
