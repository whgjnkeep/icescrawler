package ices.crawler;

import ices.crawler.utils.HttpConstant;

import java.util.*;

/**
 * Created by Neuclil on 17-6-13.
 */
public class TaskConfig {

    private String domain;

    private String userAgent;

    private String charset;

    private boolean useGzip = true;

    private boolean socketKeepAlive = true;

    private boolean tcpNoDelay = true;

    private int socketTimeout;

    private int sleepTime = 5000;

    private int retrySleepTime = 1000;

    private int retryTimes = 0;

    private int cycleRetryTimes = 0;

    private boolean disableCookieManagement = false;

    private Map<String, String> cookies = new LinkedHashMap<String, String>();

    private Map<String, String> headers = new HashMap<String, String>();

    private static final Set<Integer> DEFAULT_STATUS_CODE_SET = new HashSet<Integer>();

    private Set<Integer> acceptStatCode = DEFAULT_STATUS_CODE_SET;


    static {
        DEFAULT_STATUS_CODE_SET.add(HttpConstant.StatusCode.CODE_200);
    }

    public static TaskConfig custom(){
        return new TaskConfig();
    }

    public int getRetrySleepTime() {
        return retrySleepTime;
    }

    public void setRetrySleepTime(int retrySleepTime) {
        this.retrySleepTime = retrySleepTime;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public TaskConfig setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    public int getCycleRetryTimes() {
        return cycleRetryTimes;
    }

    public void setCycleRetryTimes(int cycleRetryTimes) {
        this.cycleRetryTimes = cycleRetryTimes;
    }

    public static Set<Integer> getDefaultStatusCodeSet() {
        return DEFAULT_STATUS_CODE_SET;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public boolean isDisableCookieManagement() {
        return disableCookieManagement;
    }

    public void setDisableCookieManagement(boolean disableCookieManagement) {
        this.disableCookieManagement = disableCookieManagement;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public boolean isSocketKeepAlive() {
        return socketKeepAlive;
    }

    public void setSocketKeepAlive(boolean socketKeepAlive) {
        this.socketKeepAlive = socketKeepAlive;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public Set<Integer> getAcceptStatCode() {
        return acceptStatCode;
    }

    public void setAcceptStatCode(Set<Integer> acceptStatCode) {
        this.acceptStatCode = acceptStatCode;
    }

    public boolean isUseGzip() {
        return useGzip;
    }

    public void setUseGzip(boolean useGzip) {
        this.useGzip = useGzip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
