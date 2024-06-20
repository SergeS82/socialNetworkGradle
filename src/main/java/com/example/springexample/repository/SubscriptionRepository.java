package com.example.springexample.repository;

import com.example.springexample.entity.Author;
import com.example.springexample.entity.Subscription;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {
    List<Subscription> findByAuthorOrderByIdDesc(Author author, Pageable pageable);
}
