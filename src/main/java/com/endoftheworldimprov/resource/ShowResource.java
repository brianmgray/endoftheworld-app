package com.endoftheworldimprov.resource;

import com.endoftheworldimprov.model.domain.ActivationStatus;
import com.endoftheworldimprov.model.domain.Ping;
import com.endoftheworldimprov.model.domain.Show;
import com.endoftheworldimprov.model.dto.IdResponseDto;
import com.endoftheworldimprov.model.dto.ShowListDto;
import com.endoftheworldimprov.service.api.IShowService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Resource for shows
 * User: bgray
 * Date: 8/2/13
 * Time: 10:46 PM
 */
@Path("/show")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShowResource {

//    @Autowired
    @Setter
    private IShowService showService;

    @GET
    @Path("/ping")
    public Ping ping() {
        return showService.ping();
    }

    @GET
    @Path("/all")
    public ShowListDto getAllShows() {
        return showService.getAllShows();
    }

    @GET
    @Path("/active/{code}")
    public Show isActive(@PathParam("code") String code) {
        return showService.findByCode(code);
    }

    @POST
    public IdResponseDto create(Show show) {
        Long id = showService.create(show);
        return new IdResponseDto(id);
    }

    @PUT
    @Path("/{key}/{activation}")
    public Response update(@PathParam("key") Long key, @PathParam("activation")ActivationStatus activationStatus) {
        showService.updateActivationStatus(key, activationStatus);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{key}")
    public Response delete(@PathParam("key") Long key) {
        showService.delete(key);
        return Response.ok().build();
    }

}
