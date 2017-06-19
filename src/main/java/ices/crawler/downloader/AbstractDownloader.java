package ices.crawler.downloader;

import ices.crawler.Request;

/**
 * Created by Neuclil on 17-6-13.
 */
public abstract class AbstractDownloader implements Downloader{

    protected void onSuccess(Request request){}

    protected void onError(Request request){}
}
