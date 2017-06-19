package ices.crawler.processor;

import ices.crawler.ResultDocument;
import ices.crawler.Task;
import ices.crawler.TaskConfig;

/**
 * Created by Neuclil on 17-6-19.
 */
public interface ResultDocumentProcessor {

    void process(ResultDocument resultDocument);

    TaskConfig getTaskConfig();
}
