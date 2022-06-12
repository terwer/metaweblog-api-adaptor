package com.terwergreen.metaweblogapiadaptor.adaptor;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.AsyncCallback;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;

/**
 * XmlRpc客户端通用基类
 *
 * @name: AbstractXmlRpcClient
 * @author: terwer
 * @date: 2022-06-12 01:28
 **/
public abstract class AbstractXmlRpcClient {
    private static Logger logger = LoggerFactory.getLogger(AbstractXmlRpcClient.class);

    // ====================
    // 通用api开始
    // ====================
    protected Object executeMeteweblog(String serverUrl, String pMethodName, List pParams) {
        Object result = null;
        try {
            // Create an object to represent our server.
            XmlRpcClient client = new XmlRpcClient();
            XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
            URL url = new URL(serverUrl);
            clientConfig.setServerURL(url);
            client.setConfig(clientConfig);

            // Call the server, and get our result.
            result = client.execute(pMethodName, pParams);
        } catch (XmlRpcException exception) {
            logger.error("JavaClient: XML-RPC Fault #" +
                    Integer.toString(exception.code) + ": " +
                    exception.toString());
        } catch (Exception exception) {
            logger.error("JavaClient: " + exception.toString());
        }
        return result;
    }

    protected void executeMeteweblogAsync(String serverUrl, String pMethodName, List pParams, AsyncCallback pCallback) {
        Object result = null;
        try {
            // Create an object to represent our server.
            XmlRpcClient client = new XmlRpcClient();
            XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
            URL url = new URL(serverUrl);
            clientConfig.setServerURL(url);
            client.setConfig(clientConfig);

            // Call the server, and get our result.
            client.executeAsync(pMethodName, pParams, pCallback);
        } catch (XmlRpcException exception) {
            logger.error("JavaClient: XML-RPC Fault #" +
                    Integer.toString(exception.code) + ": " +
                    exception.toString());
        } catch (Exception exception) {
            logger.error("JavaClient: " + exception.toString());
        }
    }
    // ====================
    // 通用api结束
    // ====================
}
