package com.terwergreen.metaweblogapiadaptor.constant;

/**
 * 接口类型
 *
 * @name: ApiTypeEnum
 * @author: terwer
 * @date: 2022-06-12 02:28
 **/
public enum ApiTypeEnum {
    /**
     * xmlrpc
     */
    API_TYPE_CONFLUENCE_XML_RPC("conf_xmlrpc"),
    /**
     * rest
     */
    API_TYPE_CONFLUENCE_REST("conf_rest"),
    /**
     * graphhql
     */
    API_TYPE_GRAPH_HQL("conf_graphhql");

    private String name;

    private ApiTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
