package ices.crawler.pipeline;

import ices.crawler.ResultItems;
import ices.crawler.Task;

/**
 * Created by Neuclil on 17-6-18.
 */
public interface Pipeline {

    void process(ResultItems resultItems, Task task);
}
