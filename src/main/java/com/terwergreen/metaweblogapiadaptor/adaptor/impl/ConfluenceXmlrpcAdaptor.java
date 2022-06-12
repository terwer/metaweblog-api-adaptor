package com.terwergreen.metaweblogapiadaptor.adaptor.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.terwergreen.metaweblogapiadaptor.adaptor.AbstractXmlRpcClient;
import com.terwergreen.metaweblogapiadaptor.adaptor.IBlogAdaptor;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.common.XmlRpcNotAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static com.terwergreen.metaweblogapiadaptor.constant.ConfluenceConstants.CATEGORIES;
import static com.terwergreen.metaweblogapiadaptor.constant.ConfluenceConstants.CONFLUENCE_DEFAULT_SPACE_KEY;
import static com.terwergreen.metaweblogapiadaptor.constant.ConfluenceConstants.DATECREATED;
import static com.terwergreen.metaweblogapiadaptor.constant.ConfluenceConstants.DESCRIPTION;
import static com.terwergreen.metaweblogapiadaptor.constant.ConfluenceConstants.TITLE;

/**
 * Confluence的metaWeblogApi实现
 *
 * @name: ConfluenceXmlrpcAdaptor
 * @author: terwer
 * @date: 2022-03-28 19:46
 **/
public class ConfluenceXmlrpcAdaptor extends AbstractXmlRpcClient implements IBlogAdaptor {
    private static Logger logger = LoggerFactory.getLogger(ConfluenceXmlrpcAdaptor.class);

    private String serverUrl;
    private String username;
    private String password;

