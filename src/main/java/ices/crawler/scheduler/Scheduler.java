package ices.crawler.scheduler;

import ices.crawler.Request;
import ices.crawler.Task;

/**
 * Created by Neuclil on 17-6-18.
 */
public interface Scheduler {

    void push(Request request, Task task);

    Request poll(Task task);
}
