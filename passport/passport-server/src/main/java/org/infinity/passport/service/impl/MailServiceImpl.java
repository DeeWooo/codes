package org.infinity.passport.service.impl;

import java.util.Locale;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.CharEncoding;
import org.infinity.passport.domain.User;
import org.infinity.passport.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

/**
 * Service for sending emails.
 */
@Service
public class MailServiceImpl implements MailService {

    private static final Logger  LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);

    @Autowired
    private MailProperties       mailProperties;

    @Autowired
    private JavaMailSenderImpl   javaMailSender;

    @Autowired
    private MessageSource        messageSource;

    @Autowired
    private SpringTemplateEngine templateEngine;

    /**
     * System default email address that sends the e-mails.
     */
    // private String from;

    @Async
    private void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        LOGGER.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart,
                isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(mailProperties.getUsername());
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            LOGGER.debug("Sent e-mail to User '{}'", to);
        } catch (Exception e) {
            LOGGER.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendActivationEmail(User user, String baseUrl) {
        LOGGER.debug("Sending activation e-mail to '{}'", user.getEmail());
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        Context context = new Context(locale);
        context.setVariable("user", user);
        context.setVariable("baseUrl", baseUrl);
        String content = templateEngine.process("/email/activation-email", context);
        String subject = messageSource.getMessage("email.activation.title", null, locale);
        sendEmail(user.getEmail(), subject, content, false, true);
    }

    @Async
    public void sendCreationEmail(User user, String baseUrl) {
        LOGGER.debug("Sending creation e-mail to '{}'", user.getEmail());
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        Context context = new Context(locale);
        context.setVariable("user", user);
        context.setVariable("baseUrl", baseUrl);
        String content = templateEngine.process("/email/creation-email", context);
        String subject = messageSource.getMessage("email.activation.title", null, locale);
        sendEmail(user.getEmail(), subject, content, false, true);
    }

    @Async
    public void sendPasswordResetMail(User user, String baseUrl) {
        LOGGER.debug("Sending password reset e-mail to '{}'", user.getEmail());
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        Context context = new Context(locale);
        context.setVariable("user", user);
        context.setVariable("baseUrl", baseUrl);
        String content = templateEngine.process("/email/password-reset-email", context);
        String subject = messageSource.getMessage("email.reset.title", null, locale);
        sendEmail(user.getEmail(), subject, content, false, true);
    }
}
