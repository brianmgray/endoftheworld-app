package com.endoftheworldimprov.service.impl;

import com.endoftheworldimprov.JsonUtils;
import com.endoftheworldimprov.model.domain.ActivationStatus;
import com.endoftheworldimprov.model.domain.Show;
import com.endoftheworldimprov.model.dto.ShowListDto;
import com.endoftheworldimprov.service.api.IPubSubService;
import com.endoftheworldimprov.service.api.IShowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Service to deal with Shows
 * @author bgray
 **/
@Slf4j
@Transactional
//@Service("showService")
public class ShowServiceImpl extends AbstractServiceImpl implements IShowService {

    @Autowired
    private IPubSubService pubSubService;

    @Value("${pubnub.showChannel}")
    private String showChannel;

    @Override
    public ShowListDto getAllShows() {
        log.debug("getAllShows");
        List<Show> shows =  entityManager.createQuery("select s from Show s order by s.date desc",
                Show.class).getResultList();
        return new ShowListDto(shows);
    }

    @Override
    public Long create(Show show) {
        log.debug("create: {}", show);
        checkCodeDoesntExist(show.getCode());
        Show answer = entityManager.merge(show);
        return answer.getKey();
    }

    @Override
    public void updateActivationStatus(Long showKey, ActivationStatus activationStatus) {
        log.debug("updateActivationStatus: {}, {}", showKey, activationStatus);
        Show show = entityManager.find(Show.class, showKey);
        checkArgument(show != null, "Show does not exist with the given key");
        show.setActivationStatus(activationStatus);
        entityManager.merge(show);

        // publish the show update to the client
        pubSubService.publish(showChannel, JsonUtils.convertToJson(show));
    }

    @Override
    public void delete(Long key) {
        log.debug("delete: {}", key);
        Show show = entityManager.find(Show.class, key);
        if (show != null) {
            entityManager.remove(show);
        }
    }

    @Override
    public Show find(Long key) {
        return entityManager.find(Show.class, key);
    }

    @Override
    public Show findByCode(String code) {
        try {
            Show show = entityManager.createQuery("select s from Show s where s.code = :code", Show.class)
                    .setParameter("code", code)
                    .getSingleResult();
            return show;
        } catch (NoResultException e) {
            throw new IllegalArgumentException("Code " + code + " does not map to a show");
        }
    }

    /**
     * Check the code doesn't already exist
     * @param code
     */
    private void checkCodeDoesntExist(String code) {
        Long count = (Long) entityManager.createQuery("select count(*) from Show s where s.code = :code")
                .setParameter("code", code).getSingleResult();
        checkArgument(count == 0, "Code already exists");
    }
}
