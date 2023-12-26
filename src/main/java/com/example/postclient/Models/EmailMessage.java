package com.example.postclient.Models;

import lombok.Data;

@Data
public class EmailMessage {
    private String to;
    private String subject;
    private String from;
    private String text;

}

