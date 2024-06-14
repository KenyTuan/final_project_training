package com.test.finalproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter @Builder
public class VerifyEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String token;

    private Timestamp expiryDate;

    @ManyToOne(targetEntity = User.class,fetch = FetchType.EAGER)
    @JoinColumn(nullable = false,name = "userId")
    private User user;

    public boolean isTokenExpired() {
        return new Timestamp(System.currentTimeMillis()).after(this.expiryDate);
    }
}
