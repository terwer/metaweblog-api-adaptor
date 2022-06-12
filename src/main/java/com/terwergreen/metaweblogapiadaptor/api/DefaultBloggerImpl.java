package com.terwergreen.metaweblogapiadaptor.api;

import com.terwergreen.metaweblogapiadaptor.MetaweblogApiAdaptorApplication;
import com.terwergreen.metaweblogapiadaptor.adaptor.impl.CnblogsXmlrpcAdaptor;
import com.terwergreen.metaweblogapiadaptor.adaptor.impl.ConfluenceXmlrpcAdaptor;
import com.terwergreen.metaweblogapiadaptor.adaptor.impl.ConfluenceRestAdaptor;
import com.terwergreen.metaweblogapiadaptor.constant.ApiTypeEnum;
import com.terwergreen.metaweblogapiadaptor.xmlrpc.blogger.IBloggerApi;
import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 通用的bloggerApi入口
 *
 * @name: DefaultBloggerImpl
 * @author: terwer
 * @date: 2022-06-11 22:04
 **/
public class DefaultBloggerImpl implements IBloggerApi {
    private static Logger logger = LoggerFactory.getLogger(DefaultBloggerImpl.class);
    private IBloggerApi bloggerApi;
    private static Properties blogProps = new Properties();

    static {
        InputStream blogPropsStream = MetaweblogApiAdaptorApplication.class.getClassLoader().getResourceAsStream("application.properties");
        try {
            blogProps.load(blogPropsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DefaultBloggerImpl() {
        String apitype = (String) blogProps.get("blog.meteweblog.apitype");
        if (ApiTypeEnum.API_TYPE_CONFLUENCE_XML_RPC.getName().equals(apitype)) {
            bloggerApi = new ConfluenceXmlrpcAdaptor((String) blogProps.get("blog.meteweblog.confluence.xmlrpc.serverUrl"), (String) blogProps.get("blog.meteweblog.confluence.xmlrpc.username"), (String) blogProps.get("blog.meteweblog.confluence.xmlrpc.password"));
        } else if (ApiTypeEnum.API_TYPE_CONFLUENCE_REST.getName().equals(apitype)) {
            bloggerApi = new ConfluenceRestAdaptor((String) blogProps.get("blog.meteweblog.confluence.rest.serverUrl"), (String) blogProps.get("blog.meteweblog.confluence.rest.username"), (String) blogProps.get("blog.meteweblog.confluence.rest.password"));
        } else {
            bloggerApi = new CnblogsXmlrpcAdaptor((String) blogProps.get("blog.meteweblog.cnblogs.xmlrpc.serverUrl"), (String) blogProps.get("blog.meteweblog.cnblogs.xmlrpc.username"), (String) blogProps.get("blog.meteweblog.cnblogs.xmlrpc.password"));
        }
        logger.info("容器中注册DefaultBloggerImpl");
    }

    @Override
    public List<Map<String, Object>> getUsersBlogs(String appKey, String username, String password) throws XmlRpcException {
        return bloggerApi.getUsersBlogs(appKey, username, password);
    }
}
