package com.terwergreen.metaweblogapiadaptor.adaptor;

/**
 * 公共博客接口
 *
 * @name: BlogHelper
 * @author: terwer
 * @date: 2022-03-28 19:45
 **/

import com.terwergreen.metaweblogapiadaptor.xmlrpc.blogger.IBloggerApi;
import com.terwergreen.metaweblogapiadaptor.xmlrpc.metaweblog.IMetaWeblogApi;
import org.apache.xmlrpc.XmlRpcException;

import java.util.List;
import java.util.Map;

/**
 * @author: terwer
 * @date: 2022/1/9 18:51
 * @description: BlogHelper
 */
public interface IBlogAdaptor extends IBloggerApi, IMetaWeblogApi {
}