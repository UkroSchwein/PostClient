package com.example.postclient.Service;

import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

public class EmailConfigurationUtil {

    public static Store connectToEmailServer(String email, String password) throws Exception {
        String domain = extractDomain(email);

        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getInstance(props, null);
        Store store = session.getStore();

        switch (domain) {
            case "mail.ru":
                store.connect("imap.mail.ru", email, password);
                break;
            case "gmail.com":
                store.connect("imap.gmail.com", email, password);
                break;
            default:
                throw new IllegalArgumentException("Unsupported email domain: " + domain);
        }

        return store;
    }

    public static Properties setPropToSMTP(String email){
        try {
        String domain = extractDomain(email);


        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        switch (domain) {
            case "mail.ru":
                props.put("mail.smtp.host", "smtp.mail.ru");
                props.put("mail.smtp.port", "587");
                break;
            case "gmail.com":
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                break;
            default:
                throw new IllegalArgumentException("Unsupported email domain: " + domain);
        }
        return props;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка установки свойств для SMTP", e);
        }
    }

    private static String extractDomain(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex != -1) {
            return email.substring(atIndex + 1);
        }
        throw new IllegalArgumentException("Invalid email address: " + email);
    }
}
