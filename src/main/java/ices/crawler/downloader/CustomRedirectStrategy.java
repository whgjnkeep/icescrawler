package ices.crawler.downloader;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import java.net.URI;

public class CustomRedirectStrategy extends LaxRedirectStrategy {

	private final static Logger LOGGER = Logger.getLogger(CustomRedirectStrategy.class);

	@Override
	public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context)
			throws ProtocolException {
		URI uri = getLocationURI(request, response, context);
		String method = request.getRequestLine().getMethod();
		if ("post".equalsIgnoreCase(method)) {
			try {
				HttpRequestWrapper httpRequestWrapper = (HttpRequestWrapper) request;
				httpRequestWrapper.setURI(uri);
				httpRequestWrapper.removeHeaders("Content-Length");
				return httpRequestWrapper;
			} catch (Exception e) {
				LOGGER.error("强转为HttpRequestWrapper出错");
			}
			return new HttpPost(uri);
		} else {
			return new HttpGet(uri);
		}
	}
}