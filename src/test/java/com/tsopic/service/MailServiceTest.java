package com.tsopic.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    ReactiveMailer mailer;

    MailService mailService;

    @BeforeEach
    void setUp() {
        mailService = new MailService(mailer);
    }

    @Test
    void sendWelcomeEmail_sendsMailWithCorrectRecipientSubjectAndBody() {
        when(mailer.send(any(Mail.class))).thenReturn(Uni.createFrom().voidItem());

        mailService.sendWelcomeEmail("user@example.com", "TestUser")
                .await().indefinitely();

        ArgumentCaptor<Mail> captor = ArgumentCaptor.forClass(Mail.class);
        verify(mailer).send(captor.capture());

        Mail sent = captor.getValue();
        assertTrue(sent.getTo().contains("user@example.com"), "Wrong recipient");
        assertTrue(sent.getSubject().contains("TestUser"), "Subject should contain username");
        assertTrue(sent.getText().contains("TestUser"), "Text body should contain username");
        assertTrue(sent.getHtml().contains("TestUser"), "HTML body should contain username");
    }

    @Test
    void sendWelcomeEmail_htmlBodyContainsExpectedStructure() {
        when(mailer.send(any(Mail.class))).thenReturn(Uni.createFrom().voidItem());

        mailService.sendWelcomeEmail("user@example.com", "Ana")
                .await().indefinitely();

        ArgumentCaptor<Mail> captor = ArgumentCaptor.forClass(Mail.class);
        verify(mailer).send(captor.capture());

        Mail sent = captor.getValue();
        assertTrue(sent.getHtml().contains("<b>Ana</b>"), "HTML body should bold the username");
        assertTrue(sent.getText().contains("Vaša registracija je uspješna"), "Text body should confirm registration");
    }

    @Test
    void sendWelcomeEmail_propagatesFailure_whenMailerThrows() {
        when(mailer.send(any(Mail.class)))
                .thenReturn(Uni.createFrom().failure(new RuntimeException("SMTP connection refused")));

        assertThrows(RuntimeException.class, () ->
                mailService.sendWelcomeEmail("user@example.com", "TestUser")
                        .await().indefinitely()
        );
    }
}
