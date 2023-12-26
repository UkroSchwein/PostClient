package com.example.postclient.Controllers;

import com.example.postclient.Models.EmailPasswordPair;
import com.example.postclient.Service.EmailPasswordPairService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
public class EmailPasswordPairController {

    private final EmailPasswordPairService emailPasswordPairService;

    @PostMapping
    public ResponseEntity<EmailPasswordPair> createEmailPasswordPair(@RequestBody EmailPasswordPair emailPasswordPair) {
        EmailPasswordPair createdPair = emailPasswordPairService.saveEmailPasswordPair(emailPasswordPair);
        return new ResponseEntity<>(createdPair, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailPasswordPair> getEmailPasswordPairById(@PathVariable Long id) {
        EmailPasswordPair pair = emailPasswordPairService.getEmailPasswordPairById(id);
        return pair != null ?
                new ResponseEntity<>(pair, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<EmailPasswordPair>> getAllEmailPasswordPairs() {
        List<EmailPasswordPair> pairs = emailPasswordPairService.getAllEmailPasswordPairs();
        return new ResponseEntity<>(pairs, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmailPasswordPair(@PathVariable Long id) {
        emailPasswordPairService.deleteEmailPasswordPair(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmailPasswordPair> updateEmailPasswordPair(@PathVariable Long id, @RequestBody EmailPasswordPair updatedPair) {
        EmailPasswordPair pair = emailPasswordPairService.updateEmailPasswordPair(id, updatedPair);
        return pair != null ?
                new ResponseEntity<>(pair, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