    public ConfluenceXmlrpcAdaptor(String serverUrl, String username, String password) {
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

    // ==================================
    // Confluence专用（旧的xmlrpc用，废弃）
    // ==================================
    @Deprecated
    public List<Map<String, Object>> getUsersBlogsConfluence(String appKey, String username, String password) throws XmlRpcException {
        logger.info("[blogger.getUsersBlogs] -> appKey: {}, username: {}, password: {}", appKey, username, password);

        Map<String, Object> userBlog = this.getUsersBlogs(appKey, username, password).get(0);
        String blogid = (String) userBlog.get("blogid");
        String newblogid = CONFLUENCE_DEFAULT_SPACE_KEY;
        String url = ((String) userBlog.get("url"));
        String newurl = url.replace(blogid, newblogid);
        String newblogName = "Confluence知识库博文";

        List<Map<String, Object>> usersBlogs = new ArrayList<>();
        Map<String, Object> blogInfo = new HashMap<>();
        blogInfo.put("blogid", newblogid);
        blogInfo.put("url", newurl);
        blogInfo.put("blogName", newblogName);
        usersBlogs.add(blogInfo);

        return usersBlogs;
    }

    // ====================
    // bloggerApi结束
    // ====================

    // ====================
    // metaWeblogApi开始
    // ====================
    // ==================================
    // Confluence校验（旧的xmlrpc用，废弃）
    // ==================================
    @Deprecated
    private void isValid(String username, String password) throws XmlRpcNotAuthorizedException {
        logger.info("username: {}, password: {}", username, password);
        boolean isValid = this.username.equals(username) && this.password.equals(password);
        logger.info("isValid = {}", isValid);
        if (!isValid) {
            throw new XmlRpcNotAuthorizedException("账号或密码有误");
        }
    }

    @Override
    public String newPost(String blogid, String username, String password, Map<String, Object> post, boolean publish) throws XmlRpcException {
        // logger.info("metaWeblog.newPost -> blogid: {}, post: {}, publish: {}", blogid, JSON.toJSONString(post), publish);
        logger.info("metaWeblog.newPost -> blogid: {}, publish: {}", blogid, publish);

        isValid(username, password);

        // 测试读取
        // String basepath = "C:/Users/terwer/Documents/share/";
        // if (SystemUtil.isLinux()) {
        //      basepath = "/Users/terwer/Documents/share/";
        // }
        // String postPath = basepath + "cross/MWeb/MWebLibrary/docs/" + filehash + ".md";
        // FileInputStream inputStream = null;
        // try {
        //     inputStream = new FileInputStream(postPath);
        //  } catch (FileNotFoundException e) {
        //     e.printStackTrace();
        // }
        // String content = ResourceUtil.readStream(inputStream);
        // logger.info("content = " + content);

        JSONObject postJson = JSONObject.parseObject(JSON.toJSONString(post));
        logger.debug("postJson = {}", postJson);

        String postId = "0";
        try {
            JSONArray catjsonArray = postJson.getJSONArray("categories");
            Vector<String> categories = new Vector<>();
            for (Object cat : catjsonArray) {
                String catStr = (String) cat;
                categories.add(catStr);
            }
            // 将标签作为新增分类
            String[] tags = postJson.getString("mt_keywords").split(",");
            if (tags.length > 0) {
                for (String tag : tags) {
                    categories.add(tag);
                }
            }

            // 转换为markdown
            String content = postJson.getString("description");
            StringBuilder sb = new StringBuilder();
            sb.append("<ac:structured-macro ac:name=\"markdown\" ac:schema-version=\"1\" ac:macro-id=\"529e4807-3a3b-401e-a337-8cccc762b3fe\"><ac:plain-text-body><![CDATA[");
            sb.append(content);
            sb.append("]]></ac:plain-text-body></ac:structured-macro>");
            String parsedMarkdown = sb.toString();

            List<Object> pParams = new ArrayList<>();
            pParams.add(CONFLUENCE_DEFAULT_SPACE_KEY);
            pParams.add(username);
            pParams.add(password);
            Hashtable<String, Object> struct = new Hashtable<>();
            struct.put(TITLE, postJson.getString("title"));
            struct.put(DESCRIPTION, parsedMarkdown);
            struct.put(DATECREATED, postJson.getDate("dateCreated"));
            struct.put(CATEGORIES, categories);
            pParams.add(struct);// 文章信息
            pParams.add(true);// 是否发布
            String result = (String) this.executeMeteweblog(serverUrl, "metaWeblog.newPost", pParams);
            if (result == null) {
                throw new XmlRpcException(String.format("标题为 %s 的博文已存在", struct.get(TITLE)));
            }
            postId = result;
            // logger.info("发布成功，result = " + result);

            logger.info("Confluence add Post:" + JSON.toJSONString(result));
        } catch (Exception e) {
            logger.error("接口异常", e);
        }

        return postId;
    }

    @Override
    public boolean editPost(String postid, String username, String password, Map<String, Object> post, boolean publish) throws XmlRpcException {
        // logger.info("metaWeblog.editPost -> postid: {}, post: {}", postid, JSON.toJSONString(post));
        logger.info("metaWeblog.editPost -> postid: {}", postid);

        boolean flag = false;
        try {

            JSONObject postJson = JSONObject.parseObject(JSON.toJSONString(post));
            logger.debug("postJson = {}", postJson);

            JSONArray catjsonArray = postJson.getJSONArray("categories");
            Vector<String> categories = new Vector<>();
            for (Object cat : catjsonArray) {
                String catStr = (String) cat;
                categories.add(catStr);
            }
            // 将标签作为新增分类
            String[] tags = postJson.getString("mt_keywords").split(",");
            if (tags.length > 0) {
                for (String tag : tags) {
                    categories.add(tag);
                }
            }

            // 转换为markdown
            String content = postJson.getString("description");
            StringBuilder sb = new StringBuilder();
            sb.append("<ac:structured-macro ac:name=\"markdown\" ac:schema-version=\"1\" ac:macro-id=\"529e4807-3a3b-401e-a337-8cccc762b3fe\"><ac:plain-text-body><![CDATA[");
            sb.append(content);
            sb.append("]]></ac:plain-text-body></ac:structured-macro>");
            String parsedMarkdown = sb.toString();

            List<Object> pParams = new ArrayList<>();
            pParams.add(postid);
            pParams.add(username);
            pParams.add(password);
            Hashtable<String, Object> struct = new Hashtable<>();
            struct.put(TITLE, postJson.getString("title"));
            struct.put(DESCRIPTION, parsedMarkdown);
            struct.put(DATECREATED, postJson.getDate("dateCreated"));
            struct.put(CATEGORIES, categories);
            pParams.add(struct);// 文章信息
            pParams.add(true);// 是否发布
            boolean result = (boolean) this.executeMeteweblog(serverUrl, "metaWeblog.editPost", pParams);
            if (!result) {
                throw new XmlRpcException(String.format("ID为 %s 的博文不存在", struct.get(TITLE)));
            }
            // logger.info("result = " + JSON.toJSONString(result));

            flag = result;
            logger.info("Confluence update Post：" + JSON.toJSONString(result));
            flag = true;
        } catch (Exception e) {
            logger.error("接口异常", e);
        }

        return flag;
    }

    @Override
    public Map<String, Object> getPost(String postid, String username, String password) throws XmlRpcException {
        logger.info("metaWeblog.getPost -> postid: {}", postid);

        isValid(username, password);

        Map<String, Object> post = new HashMap<>();
        try {
            List<Object> pParams = new ArrayList<>();
            pParams.add(postid);
            pParams.add(username);
            pParams.add(password);
            post = (Map<String, Object>) this.executeMeteweblog(serverUrl, "metaWeblog.getPost", pParams);
        } catch (Exception e) {
            e.printStackTrace();
            throw new XmlRpcException(500, e.getMessage());
        }

        return post;
    }

    @Deprecated
    public <T> List<T> getRecentPosts_blogger(Map<String, Object> mappedParams) {
        List<Object> pParams = new ArrayList<>();
        pParams.add("default");
        pParams.add("ds");
        pParams.add(this.username);
        pParams.add(this.password);
        pParams.add(10);
        List<T> result = (List<T>) this.executeMeteweblog(serverUrl, "blogger.getRecentPosts", pParams);
        return result;
    }

    @Override
    public List<Map<String, Object>> getRecentPosts(String blogid, String username, String password, int numberOfPosts) throws XmlRpcException {
        logger.info("metaWeblog.getRecentPosts -> blogid: {}, numberOfPosts: {}", blogid, numberOfPosts);

        List<Object> pParams = new ArrayList<>();
        pParams.add(CONFLUENCE_DEFAULT_SPACE_KEY);
        pParams.add(username);
        pParams.add(password);
        pParams.add(10);
        Object[] dataList = (Object[]) this.executeMeteweblog(serverUrl, "metaWeblog.getRecentPosts", pParams);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object obj : dataList) {
            Map<String, Object> data = (Map<String, Object>) obj;
            result.add(data);
        }

        return result;
    }

