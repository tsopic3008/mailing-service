package com.tsopic.resource;


import com.tsopic.service.MailService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/mail")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EmailResource {

    @Inject
    MailService mailService;

    @POST
    public Uni<Response> send(MailRequest request) {
        return mailService.sendWelcomeEmail(request.email, request.username)
                .onItem().transform(v -> {
                    System.out.println("Email successfully sent to " + request.email);
                    return Response.ok().build();
                })
                .onFailure().recoverWithItem(e -> {
                    System.err.println("Failed to send email: " + e.getMessage());
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Failed to send email.")
                            .build();
                });
    }

    public static class MailRequest {
        public String email;
        public String username;
    }
}

