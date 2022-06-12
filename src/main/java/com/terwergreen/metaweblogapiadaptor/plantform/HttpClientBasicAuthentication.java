package com.terwergreen.metaweblogapiadaptor.plantform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

/**
 * httpclient的basic校验
 * <p>
 * 参考：https://www.baeldung.com/java-httpclient-basic-auth
 * POST参考：https://mkyong.com/java/java-11-httpclient-examples/
 *
 * @name: HttpClientBasicAuthentication
 * @author: terwer
 * @date: 2022-06-11 23:53
 **/
public class HttpClientBasicAuthentication {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientBasicAuthentication.class);

    /**
     * 基于HTTP Basic验证发送GET请求
     *
     * @param url
     * @param username
     * @param password
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws URISyntaxException
     */
    protected String useClientWithHeadersGET(String url, String username, String password) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newBuilder()
                // .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("Authorization", getBasicAuthenticationHeader(username, password))
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        logger.info("Status using headers: {}", response.statusCode());

        return response.body();
    }

    /**
     * 基于HTTP Basic验证发送GET请求发送Form数据
     *
     * @param url
     * @param username
     * @param password
     * @param formData
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws URISyntaxException
     */
    protected String useClientWithHeadersPOSTForm(String url, String username, String password, Map<Object, Object> formData) throws IOException, InterruptedException, URISyntaxException {
        final HttpClient client = HttpClient.newBuilder()
                // .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // form parameters
        // Map<Object, Object> formData = new HashMap<>();
        // data.put("username", "abc");
        // data.put("password", "123");
        // data.put("custom", "secret");
        // data.put("ts", System.currentTimeMillis());

        HttpRequest request = HttpRequest.newBuilder()
                .POST(ofFormData(formData))
                .uri(URI.create(url))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", getBasicAuthenticationHeader(username, password))
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        logger.info("Status using headers: {}", response.statusCode());

        return response.body();
    }

    // Sample: 'password=123&custom=secret&username=abc&ts=1570704369823'
    private HttpRequest.BodyPublisher ofFormData(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

    /**
     * 基于HTTP Basic验证发送GET请求发送Json数据
     * @param url
     * @param username
     * @param password
     * @param jsonData
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws URISyntaxException
     */
    protected String useClientWithHeadersPOSTJson(String url, String username, String password,String jsonData) throws IOException, InterruptedException, URISyntaxException {
        final HttpClient client = HttpClient.newBuilder()
                // .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // json formatted data
        // String jsonData = new StringBuilder()
        //         .append("{")
        //         .append("\"name\":\"mkyong\",")
        //         .append("\"notes\":\"hello\"")
        //         .append("}").toString();

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                .uri(URI.create(url))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .header("Content-Type", "application/json")
                .header("Authorization", getBasicAuthenticationHeader(username, password))
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        logger.info("Status using headers: {}", response.statusCode());

        return response.body();
    }

    private static final String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder()
                .encodeToString(valueToEncode.getBytes());
    }

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        HttpClientBasicAuthentication auth = new HttpClientBasicAuthentication();

        auth.useClientWithHeadersGET("https://postman-echo.com/basic-auth", "postman", "password");
    }
}
