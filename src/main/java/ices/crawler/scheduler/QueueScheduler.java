package ices.crawler.scheduler;

import ices.crawler.Request;
import ices.crawler.Task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Neuclil on 17-6-18.
 */
public class QueueScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {

	private BlockingQueue<Request> queue = new LinkedBlockingQueue<Request>();

	@Override
	public int getLeftRequestCount(Task task) {
		return queue.size();
	}

	@Override
	public int getTotalRequestsCount(Task task) {
		return getDuplicateRemover().getTotalRequestsCount(task);
	}

	@Override
	public Request poll(Task task) {
		return queue.poll();
	}

	@Override
	protected void pushWhenNoDuplicate(Request request, Task task) {
		queue.add(request);
	}

	@Override
	public String toString() {
		return "QueueScheduler" + " --- " + super.toString();
	}

}
