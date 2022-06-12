package com.terwergreen.metaweblogapiadaptor.adaptor.impl;

import com.terwergreen.metaweblogapiadaptor.adaptor.AbstractXmlRpcClient;
import com.terwergreen.metaweblogapiadaptor.adaptor.IBlogAdaptor;
import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wordpress适配器
 *
 * @name: WordpressXmlrpcAdaptor
 * @author: terwer
 * @date: 2022-06-12 03:09
 **/
public class WordpressXmlrpcAdaptor extends AbstractXmlRpcClient implements IBlogAdaptor {
    private static Logger logger = LoggerFactory.getLogger(WordpressXmlrpcAdaptor.class);

    private String serverUrl;

    private String username;

    private String password;

    public WordpressXmlrpcAdaptor(String serverUrl, String username, String password) {
        this.serverUrl = serverUrl;
        this.username = username;
        this.password = password;
    }

    // ====================
    // bloggerApi开始
    // ====================
    @Override
    public List<Map<String, Object>> getUsersBlogs(String appKey, String username, String password) throws XmlRpcException {
        List<Map<String, Object>> usersBlogs = new ArrayList<>();

        List<String> pParams = new ArrayList<>();
        pParams.add("default");
        pParams.add(this.username);
        pParams.add(this.password);

        Object[] result = (Object[]) this.executeMeteweblog(serverUrl, "blogger.getUsersBlogs", pParams);

        HashMap<String, Object> userBlog = new HashMap<>();
        if (result != null && result.length > 0) {
            userBlog = (HashMap<String, Object>) result[0];
        }

        logger.debug("blogger.getUsersBlogs=>");
        usersBlogs.add(userBlog);
        return usersBlogs;
    }
    // ====================
    // bloggerApi结束
    // ====================

    // ====================
    // metaWeblogApi开始
    // ====================
    @Override
    public String newPost(String blogid, String username, String password, Map<String, Object> post, boolean publish) throws XmlRpcException {
        return null;
    }

    @Override
    public boolean editPost(String postid, String username, String password, Map<String, Object> post, boolean publish) throws XmlRpcException {
        return false;
    }

    @Override
    public Map<String, Object> getPost(String postid, String username, String password) throws XmlRpcException {
        return null;
    }

    @Override
    public List<Map<String, String>> getCategories(String blogid, String username, String password) throws XmlRpcException {
        return null;
    }

    @Override
    public List<Map<String, Object>> getRecentPosts(String blogid, String username, String password, int numberOfPosts) throws XmlRpcException {
        return null;
    }

    @Override
    public Map<String, String> newMediaObject(String blogid, String username, String password, Map<String, Object> post) throws XmlRpcException {
        return null;
    }
    // ====================
    // metaWeblogApi结束
    // ====================
}
