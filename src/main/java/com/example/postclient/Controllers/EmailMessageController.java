package com.example.postclient.Controllers;

import com.example.postclient.Config.AppUserDetails;
import com.example.postclient.Models.EmailMessage;
import com.example.postclient.Service.EmailMessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;;
import java.util.List;


@RestController
@RequestMapping("/api/mails")
@AllArgsConstructor
public class EmailMessageController {

    private final EmailMessageService emailMessageService;


    @GetMapping("/messages/{id}")
    public ResponseEntity<List<EmailMessage>> getEmailMessages(@AuthenticationPrincipal AppUserDetails userDetails, @PathVariable Long id) {
        return emailMessageService.getEmailMessages(userDetails, id);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<EmailMessage>> getAllEmailMessages(@AuthenticationPrincipal AppUserDetails userDetails) {
        return emailMessageService.getAllEmailMessages(userDetails);
    }

    @PostMapping("/send-email/{id}")
    public ResponseEntity<String> sendEmail(@AuthenticationPrincipal AppUserDetails userDetails, @PathVariable Long id, @RequestBody EmailMessage emailMessage) {
        return emailMessageService.sendEmail(userDetails, id, emailMessage);
    }

    @DeleteMapping("/messages/{id}/{mail_id}")
    public ResponseEntity<String> deleteEmailMessage(@AuthenticationPrincipal AppUserDetails userDetails, @PathVariable Long id, @PathVariable Long mail_id) {
        return emailMessageService.deleteEmailMessage(userDetails, id, mail_id);
    }

    @PutMapping("/messages/{id}/{mail_id}")
    public ResponseEntity<String> updateEmailMessage(@AuthenticationPrincipal AppUserDetails userDetails, @PathVariable Long id, @PathVariable Long mail_id) {
        return emailMessageService.updateEmailMessage(userDetails, id, mail_id);
    }
}