package com.terwergreen.metaweblogapiadaptor.plantform.confluence;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.terwergreen.metaweblogapiadaptor.MetaweblogApiAdaptorApplication;
import com.terwergreen.metaweblogapiadaptor.adaptor.impl.CnblogsXmlrpcAdaptor;
import com.terwergreen.metaweblogapiadaptor.adaptor.impl.ConfluenceRestAdaptor;
import com.terwergreen.metaweblogapiadaptor.adaptor.impl.ConfluenceXmlrpcAdaptor;
import com.terwergreen.metaweblogapiadaptor.constant.ApiTypeEnum;
import com.terwergreen.metaweblogapiadaptor.constant.Constants;
import com.terwergreen.metaweblogapiadaptor.plantform.AbstractRestApiClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * 封装的Confluence Rest Api客户端入口
 *
 * @name: ConfluenceRestApiClient
 * @author: terwer
 * @date: 2022-06-11 22:46
 **/
public class ConfluenceRestApiClient extends AbstractRestApiClient {
    private static final Logger logger = LoggerFactory.getLogger(ConfluenceRestApiClient.class);

    private String confluenceCloudBaseUrl;
    private String username;
    private String password;

    public ConfluenceRestApiClient(String confluenceCloudBaseUrl, String username, String password) {
        this.confluenceCloudBaseUrl = confluenceCloudBaseUrl;
        this.username = username;
        this.password = password;
    }

    // https://docs.atlassian.com/ConfluenceServer/rest/7.18.1/#space-spaces
    // http://example.com/confluence/rest/api/space?spaceKey=TST&spaceKey=ds
    public String getSpaces() {
        String result = null;
        final String[] expansions = new String[]{};
        try {
            // 格式化请求url
            final String expand = URLEncoder.encode(StringUtils.join(expansions, ","), Constants.ENCODING);
            String requestUrl = String.format("%s/space?expand=%s", confluenceCloudBaseUrl, expand, URLEncoder.encode(username, Constants.ENCODING), URLEncoder.encode(password, Constants.ENCODING));
            logger.info("getContent.requestUrl=>" + requestUrl);

            result = this.executeRestApiGET(requestUrl, username, password);
        } catch (Exception e) {
            logger.info("getContent.error=>", e);
        }

        return result;
    }

    /**
     * 获取一个Confluence页面
     *
     * @param contentId  页面ID
     * @param expansions 范围
     * @return
     */
    public String getPage(final Long contentId, final String[] expansions) {
        String result = null;
        try {
            // 格式化请求url
            final String expand = URLEncoder.encode(StringUtils.join(expansions, ","), Constants.ENCODING);
            String requestUrl = String.format("%s/content/%s?expand=%s", confluenceCloudBaseUrl, contentId, expand, URLEncoder.encode(username, Constants.ENCODING), URLEncoder.encode(password, Constants.ENCODING));
            logger.info("getContent.requestUrl=>" + requestUrl);

            requestUrl = "https://youweics.atlassian.net/wiki/rest/api/content";

            result = this.executeRestApiGET(requestUrl, username, password);
        } catch (Exception e) {
            logger.info("getContent.error=>", e);
        }

        return result;
    }

    /**
     * 创建一个Confluence页面
     *
     * @param newPage 页面数据结构
     */
    public String newPage(JSONObject newPage) {
        String requestUrl = String.format("%s/content/", confluenceCloudBaseUrl);
        String jsonData = JSON.toJSONString(newPage);

        String result = this.executeRestApiPOSTJson(requestUrl, username, password, jsonData);
        return result;
    }

