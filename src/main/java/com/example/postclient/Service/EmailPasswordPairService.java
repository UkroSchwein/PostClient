package com.example.postclient.Service;

import com.example.postclient.Models.EmailPasswordPair;
import com.example.postclient.Repository.EmailPasswordPairRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class EmailPasswordPairService {

    private EmailPasswordPairRepository emailPasswordPairRepository;


    public EmailPasswordPair saveEmailPasswordPair(EmailPasswordPair emailPasswordPair) {
        return emailPasswordPairRepository.save(emailPasswordPair);
    }

    public EmailPasswordPair getEmailPasswordPairById(Long id) {
        return emailPasswordPairRepository.findById(id).orElse(null);
    }

    public List<EmailPasswordPair> getAllEmailPasswordPairs() {
        return emailPasswordPairRepository.findAll();
    }

    public EmailPasswordPair updateEmailPasswordPair(Long id, EmailPasswordPair updatedPair) {
        Optional<EmailPasswordPair> optionalPair = emailPasswordPairRepository.findById(id);
        if (optionalPair.isPresent()) {
            EmailPasswordPair existingPair = optionalPair.get();
            existingPair.setEmail(updatedPair.getEmail());
            existingPair.setPassword(updatedPair.getPassword());
            return emailPasswordPairRepository.save(existingPair);
        }
        return null;
    }

    public void deleteEmailPasswordPair(Long id) {
        emailPasswordPairRepository.deleteById(id);
    }
}
