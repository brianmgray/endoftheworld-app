package com.endoftheworldimprov.service.impl;

import com.endoftheworldimprov.model.domain.ActivationStatus;
import com.endoftheworldimprov.model.domain.Ping;
import com.endoftheworldimprov.model.domain.Show;
import com.endoftheworldimprov.model.dto.ShowListDto;
import com.endoftheworldimprov.service.api.IShowService;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/** @author bgray */
@ContextConfiguration("/applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@Ignore
public class ShowServiceImplTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private IShowService showService;

    private Long testId;

    @Before
    public void setUp() {
        assertNotNull(entityManager);
        seedData();
    }

    @Test
    public void testPing() {
        Ping ping = showService.ping();
        assertThat(ping.getTime(), CoreMatchers.<String>notNullValue());
    }

    @Test
    public void testGetAllShows() throws Exception {
        ShowListDto shows = showService.getAllShows();
        assertThat(shows.getShows().size(), equalTo(2));
    }

    @Test
    public void testCreate() throws Exception {
        Show show = new Show(new Date(), "abc", ActivationStatus.INACTIVE);

        // create
        Long key = showService.create(show);
        log.debug("Created show with key: {}", key);

        assertThat(key, greaterThan(0L));
        Show result = getShowById(key);

        assertNotNull(result);
        assertThat(result.getCode(), equalTo("abc"));
    }

    @Test
    public void testUpdateActivationStatus() throws Exception {
        showService.updateActivationStatus(testId, ActivationStatus.ACTIVE);

        Show show = getShowById(testId);
        assertThat(show.getActivationStatus(), equalTo(ActivationStatus.ACTIVE));
    }

    @Test
    public void testDelete() throws Exception {
        Show show = getShowById(testId);
        assertThat(show, notNullValue());

        showService.delete(testId);

        Object count = entityManager.createQuery("select count(s) from Show s where s.key = key")
                .setParameter("key", testId).getSingleResult();
        assertThat((Long) count, equalTo(0L));
    }

    @Test
    public void testFindByCode() throws Exception {
        Show status = showService.findByCode("abc");
        assertThat(status.getActivationStatus(), equalTo(ActivationStatus.INACTIVE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindByCode_invalidCode_throwsException() throws Exception {
        showService.findByCode("-----");
    }

    private void seedData() {
        // 1st show
        Show show = new Show(new Date(), "abc", ActivationStatus.INACTIVE);
        entityManager.persist(show);
        testId = show.getKey();

        // 2nd show
        entityManager.persist(new Show(new Date(), "123", ActivationStatus.ACTIVE));
    }

    private Show getShowById(Long key) {
        return entityManager.find(Show.class, key);
    }
}
