package com.test.finalproject.entity;

import com.test.finalproject.enums.ProgressStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @Builder
public class Task extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Date completeDate;

    private ProgressStatus status;

    @OneToMany(mappedBy = "task",fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskDetail> taskDetails;

    @ManyToOne
    @JoinColumn(name = "userId",referencedColumnName = "id")
    private User user;
}
