package com.example.springexample.entity;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "comment", schema = "sn")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /*GenerationType.AUTO   - позволяет JPA выбрать наиволее подходящий способ
    GenerationType.IDENTITY - Генерация значения автоинкрементных столбцов если БД поддерживает (My-SQL, PostgresSQL)
    GenerationType.SEQUENCE - Генерация значения с использованием последовательности БД (Oracle)
    GenerationType.TABLE    - Генерация с использованием специальной таблицы БД, которая хранит информацию  ..
                                .. о текущем значении. Менее эфективна и редко используется.
    */
    @Column(name="id")
    private Long id;
    @Column(name = "text")
    private String text;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author")
    private Author author;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post")
    private Post post;
    @CreationTimestamp
    @Column(name = "creation_time")
    private LocalDateTime time;
}
