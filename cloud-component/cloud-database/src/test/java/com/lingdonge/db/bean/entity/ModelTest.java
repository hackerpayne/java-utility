package com.lingdonge.db.bean.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "test")
@Data
public class ModelTest {

    @Id
    private Integer id;

    private Integer userId;

    private String title;

    private String body;
}
