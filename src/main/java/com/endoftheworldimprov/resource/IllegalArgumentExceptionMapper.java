package com.endoftheworldimprov.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Map IllegalArgumentExceptions to HTTP responses
 * @author bgray
 **/
@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException>  {
    @Override
    public Response toResponse(IllegalArgumentException ex) {
        return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).type("text/plain")
                .build();
    }

}
