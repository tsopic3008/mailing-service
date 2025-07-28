package com.tsopic.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MailService {

    @Inject
    ReactiveMailer mailer;

    public Uni<Void> sendWelcomeEmail(String to, String username) {
        Mail mail = Mail.withText(
                to,
                "Dobrodošli " + username,
                "Pozdrav " + username + ",\n\nVaša registracija je uspješna!\n\nHvala što koristite naš sustav."
        );

        mail.setHtml("<p>Pozdrav <b>" + username + "</b>,</p><p>Vaša registracija je uspješna!</p><p>Hvala!</p>");
        return mailer.send(mail);
    }

}