    @Override
    public List<Map<String, String>> getCategories(String blogid, String username, String password) throws XmlRpcException {
        logger.info("metaWeblog.getCategories -> blogid: {}", blogid);

        isValid(username, password);

        List<Object> pParams = new ArrayList<>();
        pParams.add(CONFLUENCE_DEFAULT_SPACE_KEY);
        pParams.add(username);
        pParams.add(password);
        Map<String, Object> categories = (Map<String, Object>) this.executeMeteweblog(serverUrl, "metaWeblog.getCategories", pParams);

        List<Map<String, String>> result = new ArrayList<>();
        for (Map.Entry<String, Object> entry : categories.entrySet()) {
            Map<String, String> cat = new HashMap<>();
            cat.put("title", entry.getKey());
            Map<String, String> value = (Map<String, String>) entry.getValue();
            cat.putAll(value);
            result.add(cat);
        }

        return result;
    }

    /**
     * 上传附件到阿里云（由于阿里云付费，这个可做可不做）
     *
     * @param blogid
     * @param username
     * @param password
     * @param post
     * @return
     * @throws XmlRpcException
     */
    @Override
    public Map<String, String> newMediaObject(String blogid, String username, String password, Map<String, Object> post) throws XmlRpcException {
        /*
        logger.info("metaWeblog.newMediaObject -> blogid: {}", blogid);

        isValid(username, password);

        Map<String, String> urlData = new HashMap<>();

        try {
            String retUrl = "http://oss.terwergreen.com/%s";
            String name = post.get("name").toString();
            //  {year}/{mon}/{day}/{filename}{.suffix}
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String forder = sdf.format(now);
            System.out.println("forder = " + forder);
            String fileName = "bugucms/" + forder + "/" + name;
            String url = String.format(retUrl, fileName);

            byte[] bits = (byte[]) post.get("bits");
            logger.info("准备上传图片，url = " + url);
            // 开始上传图片
            OssManager manager = OssManager.getInstance();
            manager.upload(fileName, bits);

            // 水印
            // String watermark = String.format("?x-oss-process=%s", "image/auto-orient,1/quality,q_90/format,jpg/watermark,image_YnVndWNtcy9sb2dvLWRhcmsucG5nP3gtb3NzLXByb2Nlc3M9aW1hZ2UvcmVzaXplLFBfNjI,g_se,x_10,y_10");
            // String markedUrl = url + watermark;

            urlData.put("url", url);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("图片上传错误", e);
        }

        logger.info("urlData = {}", urlData);
        return urlData;
        */
        throw new RuntimeException("未实现此API");
    }
    // ====================
    // metaWeblogApi结束
    // ====================
}
