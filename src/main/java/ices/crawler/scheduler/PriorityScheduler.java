package ices.crawler.scheduler;

import ices.crawler.Request;
import ices.crawler.Task;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by Neuclil on 17-6-18.
 */
public class PriorityScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {

	public static final int INITIAL_CAPACITY = 5;

	private BlockingQueue<Request> noPriorityQueue = new LinkedBlockingQueue<Request>();

	private PriorityBlockingQueue<Request> priorityQueuePlus = new PriorityBlockingQueue<Request>(INITIAL_CAPACITY,
			new Comparator<Request>() {
				@Override
				public int compare(Request o1, Request o2) {
					return -compareLong(o1.getPriority(), o2.getPriority());
				}
			});

	private PriorityBlockingQueue<Request> priorityQueueMinus = new PriorityBlockingQueue<Request>(INITIAL_CAPACITY,
			new Comparator<Request>() {
				@Override
				public int compare(Request o1, Request o2) {
					return -compareLong(o1.getPriority(), o2.getPriority());
				}
			});

	@Override
	protected void pushWhenNoDuplicate(Request request, Task task) {
		if (request.getPriority() == 0) {
			noPriorityQueue.add(request);
		} else if (request.getPriority() > 0) {
			priorityQueuePlus.put(request);
		} else {
			priorityQueueMinus.put(request);
		}
	}

	@Override
	public int getLeftRequestCount(Task task) {
		return noPriorityQueue.size() + priorityQueuePlus.size() + priorityQueueMinus.size(); // ??
	}

	@Override
	public int getTotalRequestsCount(Task task) {
		return getDuplicateRemover().getTotalRequestsCount(task);
	}

	@Override
	public synchronized Request poll(Task task) {
		Request poll = priorityQueuePlus.poll();
		if (poll != null) {
			return poll;
		}
		poll = noPriorityQueue.poll();
		if (poll != null) {
			return poll;
		}
		return priorityQueueMinus.poll();
	}

	private int compareLong(long o1, long o2) {
		if (o1 < o2) {
			return -1;
		} else if (o1 == o2) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public String toString() {
		return "PriorityScheduler" + " --- " + super.toString();
	}

}
