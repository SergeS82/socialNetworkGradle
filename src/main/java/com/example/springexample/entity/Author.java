package com.example.springexample.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "author", schema = "sn")
@Where(clause = "is_deleted = false")
public class Author implements com.example.springexample.entity.lib.Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name="mail")
    private String mail;
    @Column(name="phone")
    private String phone;
    @Column(name = "sex")
    private Character sex;
    @Column(name = "city")
    String city;
    @Column(name = "is_deleted")
    Boolean isDeleted;
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;
}
