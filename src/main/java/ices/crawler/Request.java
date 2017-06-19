package ices.crawler;

import org.apache.commons.collections.map.HashedMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Neuclil on 17-6-13.
 */
public class Request implements Serializable{

    public static final String CYCLE_TRIED_TIMES = "_cycle_tried_times";

    private String url;

    private String method;

    private int priority;

    private HttpRequestBody requestBody;

    private Map<String, String> cookies = new HashMap<String, String>();

    private Map<String, String> headers = new HashMap<String, String>();

    private Map<String, Object> extras;

    public int getPriority() {
        return priority;
    }

    public Request setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }

    public Object getExtra(String key) {
        if(extras == null)
            return null;
        return extras.get(key);
    }

    public Request putExtra(String key, Object value) {
        if(extras == null)
            extras = new HashMap<String, Object>();
        extras.put(key, value);
        return this;
    }

    public Request(String url){
        this.url = url;
    }

    public HttpRequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(HttpRequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
