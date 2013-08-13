package com.endoftheworldimprov.model.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Which state to move the show into
 * @author bgray
 **/

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public enum ActivationStatus {
        ACTIVE, INACTIVE
}
