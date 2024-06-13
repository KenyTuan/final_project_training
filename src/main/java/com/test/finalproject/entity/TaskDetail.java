package com.test.finalproject.entity;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity @Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter @Builder
public class TaskDetail extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "taskId")
    private Task task;


}
