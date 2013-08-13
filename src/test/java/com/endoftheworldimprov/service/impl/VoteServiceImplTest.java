package com.endoftheworldimprov.service.impl;

import com.endoftheworldimprov.model.domain.ActivationStatus;
import com.endoftheworldimprov.model.domain.Show;
import com.endoftheworldimprov.model.domain.Vote;
import com.endoftheworldimprov.model.domain.VoteValue;
import com.endoftheworldimprov.model.dto.VoteDto;
import com.endoftheworldimprov.model.dto.VotesForShow;
import com.endoftheworldimprov.service.api.IVoteService;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/** @author bgray */
@ContextConfiguration("/applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class VoteServiceImplTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private IVoteService voteService;

    private Vote testVote;
    private Show testShow1;
    private Show testShow2;
    private Show testShow3;

    @Before
    public void setUp() {
        assertNotNull(entityManager);
        seedData();
    }

    @Test
    public void testSumVotesForShow() throws Exception {
        {
            VotesForShow totals = voteService.getVoteTotals(testShow1.getKey());
            assertThat(totals.getShowKey(), equalTo(testShow1.getKey()));
            assertThat(totals.getSaveVotes(), equalTo(2));
            assertThat(totals.getDestroyVotes(), equalTo(1));
        }
        {
            VotesForShow totals = voteService.getVoteTotals(testShow2.getKey());
            assertThat(totals.getShowKey(), equalTo(testShow2.getKey()));
            assertThat(totals.getSaveVotes(), equalTo(1));
            assertThat(totals.getDestroyVotes(), equalTo(0));
        }
    }

    @Test
    public void testSumVotesForShow_noVotes_worksFine() throws Exception {
        VotesForShow totals = voteService.getVoteTotals(testShow3.getKey());
        assertThat(totals.getSaveVotes(), equalTo(0));
        assertThat(totals.getDestroyVotes(), equalTo(0));
    }

    @Test
    public void testCreate() throws Exception {
        VoteDto vote = new VoteDto(testShow1.getKey(), "test.ip.100", VoteValue.DESTROY);
        Long key = voteService.persist(vote);

        log.debug("Created show with key: {}", key);

        assertThat(key, Matchers.greaterThan(0L));
        Vote result = getVoteBy(key);

        assertNotNull(result);
        assertThat(result.getDestroyVote(), equalTo(1));
        assertThat(result.getSaveVote(), equalTo(0));
    }

    @Test
    public void testCreate_duplicateIp_doesUpdate() throws Exception {
        VoteDto vote = new VoteDto(testShow1.getKey(), "test.ip1", VoteValue.DESTROY);
        Long key = voteService.persist(vote);

        log.debug("Created show with key: {}", key);

        assertThat(key, equalTo(testVote.getKey()));
        Vote result = getVoteBy(key);

        assertNotNull(result);
        assertThat(result.getDestroyVote(), equalTo(1));
        assertThat(result.getSaveVote(), equalTo(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_inactiveShow_throwsException() throws Exception {
        VoteDto vote = new VoteDto(testShow3.getKey(), "test.ip1", VoteValue.DESTROY);
        voteService.persist(vote);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void seedData() {
        seedShows();

        // votes for show 1
        Vote vote = Vote.createSaveVote(testShow1.getKey(), "test.ip1");
        testVote = entityManager.merge(vote);
        entityManager.persist(Vote.createSaveVote(testShow1.getKey(), "test.ip2"));
        entityManager.persist(Vote.createDestroyVote(testShow1.getKey(), "test.ip3"));

        // votes for show 2
        entityManager.persist(Vote.createSaveVote(testShow2.getKey(), "test.ip1"));
    }

    private void seedShows() {
        Show show1 = new Show(new Date(), "abc", ActivationStatus.ACTIVE);
        testShow1 = entityManager.merge(show1);

        Show show2 = new Show(new Date(), "123", ActivationStatus.ACTIVE);
        testShow2 = entityManager.merge(show2);

        Show show3 = new Show(new Date(), "inactive", ActivationStatus.INACTIVE);
        testShow3 = entityManager.merge(show3);
    }

    private Vote getVoteBy(Long key) {
        return entityManager.find(Vote.class, key);
    }
}
