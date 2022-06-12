package com.terwergreen.metaweblogapiadaptor.plantform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Resi Api请求处理抽象类
 *
 * @name: AbstractRestApiClient
 * @author: terwer
 * @date: 2022-06-11 22:47
 **/
public class AbstractRestApiClient extends HttpClientBasicAuthentication {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRestApiClient.class);

    public String executeRestApiGET(String url, String username, String password) {
        try {
            // useClientWithAuthenticator在confluence中无效
            // this.useClientWithAuthenticator(url, username, password);
            return this.useClientWithHeadersGET(url, username, password);
        } catch (Exception e) {
            logger.error("executeRestApi.error=>", e);
            throw new RuntimeException(e);
        }
    }

    public String executeRestApiPOSTForm(String url, String username, String password, Map<Object, Object> formData) {
        try {
            // useClientWithAuthenticator在confluence中无效
            // this.useClientWithAuthenticator(url, username, password);
            return this.useClientWithHeadersPOSTForm(url, username, password, formData);
        } catch (Exception e) {
            logger.error("executeRestApi.error=>", e);
            throw new RuntimeException(e);
        }
    }

    public String executeRestApiPOSTJson(String url, String username, String password, String jsonData) {
        try {
            // useClientWithAuthenticator在confluence中无效
            // this.useClientWithAuthenticator(url, username, password);
            return this.useClientWithHeadersPOSTJson(url, username, password, jsonData);
        } catch (Exception e) {
            logger.error("executeRestApi.error=>", e);
            throw new RuntimeException(e);
        }
    }
}
