package org.infinity.passport.service;

import org.infinity.passport.domain.User;

public interface MailService {

    void sendActivationEmail(User user, String baseUrl);

    void sendCreationEmail(User user, String baseUrl);

    void sendPasswordResetMail(User user, String baseUrl);
}
