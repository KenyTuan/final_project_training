package com.test.finalproject.service;

import jakarta.mail.MessagingException;


public interface MailService {

    void sendMail(String to, String subject, String body);
    void sendHtmlEmail(String to, String subject) throws MessagingException;
    void sendAttachmentsEmail(String to, String subject, String body) throws MessagingException;
}
