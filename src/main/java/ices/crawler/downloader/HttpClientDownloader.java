package ices.crawler.downloader;

import ices.crawler.Request;
import ices.crawler.ResultDocument;
import ices.crawler.Task;
import ices.crawler.TaskConfig;
import ices.crawler.utils.CharsetUtils;
import ices.crawler.utils.HttpClientUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Neuclil on 17-6-13.
 */
public class HttpClientDownloader extends AbstractDownloader{

    private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();

    private HttpClientFactory httpClientFactory = new HttpClientFactory();

    private boolean responseHeader = true;

    private CloseableHttpClient getHttpClient(TaskConfig config){
        if(config == null){
            return httpClientFactory.createHttpClient(null);
        }
        String domain = config.getDomain();
        CloseableHttpClient httpClient = httpClients.get(domain);
        if(httpClient == null){
            synchronized (this){
                httpClient = httpClients.get(domain);
                if (httpClient == null) {
                    httpClient = httpClientFactory.createHttpClient(config);
                    httpClients.put(domain, httpClient);
                }
            }
        }
        return httpClient;
    }

    @Override
    public ResultDocument download(Request request, Task task) {
        if(task == null || task.getTaskConfig() ==null){
            throw new NullPointerException("task or site can not be null");
        }

        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = getHttpClient(task.getTaskConfig());

        HttpClientRequestContext requestContext  = HttpClientUtils.convert(request, task.getTaskConfig());

        ResultDocument resultDocument = new ResultDocument();
        resultDocument.setDownloadSuccess(false);

        try{
             httpResponse = httpClient.execute(requestContext.getHttpUriRequest(),
                    requestContext.getHttpClientContext());
             resultDocument = handlerResponse(request, task.getTaskConfig().getCharset(),
                     httpResponse, task);
            onSuccess(request);
            return resultDocument;
        }catch(IOException e){
            e.printStackTrace();
            onError(request);
            return resultDocument;
        }finally {
            if (httpResponse != null) {
                //ensure the connection is released back to pool
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }
        }
    }

    protected ResultDocument handlerResponse(Request request,
                                             String charset,
                                             HttpResponse httpResponse,
                                             Task task) throws IOException {
        String content = getResponseContent(charset, httpResponse);
        ResultDocument resultDocument = new ResultDocument();
        resultDocument.setRawText(content);
        resultDocument.setRequest(request);
        resultDocument.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        resultDocument.setDownloadSuccess(true);
        if(responseHeader){
            resultDocument.setHeaders(HttpClientUtils.convertHeaders(httpResponse.getAllHeaders()));
        }
        return resultDocument;
    }

    private String getResponseContent(String charset, HttpResponse httpResponse) throws IOException {
        if(charset == null){
            byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
            String htmlCharset = getHtmlCharset(httpResponse, contentBytes);
            if(htmlCharset != null){
                return new String(contentBytes, htmlCharset);
            }else{
                // TODO
                return new String(contentBytes);
            }
        }else{
            return IOUtils.toString(httpResponse.getEntity().getContent(), charset);
        }
    }

    private String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes) throws IOException {
        return CharsetUtils.detectCharset(httpResponse.getEntity().getContentType() == null ? "" : httpResponse.getEntity().getContentType().getValue(), contentBytes);
    }

    @Override
    public void setThread(int threadNum) {

    }
}
