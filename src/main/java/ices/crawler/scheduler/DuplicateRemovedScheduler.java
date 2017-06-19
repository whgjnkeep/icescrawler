package ices.crawler.scheduler;

import org.apache.log4j.Logger;

import ices.crawler.Request;
import ices.crawler.Task;
import ices.crawler.scheduler.remover.DuplicateRemover;
import ices.crawler.scheduler.remover.HashSetDuplicateRemover;
import ices.crawler.utils.HttpConstant;

/**
 * Created by Neuclil on 17-6-18.
 */
public abstract class DuplicateRemovedScheduler implements Scheduler {

	private final static Logger LOGGER = Logger.getLogger(DuplicateRemovedScheduler.class);

	private DuplicateRemover duplicateRemover = new HashSetDuplicateRemover();

	public DuplicateRemover getDuplicateRemover() {
		return duplicateRemover;
	}

	public DuplicateRemovedScheduler setDuplicateRemover(DuplicateRemover duplicateRemover) {
		this.duplicateRemover = duplicateRemover;
		return this;
	}

	@Override
	public void push(Request request, Task task) {
		LOGGER.trace("get a candidate url {" + request.getUrl() + "}");
		if (shouldReserved(request) || noNeedToRemoveDuplicate(request)
				|| !duplicateRemover.isDuplicate(request, task)) {
			LOGGER.debug("push to queue {" + request.getUrl() + "}");
			pushWhenNoDuplicate(request, task);
		}
	}

	protected boolean shouldReserved(Request request) {
		return request.getExtra(Request.CYCLE_TRIED_TIMES) != null;
	}

	protected boolean noNeedToRemoveDuplicate(Request request) {
		return HttpConstant.Method.POST.equalsIgnoreCase(request.getMethod());
	}

	protected void pushWhenNoDuplicate(Request request, Task task) {

	}

	@Override
	public String toString() {
		return "DuplicateRemovedScheduler [duplicateRemover=" + duplicateRemover + "]";
	}
	
}
