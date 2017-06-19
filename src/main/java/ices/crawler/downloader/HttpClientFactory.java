package ices.crawler.downloader;

import ices.crawler.TaskConfig;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.CookieStore;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Created by Neuclil on 17-6-13.
 */
public class HttpClientFactory {

    private PoolingHttpClientConnectionManager connectionManager;

    public HttpClientFactory() {
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", buildSSLConnectionSocketFactory())
                .build();
        connectionManager = new PoolingHttpClientConnectionManager(reg);
        connectionManager.setDefaultMaxPerRoute(100);
    }

    private SSLConnectionSocketFactory buildSSLConnectionSocketFactory() {
        try {
            return new SSLConnectionSocketFactory(createIgnoreVerifySSL()); // 优先绕过安全证书
        } catch (KeyManagementException e) {
            //
        } catch (NoSuchAlgorithmException e) {
            //
        }
        return SSLConnectionSocketFactory.getSocketFactory();
    }

    private SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

        };

        SSLContext sc = SSLContext.getInstance("SSLv3");
        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    public CloseableHttpClient createHttpClient(TaskConfig config){
        final HttpClientBuilder httpClientBuilder = HttpClients.custom();

        httpClientBuilder.setConnectionManager(connectionManager);
        if(config.getUserAgent() != null){
            httpClientBuilder.setUserAgent(config.getUserAgent());
        }else{
            httpClientBuilder.setUserAgent("");
        }
        if(config.isUseGzip()){
            httpClientBuilder.addInterceptorFirst(new HttpRequestInterceptor() {
                @Override
                public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
                    if(!httpRequest.containsHeader("Accept-Encoding")){
                        httpRequest.addHeader("Accept-Encoding", "gzip");
                    }
                }
            });
        }
        httpClientBuilder.setRedirectStrategy(new CustomRedirectStrategy());

        SocketConfig.Builder socketConfigBuilder = SocketConfig.custom();
        socketConfigBuilder.setSoKeepAlive(config.isSocketKeepAlive());
        socketConfigBuilder.setTcpNoDelay(config.isTcpNoDelay());
        socketConfigBuilder.setSoTimeout(config.getSocketTimeout());
        SocketConfig socketConfig = socketConfigBuilder.build();

        httpClientBuilder.setDefaultSocketConfig(socketConfig);
        connectionManager.setDefaultSocketConfig(socketConfig);

        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(config.getRetryTimes(),
                true));
        createCookie(httpClientBuilder, config);
        return httpClientBuilder.build();
    }

    private void createCookie(HttpClientBuilder httpClientBuilder, TaskConfig config){
        if(config.isDisableCookieManagement()){
            httpClientBuilder.disableCookieManagement();
            return;
        }

        CookieStore cookieStore = new BasicCookieStore();
        for(Map.Entry<String, String> cookieEntry : config.getCookies().entrySet()){
            BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
            cookie.setDomain(config.getDomain());
            cookieStore.addCookie(cookie);
        }
        httpClientBuilder.setDefaultCookieStore(cookieStore);
    }
}
