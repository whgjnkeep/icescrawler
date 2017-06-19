package ices.crawler;

/**
 * Created by Neuclil on 17-6-19.
 */
public interface SpiderListener {

    void onSuccess(Request request);

    void onError(Request request);
}
