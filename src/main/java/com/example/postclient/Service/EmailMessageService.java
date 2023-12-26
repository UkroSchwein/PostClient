package com.example.postclient.Service;

import com.example.postclient.Config.AppUserDetails;
import com.example.postclient.Models.EmailMessage;
import com.example.postclient.Models.EmailPasswordPair;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static com.example.postclient.Service.EmailConfigurationUtil.connectToEmailServer;
import static com.example.postclient.Service.EmailConfigurationUtil.setPropToSMTP;

@Service
@AllArgsConstructor
public class EmailMessageService {

    private AppUserService appUserService;

    public ResponseEntity<List<EmailMessage>> getEmailMessages(AppUserDetails userDetails, Long id) {
        String email;
        String password;
        Optional<EmailPasswordPair> emailPasswordPairOptional = appUserService.getEmailPasswordPairById(userDetails.getUsername(), id);
        if (emailPasswordPairOptional.isPresent()) {
            EmailPasswordPair emailPasswordPair = emailPasswordPairOptional.get();
            email = emailPasswordPair.getEmail();
            password = emailPasswordPair.getPassword();
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            Store store = connectToEmailServer(email, password);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            System.out.println("COUNT" + inbox.getMessageCount());

            Message[] messages = inbox.getMessages();


            List<EmailMessage> emailMessages = new ArrayList<>();
            for (Message message : messages) {
                try {
                    Object content = message.getContent();

                    if (content instanceof Multipart) {
                        Multipart multipart = (Multipart) content;
                        EmailMessage emailMessage = new EmailMessage();
                        emailMessage.setTo(email);
                        emailMessage.setSubject(message.getSubject());
                        emailMessage.setFrom(decodeText(message.getFrom()[0].toString()));
                        for (int i = 0; i < multipart.getCount(); i++) {
                            BodyPart body = multipart.getBodyPart(i);

                            if (body.getContentType().startsWith("TEXT/HTML") || body.getContentType().startsWith("text/html")) {
                                emailMessage.setText(body.getContent().toString());
                                emailMessages.add(emailMessage);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            inbox.close(false);
            store.close();

            return new ResponseEntity<>(emailMessages, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<EmailMessage>> getAllEmailMessages(AppUserDetails userDetails) {
        Optional<List<EmailPasswordPair>> emailPasswordPairsOptional = appUserService.getEmailPasswordPairsByUsername(userDetails.getUsername());
        String email;
        String password;
        if (emailPasswordPairsOptional.isPresent()) {
            List<EmailPasswordPair> emailPasswordPairs = emailPasswordPairsOptional.get();
            List<EmailMessage> allEmailMessages = new ArrayList<>();

            for (EmailPasswordPair emailPasswordPair : emailPasswordPairs) {
                email = emailPasswordPair.getEmail();
                password = emailPasswordPair.getPassword();

                try {
                    Store store = connectToEmailServer(email, password);
                    Folder inbox = store.getFolder("INBOX");
                    inbox.open(Folder.READ_ONLY);

                    Message[] messages = inbox.getMessages();
                    List<EmailMessage> emailMessages = new ArrayList<>();

                    for (Message message : messages) {
                        try {
                            Object content = message.getContent();
                            EmailMessage emailMessage = new EmailMessage();

                            if (content instanceof Multipart) {
                                Multipart multipart = (Multipart) content;
                                emailMessage.setTo(email);
                                emailMessage.setSubject(message.getSubject());
                                emailMessage.setFrom(decodeText(message.getFrom()[0].toString()));
                                for (int i = 0; i < multipart.getCount(); i++) {
                                    BodyPart body = multipart.getBodyPart(i);

                                    if (body.getContentType().startsWith("TEXT/HTML") || body.getContentType().startsWith("text/html")) {
                                        emailMessage.setText(body.getContent().toString());
                                        System.out.println(body.getContent().toString());
                                        emailMessages.add(emailMessage);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    inbox.close(false);
                    store.close();

                    allEmailMessages.addAll(emailMessages);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return new ResponseEntity<>(allEmailMessages, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<String> sendEmail(AppUserDetails userDetails, Long id, EmailMessage emailMessage) {
        String email;
        String password;
        Optional<EmailPasswordPair> emailPasswordPairOptional = appUserService.getEmailPasswordPairById(userDetails.getUsername(), id);

        if (emailPasswordPairOptional.isPresent()) {
            EmailPasswordPair emailPasswordPair = emailPasswordPairOptional.get();
            email = emailPasswordPair.getEmail();
            password = emailPasswordPair.getPassword();
        } else {
            return new ResponseEntity<>("Email и/или пароль не найдены.", HttpStatus.NOT_FOUND);
        }

        Properties props = setPropToSMTP(email);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailMessage.getTo()));
            message.setSubject(emailMessage.getSubject());
            message.setText(emailMessage.getText());


//            MimeBodyPart msgPart = new MimeBodyPart();
//            MimeMultipart multipart = new MimeMultipart();
//            multipart.addBodyPart(msgPart);
//
//            msgPart.setText(emailMessage.getText(), "UTF-8");
//            msgPart.setHeader("Content-Type", "text/html; charset=\"utf-8\"");
//            message.setContent(multipart);
//            message.saveChanges();


            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(emailMessage.getText(), "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);


            Transport.send(message);
            return new ResponseEntity<>("Письмо отправлено успешно.", HttpStatus.OK);
        } catch (MessagingException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Ошибка при отправке письма.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> deleteEmailMessage(AppUserDetails userDetails, Long id, Long mailId) {
        String email;
        String password;
        Optional<EmailPasswordPair> emailPasswordPairOptional = appUserService.getEmailPasswordPairById(userDetails.getUsername(), id);

        if (emailPasswordPairOptional.isPresent()) {
            EmailPasswordPair emailPasswordPair = emailPasswordPairOptional.get();
            email = emailPasswordPair.getEmail();
            password = emailPasswordPair.getPassword();
        } else {
            return new ResponseEntity<>("Email и/или пароль не найдены.", HttpStatus.NOT_FOUND);
        }

        try {
            Store store = connectToEmailServer(email, password);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            Message[] messages = inbox.getMessages();
            for (Message message : messages) {
                if (message.getMessageNumber() == mailId) {
                    message.setFlag(Flags.Flag.DELETED, true);

                    inbox.expunge();
                    inbox.close(true);
                    store.close();

                    return new ResponseEntity<>("Письмо успешно удалено.", HttpStatus.OK);
                }
            }

            inbox.close(false);
            store.close();

            return new ResponseEntity<>("Письмо с указанным идентификатором не найдено.", HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Ошибка при удалении письма.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> updateEmailMessage(AppUserDetails userDetails, Long id, Long mailId) {
        String email;
        String password;
        Optional<EmailPasswordPair> emailPasswordPairOptional = appUserService.getEmailPasswordPairById(userDetails.getUsername(), id);

        if (emailPasswordPairOptional.isPresent()) {
            EmailPasswordPair emailPasswordPair = emailPasswordPairOptional.get();
            email = emailPasswordPair.getEmail();
            password = emailPasswordPair.getPassword();
        } else {
            return new ResponseEntity<>("Email и/или пароль не найдены.", HttpStatus.NOT_FOUND);
        }

        try {
            Store store = connectToEmailServer(email, password);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            Message[] messages = inbox.getMessages();
            for (Message message : messages) {
                if (message.getMessageNumber() == mailId) {
                    Flags flags = new Flags(Flags.Flag.SEEN);
                    message.setFlags(flags, true);
                    inbox.close(true);
                    store.close();

                    return new ResponseEntity<>("Письмо успешно обновлено.", HttpStatus.OK);
                }
            }
            inbox.close(false);
            store.close();

            return new ResponseEntity<>("Письмо с указанным идентификатором не найдено.", HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Ошибка при обновлении письма.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    private String decodeText(String text) throws UnsupportedEncodingException {
        return MimeUtility.decodeText(text);
    }
}