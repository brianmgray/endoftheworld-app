package com.endoftheworldimprov.model.dto;

import com.endoftheworldimprov.model.domain.Show;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * A list of shows (object needed by JAX)
 *  @author bgray
 * */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowListDto {

    List<Show> shows;
}
