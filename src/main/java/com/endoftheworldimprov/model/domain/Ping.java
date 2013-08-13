package com.endoftheworldimprov.model.domain;

import lombok.Data;

/**
 * Ping object to test services
 * @author bgray
 **/
//@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Data
public class Ping {

    private final String time;

    public Ping() {
        this.time = String.valueOf(System.currentTimeMillis());
    }
}
