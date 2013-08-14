package com.endoftheworldimprov.service.impl;

import com.endoftheworldimprov.service.api.IPubSubService;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * Service to deal with Shows
 * @author bgray
 **/
@Slf4j
@Transactional
@Component
@ToString
public class PubSubServiceImpl implements IPubSubService {

    @Value("${pubnub.publishKey}")
    private String publishKey;

    @Value("${pubnub.subscribeKey}")
    private String subscribeKey;

    @Value("${pubnub.secretKey}")
    private String secretKey;

    private Pubnub pubnub;

    @PostConstruct
    public void initMethod() throws Exception {
        pubnub =  new Pubnub(publishKey, subscribeKey);
    }
    
    @Override
    public void publish(String channel, JSONObject message) {
        pubnub.publish(channel, message, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                log.debug("Published {} : message: {}", channel, message);
            }
        });
    }
}
