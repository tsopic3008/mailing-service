package com.tsopic.resource;

import com.tsopic.dto.MailRequest;
import com.tsopic.service.MailService;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/mail")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EmailResource {

    private final MailService mailService;

    @Inject
    public EmailResource(MailService mailService) {
        this.mailService = mailService;
    }

    @POST
    public Uni<Response> send(@Valid MailRequest request) {
        return mailService.sendWelcomeEmail(request.email(), request.username())
                .onItem().transform(ignored -> Response.ok().build())
                .onFailure().recoverWithItem(ex -> {
                    Log.errorf(ex, "Failed to send email to %s", request.email());
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Failed to send email.")
                            .build();
                });
    }
}
