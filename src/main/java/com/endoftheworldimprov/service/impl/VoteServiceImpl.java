package com.endoftheworldimprov.service.impl;

import com.endoftheworldimprov.JsonUtils;
import com.endoftheworldimprov.model.domain.ActivationStatus;
import com.endoftheworldimprov.model.domain.Show;
import com.endoftheworldimprov.model.domain.Vote;
import com.endoftheworldimprov.model.dto.VoteDto;
import com.endoftheworldimprov.model.dto.VotesForShow;
import com.endoftheworldimprov.service.api.IPubSubService;
import com.endoftheworldimprov.service.api.IShowService;
import com.endoftheworldimprov.service.api.IVoteService;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Service to deal with Votes
 * @author bgray
 **/
@Slf4j
@Transactional
public class VoteServiceImpl extends AbstractServiceImpl implements IVoteService {

    // IP used when the IP cannot be determined
    private static final String DEFAULT_IP = "default";

    @Setter
    private IShowService showService;

    @Autowired
    private IPubSubService pubSubService;

    @Value("${pubnub.voteChannel}")
    private String voteChannel;

    @Override
    public VotesForShow getVoteTotals(Long showKey) {
        log.debug("getVoteTotals");
        VotesForShow votes = new VotesForShow();
        votes.setShowKey(showKey);

        Map<String, Object> result = (Map<String, Object>) entityManager.createQuery(
                "SELECT new map( SUM(v.saveVote) as save, SUM(v.destroyVote) as destroy)" +
                "FROM Vote v " +
                "WHERE v.showKey = :showKey ")
            .setParameter("showKey", showKey)
            .getSingleResult();

        if (result.size() > 1) {
            votes.setSaveVotes(convertToInt(result, "save"));
            votes.setDestroyVotes(convertToInt(result, "destroy"));
        }
        return votes;
    }

    @Override
    public Long persist(VoteDto voteDto) {
        log.debug("create {}", voteDto);

        // check show exists and is active
        Show show = showService.find(voteDto.getShowKey());
        Preconditions.checkArgument(show != null && ActivationStatus.ACTIVE.equals(show.getActivationStatus()),
                "Show is invalid: {}", show);

        Vote toPersist = Vote.createVote(voteDto);
        Vote existingVote = getVoteByIp(voteDto.getIp());

        if (existingVote != null) {
            log.debug("Vote existed, updating");
            existingVote.setSaveVote(toPersist.getSaveVote());
            existingVote.setDestroyVote(toPersist.getDestroyVote());
            toPersist = existingVote;
        }

        Vote answer = entityManager.merge(toPersist);

        // notify pubnub
        pubSubService.publish(voteChannel, JsonUtils.convertToJson(show));
        return answer.getKey();
    }

    /**
     * Get an existing vote by IP
     * @param ip
     * @return vote if there is one, null otherwise
     */
    private Vote getVoteByIp(String ip) {
        if (!Strings.isNullOrEmpty(ip) && !DEFAULT_IP.equals(ip)) {
            List<Vote> votes = entityManager.createQuery("select v from Vote v where v.ip = :ip", Vote.class)
                    .setParameter("ip", ip).getResultList();
            return Iterables.getFirst(votes, null);
        }
        return null;
    }

    private int convertToInt(Map<String, Object> result, String key) {
        Long longValue = (Long) result.get(key);
        return longValue == null ? 0 : longValue.intValue();
    }
}
