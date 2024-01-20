package com.webapp.FinTurn.service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;

public interface EmailService {

    void sendNewPasswordEmail(String firstName, String password, String email) throws MessagingException;

    void sendRegisterEmail(String firstName, String email) throws MessagingException;

    void sendAddedUserEmail(String firstName, String password, String email) throws MessagingException;

    void sendEmail(String email, String subject, String text) throws MessagingException;

    Message createEmail(String firstName, String password, String email) throws MessagingException;

    Session getEmailSession();
}
