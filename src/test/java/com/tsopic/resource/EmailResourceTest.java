package com.tsopic.resource;

import com.tsopic.service.MailService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class EmailResourceTest {

    @InjectMock
    MailService mailService;

    @Test
    void send_validRequest_returns200() {
        when(mailService.sendWelcomeEmail(anyString(), anyString()))
                .thenReturn(Uni.createFrom().voidItem());

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"email": "user@example.com", "username": "testuser"}
                        """)
                .when().post("/mail")
                .then()
                .statusCode(200);

        verify(mailService).sendWelcomeEmail("user@example.com", "testuser");
    }

    @Test
    void send_invalidEmailFormat_returns400() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"email": "not-a-valid-email", "username": "testuser"}
                        """)
                .when().post("/mail")
                .then()
                .statusCode(400);
    }

    @Test
    void send_blankEmail_returns400() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"email": "", "username": "testuser"}
                        """)
                .when().post("/mail")
                .then()
                .statusCode(400);
    }

    @Test
    void send_blankUsername_returns400() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"email": "user@example.com", "username": ""}
                        """)
                .when().post("/mail")
                .then()
                .statusCode(400);
    }

    @Test
    void send_usernameTooShort_returns400() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"email": "user@example.com", "username": "x"}
                        """)
                .when().post("/mail")
                .then()
                .statusCode(400);
    }

    @Test
    void send_missingFields_returns400() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when().post("/mail")
                .then()
                .statusCode(400);
    }

    @Test
    void send_serviceFailure_returns500() {
        when(mailService.sendWelcomeEmail(anyString(), anyString()))
                .thenReturn(Uni.createFrom().failure(new RuntimeException("SMTP connection refused")));

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"email": "user@example.com", "username": "testuser"}
                        """)
                .when().post("/mail")
                .then()
                .statusCode(500)
                .body(containsString("Failed to send email."));
    }
}
