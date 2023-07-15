package com.teamo.teamo.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "TEST_TABLE")
public class Test {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Test_ID")
    private Long id;

    private String name;
}
