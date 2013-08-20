package com.endoftheworldimprov;

import com.endoftheworldimprov.model.domain.Show;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

/** @author bgray */
@NoArgsConstructor
@Slf4j
public final class JsonUtils {

    public static final JSONObject convertToJson(Show show) {
        JSONObject message = new JSONObject(show);
        try {
            message.put("activationStatus", show.getActivationStatus().name());
        } catch (JSONException e) {
            log.error("JSONException updating activation status", e);
        }
        return message;
    }
}
