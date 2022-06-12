package com.terwergreen.metaweblogapiadaptor.adaptor.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.terwergreen.metaweblogapiadaptor.adaptor.AbstractXmlRpcClient;
import com.terwergreen.metaweblogapiadaptor.adaptor.IBlogAdaptor;
import com.terwergreen.metaweblogapiadaptor.plantform.confluence.ConfluenceRestApiClient;
import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.terwergreen.metaweblogapiadaptor.constant.ConfluenceConstants.CONFLUENCE_DEFAULT_SPACE_KEY;
import static com.terwergreen.metaweblogapiadaptor.plantform.confluence.ConfluenceRestApiClient.defineConfluencePage;

/**
 * Confluence的Rest适配器
 *
 * @name: ConfluenceRestAdaptor
 * @author: terwer
 * @date: 2022-06-12 02:01
 **/
public class ConfluenceRestAdaptor extends AbstractXmlRpcClient implements IBlogAdaptor {
    private static Logger logger = LoggerFactory.getLogger(ConfluenceRestAdaptor.class);

    private String serverUrl;
    private String username;
    private String password;
    private ConfluenceRestApiClient restApiClient;

    public ConfluenceRestAdaptor(String serverUrl, String username, String password) {
        this.serverUrl = serverUrl;
        this.username = username;
        this.password = password;

        restApiClient = new ConfluenceRestApiClient(serverUrl, username, password);
    }

    // ====================
    // bloggerApi开始
    // ====================
    @Override
    public List<Map<String, Object>> getUsersBlogs(String appKey, String username, String password) throws XmlRpcException {
        List<Map<String, Object>> usersBlogs = new ArrayList<>();

        String result = restApiClient.getSpaces();
        JSONObject resultObj = JSON.parseObject(result);
        JSONArray spaces = resultObj.getJSONArray("results");
        for (Object obj : spaces) {
            JSONObject space = (JSONObject) obj;

            if(!CONFLUENCE_DEFAULT_SPACE_KEY.equalsIgnoreCase(space.getString("key"))){
                continue;
            }

            HashMap<String, Object> userBlog = new HashMap<>();
            userBlog.put("blogName", space.get("name"));
            userBlog.put("blogid", space.get("key"));
            userBlog.put("url", String.format("https://youweics.atlassian.net/wiki/spaces/%s", space.get("key")));
            usersBlogs.add(userBlog);
        }

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
        // logger.info("metaWeblog.newPost -> blogid: {}, post: {}, publish: {}", blogid, JSON.toJSONString(post), publish);
        logger.info("metaWeblog.newPost -> blogid: {}, publish: {}", blogid, publish);

        // isValid(username, password);

        JSONObject postJson = JSONObject.parseObject(JSON.toJSONString(post));
        logger.debug("postJson = {}", postJson);

        // 1、准备数据
        String wikiPageTitle = postJson.getString("title");
        String wikiPage = postJson.getString("description");
        String wikiSpace = CONFLUENCE_DEFAULT_SPACE_KEY;
        String labelToAdd = "awesome_stuff";
        int parentPageId = 1277961;

        // 开始调用confluence rest api
        JSONObject newPage = defineConfluencePage(wikiPageTitle,
                wikiPage,
                wikiSpace,
                labelToAdd,
                parentPageId);
        String reuslt = restApiClient.newPage(newPage);
        JSONObject rtnObj = JSON.parseObject(reuslt);
        String id = rtnObj.getString("id");
        if (id == null) {
            logger.error("client.newPage error,message=>" + rtnObj.getString("message"));
            return "0";
        }
        logger.info("client.newPage success,id=>" + reuslt);
        // logger.info("client.newPage=>" + reuslt);
        return reuslt;
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
