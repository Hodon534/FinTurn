package com.webapp.FinTurn.service.impl;

import static com.webapp.FinTurn.constant.EmailConstant.*;

import com.sun.mail.smtp.SMTPTransport;
import com.webapp.FinTurn.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendNewPasswordEmail(String username, String password, String email) throws MessagingException {
        String text = EMAIL_NEW_PASSWORD_TEXT + password + EMAIL_SPACING_ABOVE_SIGNATURE + EMAIL_SIGNATURE;
        this.sendEmail(email, EMAIL_SUBJECT_NEW_PASSWORD, text);
    }

    @Override
    public void sendRegisterEmail(String username, String email) throws MessagingException {
        String text = EMAIL_REGISTERED_TEXT + EMAIL_SPACING_ABOVE_SIGNATURE + EMAIL_SIGNATURE;
        this.sendEmail(email, EMAIL_SUBJECT_REGISTERED_ACCOUNT, text);
    }

    @Override
    public void sendAddedUserEmail(String username, String password, String email) throws MessagingException {
        String text = EMAIL_NEW_ACCOUNT_ADDED_TEXT + password + EMAIL_SPACING_ABOVE_SIGNATURE + EMAIL_SIGNATURE;
        this.sendEmail(email, EMAIL_NEW_ACCOUNT_ADDED_TEXT, text);
    }

    @Override
    public void sendEmail(String email, String subject, String text) throws MessagingException {
        Message message = createEmail(email, subject, text);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        log.info("Email has been sent");
        smtpTransport.close();
    }

    @Override
    public Message createEmail(String email, String subject, String text) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(subject);
        message.setText(text);
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    @Override
    public Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLE, true);
        properties.put(SMTP_STARTTLS_REQUIRED, true);
        return Session.getInstance(properties, null);
    }
}
