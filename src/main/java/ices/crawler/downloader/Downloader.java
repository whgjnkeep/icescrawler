package ices.crawler.downloader;

import ices.crawler.ResultDocument;
import ices.crawler.Request;
import ices.crawler.Task;

/**
 * Created by Neuclil on 17-6-13.
 */
public interface Downloader {

    ResultDocument download(Request request, Task task);

    void setThread(int threadNum);
}
