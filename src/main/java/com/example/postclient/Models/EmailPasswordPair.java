package com.example.postclient.Models;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name ="EmailPasswordPair")
public class EmailPasswordPair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

}
