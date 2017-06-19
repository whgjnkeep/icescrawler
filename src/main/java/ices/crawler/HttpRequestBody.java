package ices.crawler;

public class HttpRequestBody {

    private final byte[] body;

    private final String contentType;

    private final String encoding;

    public HttpRequestBody(byte[] body, String contentType, String encoding) {
        this.body = body;
        this.contentType = contentType;
        this.encoding = encoding;
    }

    public String getContentType() {
        return contentType;
    }

    public String getEncoding() {
        return encoding;
    }


    public byte[] getBody() {
        return body;
    }
}