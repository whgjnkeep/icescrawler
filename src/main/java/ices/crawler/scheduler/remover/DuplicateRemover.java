package ices.crawler.scheduler.remover;

import ices.crawler.Request;
import ices.crawler.Task;

/**
 * Created by Neuclil on 17-6-18.
 */
public interface DuplicateRemover {

    boolean isDuplicate(Request request, Task task);

    void resetDuplicateCheck(Task task);

    int getTotalRequestsCount(Task task);
}
