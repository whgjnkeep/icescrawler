package ices.crawler.scheduler;

import ices.crawler.Task;

/**
 * Created by Neuclil on 17-6-18.
 */
public interface MonitorableScheduler extends Scheduler{

    int getLeftRequestCount(Task task);

    int getTotalRequestsCount(Task task);
}
