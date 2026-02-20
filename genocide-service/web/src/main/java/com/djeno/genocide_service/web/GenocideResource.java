package com.djeno.genocide_service.web;

import com.djeno.genocide_service.ejb.CityNotFoundException;
import com.djeno.genocide_service.ejb.GenocideServiceRemote;
import com.djeno.genocide_service.web.dto.ErrorResponse;
import com.djeno.genocide_service.web.dto.ResponseMessage;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JAX-RS ресурс для операций геноцида.
 * Делегирует бизнес-логику EJB-компоненту.
 */
@Path("/genocide")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GenocideResource {

    private static final Logger LOGGER = Logger.getLogger(GenocideResource.class.getName());

    @EJB
    private GenocideServiceRemote genocideService;

    @POST
    @Path("/kill/{id}")
    public Response killPopulationOfCity(@PathParam("id") int id) {
        LOGGER.info("Received request to kill population of city: " + id);
        
        try {
            genocideService.killPopulationOfCity(id);
            return Response.ok(new ResponseMessage("Население города уничтожено")).build();
        } catch (CityNotFoundException e) {
            LOGGER.log(Level.WARNING, "City not found: " + id, e);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("City not found", e.getMessage()))
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error killing population: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal error", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/move-to-poorest/{id}")
    public Response movePopulationToPoorestCity(@PathParam("id") int id) {
        LOGGER.info("Received request to move population from city: " + id);
        
        try {
            int targetCityId = genocideService.movePopulationToPoorestCity(id);
            
            if (targetCityId == id) {
                return Response.ok(new ResponseMessage(
                        "Переселение невозможно: нет города с уровнем жизни не выше текущего. Население остаётся в городе с id: " + id
                )).build();
            }
            
            return Response.ok(new ResponseMessage(
                    "Население города с id " + id + " переселено в город с id: " + targetCityId
            )).build();
        } catch (CityNotFoundException e) {
            LOGGER.log(Level.WARNING, "City not found: " + id, e);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("City not found", e.getMessage()))
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error moving population: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal error", e.getMessage()))
                    .build();
        }
    }
}
