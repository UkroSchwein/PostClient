package com.example.postclient.Repository;

import com.example.postclient.Models.EmailPasswordPair;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailPasswordPairRepository extends JpaRepository<EmailPasswordPair, Long> {
}
