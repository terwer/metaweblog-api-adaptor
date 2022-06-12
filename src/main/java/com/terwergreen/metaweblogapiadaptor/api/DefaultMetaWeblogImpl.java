package com.terwergreen.metaweblogapiadaptor.api;

import com.terwergreen.metaweblogapiadaptor.MetaweblogApiAdaptorApplication;
import com.terwergreen.metaweblogapiadaptor.adaptor.impl.CnblogsXmlrpcAdaptor;
import com.terwergreen.metaweblogapiadaptor.adaptor.impl.ConfluenceXmlrpcAdaptor;
import com.terwergreen.metaweblogapiadaptor.adaptor.impl.ConfluenceRestAdaptor;
import com.terwergreen.metaweblogapiadaptor.constant.ApiTypeEnum;
import com.terwergreen.metaweblogapiadaptor.xmlrpc.metaweblog.IMetaWeblogApi;
import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 通用的MetaweblogApi入口
 *
 * @name: DefaultMetaWeblogImpl
 * @author: terwer
 * @date: 2022-06-11 22:05
 **/
public class DefaultMetaWeblogImpl implements IMetaWeblogApi {
    private static Logger logger = LoggerFactory.getLogger(DefaultMetaWeblogImpl.class);
    private IMetaWeblogApi metaWeblogApi;
    private static Properties blogProps = new Properties();

    static {
        InputStream blogPropsStream = MetaweblogApiAdaptorApplication.class.getClassLoader().getResourceAsStream("application.properties");
        try {
            blogProps.load(blogPropsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DefaultMetaWeblogImpl() {
        String apitype = (String) blogProps.get("blog.meteweblog.apitype");
        if (ApiTypeEnum.API_TYPE_CONFLUENCE_XML_RPC.getName().equals(apitype)) {
            metaWeblogApi = new ConfluenceXmlrpcAdaptor((String) blogProps.get("blog.meteweblog.confluence.xmlrpc.serverUrl"), (String) blogProps.get("blog.meteweblog.confluence.xmlrpc.username"), (String) blogProps.get("blog.meteweblog.confluence.xmlrpc.password"));
        } else if (ApiTypeEnum.API_TYPE_CONFLUENCE_REST.getName().equals(apitype)) {
            metaWeblogApi = new ConfluenceRestAdaptor((String) blogProps.get("blog.meteweblog.confluence.rest.serverUrl"), (String) blogProps.get("blog.meteweblog.confluence.rest.username"), (String) blogProps.get("blog.meteweblog.confluence.rest.password"));
        } else {
            metaWeblogApi = new CnblogsXmlrpcAdaptor((String) blogProps.get("blog.meteweblog.cnblogs.xmlrpc.serverUrl"), (String) blogProps.get("blog.meteweblog.cnblogs.xmlrpc.username"), (String) blogProps.get("blog.meteweblog.cnblogs.xmlrpc.password"));
        }
        logger.info("容器中注册DefaultMetaWeblogImpl");
    }

    @Override
    public String newPost(String blogid, String username, String password, Map<String, Object> post, boolean publish) throws XmlRpcException {
        return metaWeblogApi.newPost(blogid, username, password, post, publish);
    }

    @Override
    public boolean editPost(String postid, String username, String password, Map<String, Object> post, boolean publish) throws XmlRpcException {
        return metaWeblogApi.editPost(postid, username, password, post, publish);
    }

    @Override
    public Map<String, Object> getPost(String postid, String username, String password) throws XmlRpcException {
        return metaWeblogApi.getPost(postid, username, password);
    }

    @Override
    public List<Map<String, String>> getCategories(String blogid, String username, String password) throws XmlRpcException {
        return metaWeblogApi.getCategories(blogid, username, password);
    }

    @Override
    public List<Map<String, Object>> getRecentPosts(String blogid, String username, String password, int numberOfPosts) throws XmlRpcException {
        return metaWeblogApi.getRecentPosts(blogid, username, password, numberOfPosts);
    }

    @Override
    public Map<String, String> newMediaObject(String blogid, String username, String password, Map<String, Object> post) throws XmlRpcException {
        return metaWeblogApi.newMediaObject(blogid, username, password, post);
    }
}
