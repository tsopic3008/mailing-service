package com.tsopic.service;

import io.quarkus.logging.Log;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MailService {

    private final ReactiveMailer mailer;

    @Inject
    public MailService(ReactiveMailer mailer) {
        this.mailer = mailer;
    }

    public Uni<Void> sendWelcomeEmail(String to, String username) {
        Log.debugf("Preparing welcome email for: %s", to);

        var mail = Mail.withText(to, "Dobrodošli " + username, buildTextBody(username));
        mail.setHtml(buildHtmlBody(username));

        return mailer.send(mail)
                .invoke(() -> Log.infof("Welcome email successfully sent to: %s", to));
    }

    private String buildTextBody(String username) {
        return """
                Pozdrav %s,

                Vaša registracija je uspješna!

                Hvala što koristite naš sustav.""".formatted(username);
    }

    private String buildHtmlBody(String username) {
        return """
                <p>Pozdrav <b>%s</b>,</p>
                <p>Vaša registracija je uspješna!</p>
                <p>Hvala što koristite naš sustav.</p>""".formatted(username);
    }
}
