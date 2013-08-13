package com.endoftheworldimprov.model.dto;

import com.endoftheworldimprov.model.domain.VoteValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * A vote by an audience member at a show
 * @author bgray
 **/
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class VoteDto {

    private Long key;
    @NonNull private Long showKey;
    @NonNull private String ip;
    @NonNull private VoteValue vote;

}
