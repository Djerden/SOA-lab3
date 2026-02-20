package com.djeno.genocide_service.web;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Health check endpoint для совместимости с actuator.
 */
@Path("/actuator")
@Produces(MediaType.APPLICATION_JSON)
public class HealthResource {

    @GET
    @Path("/health")
    public Response health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        return Response.ok(status).build();
    }

    @GET
    @Path("/info")
    public Response info() {
        Map<String, Object> info = new HashMap<>();
        info.put("app", "genocide-service");
        info.put("version", "0.0.1-SNAPSHOT");
        return Response.ok(info).build();
    }
}