    // ============
    // private methods
    // ============
    public static JSONObject defineConfluencePage(String pageTitle,
                                                  String wikiEntryText,
                                                  String pageSpace,
                                                  String label,
                                                  int parentPageId) throws JSONException {
        //This would be the command in Python (similar to the example
        //in the Confluence example:
        //
        //curl -u <username>:<password> -X POST -H 'Content-Type: application/json' -d'{
        // "type":"page",
        // "title":"My Awesome Page",
        // "ancestors":[{"id":9994246}],
        // "space":{"key":"JOUR"},
        // "body":
        //        {"storage":
        //                   {"value":"<h1>Things That Are Awesome</h1><ul><li>Birds</li><li>Mammals</li><li>Decapods</li></ul>",
        //                    "representation":"storage"}
        //        },
        // "metadata":
        //             {"labels":[
        //                        {"prefix":"global",
        //                        "name":"journal"},
        //                        {"prefix":"global",
        //                        "name":"awesome_stuff"}
        //                       ]
        //             }
        // }'
        // http://localhost:8080/confluence/rest/api/content/ | python -mjson.tool

        JSONObject newPage = new JSONObject();

        // "type":"page",
        // "title":"My Awesome Page"
        newPage.put("type", "page");
        newPage.put("title", pageTitle);

        // "ancestors":[{"id":9994246}],
        JSONObject parentPage = new JSONObject();
        parentPage.put("id", parentPageId);

        JSONArray parentPageArray = new JSONArray();
        parentPageArray.add(parentPage);

        newPage.put("ancestors", parentPageArray);

        // "space":{"key":"JOUR"},
        JSONObject spaceOb = new JSONObject();
        spaceOb.put("key", pageSpace);
        newPage.put("space", spaceOb);

        // "body":
        //        {"storage":
        //                   {"value":"<p><h1>Things That Are Awesome</h1><ul><li>Birds</li><li>Mammals</li><li>Decapods</li></ul></p>",
        //                    "representation":"storage"}
        //        },
        JSONObject jsonObjects = new JSONObject();

        jsonObjects.put("value", wikiEntryText);
        jsonObjects.put("representation", "storage");

        JSONObject storageObject = new JSONObject();
        storageObject.put("storage", jsonObjects);

        newPage.put("body", storageObject);


        //LABELS
        // "metadata":
        //             {"labels":[
        //                        {"prefix":"global",
        //                        "name":"journal"},
        //                        {"prefix":"global",
        //                        "name":"awesome_stuff"}
        //                       ]
        //             }
        JSONObject prefixJsonObject1 = new JSONObject();
        prefixJsonObject1.put("prefix", "global");
        prefixJsonObject1.put("name", "journal");
        JSONObject prefixJsonObject2 = new JSONObject();
        prefixJsonObject2.put("prefix", "global");
        prefixJsonObject2.put("name", label);

        JSONArray prefixArray = new JSONArray();
        prefixArray.add(prefixJsonObject1);
        prefixArray.add(prefixJsonObject2);

        JSONObject labelsObject = new JSONObject();
        labelsObject.put("labels", prefixArray);

        newPage.put("metadata", labelsObject);

        return newPage;
    }

    public static void main(String[] args) {
        // ==============
        // 读取配置
        // ==============
        Properties blogProps = new Properties();
        InputStream blogPropsStream = MetaweblogApiAdaptorApplication.class.getClassLoader().getResourceAsStream("application-pro.properties");
        try {
            blogProps.load(blogPropsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ConfluenceRestApiClient client = new ConfluenceRestApiClient((String) blogProps.get("blog.meteweblog.confluence.rest.serverUrl"), (String) blogProps.get("blog.meteweblog.confluence.rest.username"), (String) blogProps.get("blog.meteweblog.confluence.rest.password"));

        // ==============
        // 测试getPage
        // ==============
        //This is the Page ID that can be found
        //if you go to the "Page Information" section
        //within Confluence
//        final long pageId = 819457;
//        String content = client.getPage(pageId, new String[]{"body.storage", "version", "ancestors"});
//        logger.info("client.getContent=>" + content);

        // ==============
        // 测试newPage
        // ==============
        String wikiPageTitle = "My Awesome Page";
        String wikiPage = "<h1>Things That Are Awesome</h1><ul><li>Birds</li><li>Mammals</li><li>Decapods</li></ul>";
        String wikiSpace = "SPC";
        String labelToAdd = "awesome_stuff";
        int parentPageId = 1277961;
        JSONObject newPage = defineConfluencePage(wikiPageTitle,
                wikiPage,
                wikiSpace,
                labelToAdd,
                parentPageId);
        String reuslt = client.newPage(newPage);
        JSONObject rtnObj = JSON.parseObject(reuslt);
        String id = rtnObj.getString("id");
        if (id == null) {
            logger.error("client.newPage error,message=>" + rtnObj.getString("message"));
            return;
        }
        logger.info("client.newPage success,id=>" + reuslt);
        // logger.info("client.newPage=>" + reuslt);
    }
}
