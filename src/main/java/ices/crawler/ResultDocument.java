package ices.crawler;

import ices.crawler.selector.Html;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Neuclil on 17-6-13.
 */
public class ResultDocument {

    private boolean downloadSuccess = true;

    private Html html;

    private String rawText;

    private Request request;

    private int statusCode;

    private Map<String, List<String>> headers;

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    private List<Request> targetRequests = new ArrayList<Request>();

    public List<Request> getTargetRequests() {
        return targetRequests;
    }

    private ResultItems resultItems = new ResultItems();

    public Html getHtml() {
        if (html == null) {
            html = new Html(rawText, request.getUrl());
        }
        return html;
    }

    public void putField(String key, Object field) {
        resultItems.put(key, field);
    }

    public ResultItems getResultItems() {
        return resultItems;
    }

    public void setTargetRequests(List<Request> targetRequests) {
        this.targetRequests = targetRequests;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
        this.resultItems.setRequest(request);
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public boolean isDownloadSuccess() {
        return downloadSuccess;
    }

    public void setDownloadSuccess(boolean downloadSuccess) {
        this.downloadSuccess = downloadSuccess;
    }

	@Override
	public String toString() {
		return "ResultDocument [" + 
				", request=" + request + 
	            ", downloadSuccess=" + downloadSuccess + 
	            ", html=" + html + 
	            ", rawText=" + rawText + 
			    ", statusCode=" + statusCode + 
			    ", headers=" + headers + 
			    ", targetRequests=" + targetRequests + 
			    ", resultItems=" + resultItems + 
			    "]";
	}
 
    
}