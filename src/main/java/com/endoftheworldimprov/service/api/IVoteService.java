package com.endoftheworldimprov.service.api;

import com.endoftheworldimprov.model.domain.Ping;
import com.endoftheworldimprov.model.dto.VoteDto;
import com.endoftheworldimprov.model.dto.VotesForShow;

/**
 * Service to deal with votes
 * @author bgray
 **/
public interface IVoteService {

    /**
     * Verify the service is up and running
     * @return
     */
    Ping ping();

    /**
     * Sum all the votes for the given show
     * @param showKey
     * @return
     */
    VotesForShow getVoteTotals(Long showKey);

    /**
     * Create a new vote
     *
     * @param vote
     * @return
     */
    Long persist(VoteDto vote);
}

