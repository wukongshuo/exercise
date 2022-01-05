package com.xxw.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;


@Slf4j
public class HttpClient {

    private HttpResult getResult(CloseableHttpResponse response) throws IOException {
        HttpResult result = new HttpResult();
        result.setStatus(response.getStatusLine().getStatusCode());
        result.setReasonPhrase(response.getStatusLine().getReasonPhrase());
        if (result.getStatus() == 200) {
            HttpEntity entity = response.getEntity();
            String beforeJson = EntityUtils.toString(entity, "utf-8");
            String afterJson = "";
            if (null != beforeJson && !"".equals(beforeJson)) {
                SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
                filter.getExcludes().addAll(Arrays.asList("passwd,creater,updater,updateTime".split(","))); //,createTime 创建时间不需要过滤
                JSONObject jsonObject = JSON.parseObject(beforeJson);
                afterJson = JSON.toJSONString(jsonObject, filter);
            }
            result.setData(afterJson);
            EntityUtils.consume(entity);
        }
        return result;
    }

    public HttpResult post(String url, String data) throws IOException {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        HttpResult result = null;
        try {
            client = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(15000).setConnectTimeout(15000).setConnectionRequestTimeout(15000).build();
            post.setConfig(requestConfig);
            post.addHeader("Content-Type", "application/json");
            if (data != null && !"".equals(data.trim())) {
                StringEntity entity = new StringEntity(data, "utf-8");
                post.setEntity(entity);
            }
            response = client.execute(post);
            result = getResult(response);
        } catch (ConnectTimeoutException | HttpHostConnectException e) {
            log.error("HttpHostConnectException: {}", ExceptionUtil.buildErrorMessage(e));
            throw e;
        } catch (IOException e) {
            log.error(ExceptionUtil.buildErrorMessage(e));
            throw e;
        } finally {
            try {

                if (Objects.nonNull(client) && Objects.nonNull(response)) {
                    client.close();
                }

                if (Objects.nonNull(response) && Objects.nonNull(result)) {
                    response.close();
                }

            } catch (IOException e) {
                log.error("http接口调用异常：url--> {} , 参数:--> {}", url, data, e);
            }
        }
        return result;
    }

    public HttpResult post(String url, byte[] data) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        if (data != null && data.length != 0) {
            ByteArrayEntity entity = new ByteArrayEntity(data);
            post.setEntity(entity);
        }
        CloseableHttpResponse response = client.execute(post);
        HttpResult result = getResult(response);
        response.close();
        client.close();
        return result;
    }

    public HttpResult postFile(String url, File file) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("sendfile", file);
        post.setEntity(builder.build());
        CloseableHttpResponse response = client.execute(post);
        HttpResult result = new HttpResult();
        result.setStatus(response.getStatusLine().getStatusCode());
        result.setReasonPhrase(response.getStatusLine().getReasonPhrase());
        if (result.getStatus() == 200) {
            HttpEntity entity = response.getEntity();
            String data = EntityUtils.toString(entity, "utf-8");
            data = data.substring(data.indexOf("httpurl=") + "httpurl=".length());
            result.setData(data);
            EntityUtils.consume(entity);
        }
        response.close();
        client.close();
        return result;
    }

    public HttpResult get(String path, Map<String, String> params) throws IOException {
        String url = path;
        if (params != null && !params.isEmpty()) {
            url = url + "?";
            Set<Entry<String, String>> set = params.entrySet();
            for (Entry<String, String> entry : set) {
                url = url + entry.getKey() + "=" + entry.getValue() + "&";
            }
            url = url.substring(0, url.length() - 1);
        }
        if (!url.toLowerCase().startsWith("http://")) {
            url = "http://" + url;
        }
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response = client.execute(get);
        HttpResult result = getResult(response);
        response.close();
        client.close();
        return result;
    }

    public HttpResult get(String path, String[] keys, String[] values) throws IOException {
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            params.put(keys[i], values[i]);
        }
        return get(path, params);
    }

    public HttpResult get(String path) throws IOException {
        return get(path, null);
    }

    public static void main(String[] args) throws IOException {
        HttpClient util = new HttpClient();
        Map<String, String> map = new HashMap<String, String>();
        map.put("method", "getAll");
        util.get("localhost:8080/map-signal/division", new String[]{"method"}, new String[]{"getAll"});
    }

}
