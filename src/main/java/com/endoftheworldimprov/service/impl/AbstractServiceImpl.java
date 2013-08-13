package com.endoftheworldimprov.service.impl;

import com.endoftheworldimprov.model.domain.Ping;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Base class for services
 * @author bgray
 * */
public class AbstractServiceImpl {

    @PersistenceContext
    protected EntityManager entityManager;

    public Ping ping() {
        return new Ping();
    }
}
