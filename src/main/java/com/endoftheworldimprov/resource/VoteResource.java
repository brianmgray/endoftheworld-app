package com.endoftheworldimprov.resource;

import com.endoftheworldimprov.model.domain.Ping;
import com.endoftheworldimprov.model.dto.IdResponseDto;
import com.endoftheworldimprov.model.dto.VoteDto;
import com.endoftheworldimprov.model.dto.VotesForShow;
import com.endoftheworldimprov.service.api.IVoteService;
import lombok.Setter;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Resource for votes
 * User: bgray
 * Date: 8/2/13
 * Time: 10:46 PM
 */
@Path("/vote")
@Produces("application/json")
public class VoteResource {

    @Setter
    private IVoteService voteService;

    @GET
    @Path("/ping")
    public Ping ping() {
        return voteService.ping();
    }

    @GET
    @Path("/forShow/{showKey}")
    public VotesForShow votesForShow(@PathParam("showKey") Long showKey) {
        return voteService.getVoteTotals(showKey);
    }

    @POST
    public IdResponseDto vote(VoteDto vote) {
        Long id = voteService.persist(vote);
        return new IdResponseDto(id);
    }

}
