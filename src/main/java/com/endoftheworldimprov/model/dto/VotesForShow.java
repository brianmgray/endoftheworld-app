package com.endoftheworldimprov.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Vote totals for a show
 * @author bgray
 **/
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VotesForShow {

    private Long showKey;
    private int saveVotes;
    private int destroyVotes;

}
