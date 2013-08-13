package com.endoftheworldimprov.service.api;

import com.endoftheworldimprov.model.domain.ActivationStatus;
import com.endoftheworldimprov.model.domain.Ping;
import com.endoftheworldimprov.model.domain.Show;
import com.endoftheworldimprov.model.dto.ShowListDto;

/**
 * Service to deal with shows
 * @author bgray
 **/
public interface IShowService {

    /**
     * Verify the service is up and running
     * @return
     */
    Ping ping();

    /**
     * List all shows
     * @return
     */
    ShowListDto getAllShows();

    /**
     * Create a new show
     * @param show
     * @return ID of the created show
     */
    Long create(Show show);

    /**
     * Update the activation status of an existing show
     * @param showKey
     * @param activationStatus
     */
    void updateActivationStatus(Long showKey, ActivationStatus activationStatus);

    /**
     * Delete the show
     * @param key
     */
    void delete(Long key);

    /**
     * Find a show by key
     * @param key
     */
    Show find(Long key);

    /**
     * Find a show by code
     * @param code
     * @return
     */
    Show findByCode(String code);
}
