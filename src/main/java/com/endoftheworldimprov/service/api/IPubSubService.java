package com.endoftheworldimprov.service.api;


import org.json.JSONObject;

/**
 * Service for pubnub
 * @author bgray
 * */
public interface IPubSubService {

    /**
     * Publish a message to listening clients
     * @param channel to publish onto
     * @param message to publish
     */
    void publish(String channel, JSONObject message);
}
