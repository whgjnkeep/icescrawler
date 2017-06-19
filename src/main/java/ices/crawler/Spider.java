package ices.crawler;

import ices.crawler.downloader.Downloader;
import ices.crawler.downloader.HttpClientDownloader;
import ices.crawler.pipeline.CollectorPipeline;
import ices.crawler.pipeline.ConsolePipeline;
import ices.crawler.pipeline.Pipeline;
import ices.crawler.pipeline.ResultItemsCollectorPipeline;
import ices.crawler.processor.ResultDocumentProcessor;
import ices.crawler.scheduler.QueueScheduler;
import ices.crawler.scheduler.Scheduler;
import ices.crawler.utils.CountableThreadPool;
import ices.crawler.utils.UrlUtils;
import ices.crawler.utils.WMCollections;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Neuclil on 17-6-19.
 */
public class Spider implements Runnable, Task {

	private final static Logger LOGGER = Logger.getLogger(Spider.class);

	protected final static int STAT_INIT = 0;

	protected final static int STAT_RUNNING = 1;

	protected final static int STAT_STOPPED = 2;

	protected Downloader downloader;

	protected List<Pipeline> pipelines = new ArrayList<Pipeline>();

	protected ResultDocumentProcessor processor;

	protected List<Request> startRequests;

	protected TaskConfig config;

	protected String uuid;

	protected Scheduler scheduler = new QueueScheduler();

	protected CountableThreadPool threadPool;

	protected ExecutorService executorService;

	protected int threadNum = 1;

	protected AtomicInteger stat = new AtomicInteger(STAT_INIT);

	protected boolean spawnUrl = true;

	protected boolean destroyWhenExit = true;

	protected boolean exitWhenComplete = true;

	private ReentrantLock newUrlLock = new ReentrantLock();

	private Condition newUrlCondition = newUrlLock.newCondition();

	private List<SpiderListener> listeners;

	private final AtomicLong resultDocumentCount = new AtomicLong(0);

	private Date startTime;

	private int emptySleepTime = 30000;

	public static Spider create(ResultDocumentProcessor processor) {
		return new Spider(processor);
	}

	public Spider(ResultDocumentProcessor processor) {
		this.processor = processor;
		this.config = processor.getTaskConfig();
	}

	public Spider startUrls(List<String> startUrls) {
		checkIfRunning();
		this.startRequests = UrlUtils.convertToRequests(startUrls);
		return this;
	}

	public Spider startRequest(List<Request> startRequests) {
		checkIfRunning();
		this.startRequests = startRequests;
		return this;
	}

	public Spider setUUID(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public Spider setScheduler(Scheduler scheduler) {
		checkIfRunning();
		Scheduler oldScheduler = this.scheduler;
		this.scheduler = scheduler;
		if (oldScheduler != null) {
			Request request;
			while ((request = oldScheduler.poll(this)) != null) {
				this.scheduler.push(request, this);
			}
		}
		return this;
	}

	public Spider addPipeline(Pipeline pipeline) {
		checkIfRunning();
		this.pipelines.add(pipeline);
		return this;
	}

	public Spider setPipelines(List<Pipeline> pipelines) {
		checkIfRunning();
		this.pipelines = pipelines;
		return this;
	}

	public Spider clearPipeline() {
		pipelines = new ArrayList<Pipeline>();
		return this;
	}

	public Spider setDownloader(Downloader downloader) {
		checkIfRunning();
		this.downloader = downloader;
		return this;
	}

	protected void initComponent() {
		if (downloader == null) {
			this.downloader = new HttpClientDownloader();
		}
		if (scheduler == null) {
			this.scheduler = new QueueScheduler();
		}
		if (pipelines.isEmpty()) {
			pipelines.add(new ConsolePipeline());
		}
		downloader.setThread(threadNum);
		if (threadPool == null || threadPool.isShutdown()) {
			if (executorService != null && !executorService.isShutdown()) {
				threadPool = new CountableThreadPool(threadNum, executorService);
			} else {
				threadPool = new CountableThreadPool(threadNum);
			}
		}
		if (startRequests != null) {
			for (Request request : startRequests) {
				addRequest(request);
			}
			startRequests.clear();
		}
		startTime = new Date();
		LOGGER.info("Downloader: " + this.downloader +"\n"
				   +"Scheduler: " + this.scheduler +"\n"
				   +"Pipeline: " + this.pipelines +"\n");
	}

	private void addRequest(Request request) {
		if (config.getDomain() == null && request != null && request.getUrl() != null) {
			config.setDomain(UrlUtils.getDomain(request.getUrl()));
		}
		scheduler.push(request, this);
	}

	protected void checkIfRunning() {
		if (stat.get() == STAT_RUNNING) {
			throw new IllegalStateException("already running");
		}
	}

	@Override
	public String getUUID() {
		if (uuid != null) {
			return uuid;
		}

		if (config != null) {
			return config.getDomain();
		}

		uuid = UUID.randomUUID().toString();
		return uuid;
	}

	@Override
	public TaskConfig getTaskConfig() {
		return config;
	}

	@Override
	public void run() {
		checkRunningStat();
		initComponent();
		LOGGER.info("Spider {" + getUUID() + "} started!");
		while (!Thread.currentThread().isInterrupted() && stat.get() == STAT_RUNNING) {
			final Request request = scheduler.poll(this);
			if (request == null) {
				if (threadPool.getThreadAlive() == 0 && exitWhenComplete) {
					break;
				}
				// wait until new url added
				waitNewUrl();
			} else {
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						try {
							processRequest(request);
							onSuccess(request);
						} catch (Exception e) {
							onError(request);
							LOGGER.error("process request " + request + " error", e);
						} finally {
							resultDocumentCount.incrementAndGet();
							signalNewUrl();
						}
					}
				});
			}
		}
		stat.set(STAT_STOPPED);
		if (destroyWhenExit) {
			close();
		}
		LOGGER.info("Spider {" + getUUID() + "} closed! " + "{" + resultDocumentCount.get() + "}"
				+ " resultDocuments downloaded.");
	}

	private void close() {
		destroyEach(downloader);
		destroyEach(processor);
		destroyEach(scheduler);
		for (Pipeline pipeline : pipelines) {
			destroyEach(pipeline);
		}
		threadPool.shutdown();
	}

	private void destroyEach(Object object) {
		if (object instanceof Closeable) {
			try {
				((Closeable) object).close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void signalNewUrl() {
		try {
			newUrlLock.lock();
			newUrlCondition.signalAll();
		} finally {
			newUrlLock.unlock();
		}
	}

	private void processRequest(Request request) {
		ResultDocument resultDocument = downloader.download(request, this);
		if (resultDocument.isDownloadSuccess()) {
			onDownloadSuccess(request, resultDocument);
		} else {
			onDownloaderFail(request);
		}
	}

	private void onDownloaderFail(Request request) {
		if (config.getCycleRetryTimes() == 0) {
			sleep(config.getSleepTime());
		} else {
			doCycleRetry(request);
		}
		onError(request);
	}

	private void onError(Request request) {
		if (CollectionUtils.isNotEmpty(listeners)) {
			for (SpiderListener spiderListener : listeners) {
				spiderListener.onError(request);
			}
		}
	}

	private void doCycleRetry(Request request) {
		Object cycleTriedTimesObject = request.getExtra(Request.CYCLE_TRIED_TIMES);
		if (cycleTriedTimesObject == null) {
			addRequest(SerializationUtils.clone(request).setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES, 1));
		} else {
			int cycleTriedTimes = (Integer) cycleTriedTimesObject;
			cycleTriedTimes++;
			if (cycleTriedTimes < config.getCycleRetryTimes()) {
				addRequest(SerializationUtils.clone(request).setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES,
						cycleTriedTimes));
			}
		}
		sleep(config.getRetrySleepTime());
	}

	protected void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			LOGGER.error("Thread interrupted when sleep", e);
		}
	}

	private void onDownloadSuccess(Request request, ResultDocument resultDocument) {
		onSuccess(request);
		if (config.getAcceptStatCode().contains(resultDocument.getStatusCode())) {
			processor.process(resultDocument);
			extractAndAddRequests(resultDocument, spawnUrl);
			if (!resultDocument.getResultItems().isSkip()) {
				for (Pipeline pipeline : pipelines) {
					pipeline.process(resultDocument.getResultItems(), this);
				}
			}
		}
		sleep(config.getSleepTime());
		return;
	}

	private void extractAndAddRequests(ResultDocument resultDocument, boolean spawnUrl) {
		if (spawnUrl && CollectionUtils.isNotEmpty(resultDocument.getTargetRequests())) {
			for (Request request : resultDocument.getTargetRequests()) {
				addRequest(request);
			}
		}
	}

	private void onSuccess(Request request) {
		if (CollectionUtils.isNotEmpty(listeners)) {
			for (SpiderListener spiderListener : listeners) {
				spiderListener.onSuccess(request);
			}
		}
	}

	private void waitNewUrl() {
		newUrlLock.lock();
		try {
			if (threadPool.getThreadAlive() == 0 && exitWhenComplete) {
				return;
			}
			newUrlCondition.await(emptySleepTime, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			LOGGER.warn("waitNewUrl - interrupted error!", e);
		} finally {
			newUrlLock.unlock();
		}
	}

	private void checkRunningStat() {
		while (true) {
			int statNow = stat.get();
			if (statNow == STAT_RUNNING) {
				throw new IllegalStateException("Spider is already running!");
			}
			if (stat.compareAndSet(statNow, STAT_RUNNING)) {
				break;
			}
		}
	}

	public void runAsync() {
		Thread thread = new Thread(this);
		thread.setDaemon(false);
		thread.start();
	}

	public Spider addUrl(String... urls) {
		for (String url : urls) {
			addRequest(new Request(url));
		}
		signalNewUrl();
		return this;
	}

	public <T> List<T> getAll(Collection<String> urls) {
		destroyWhenExit = false;
		spawnUrl = false;
		if (startRequests != null) {
			startRequests.clear();
		}
		for (Request request : UrlUtils.convertToRequests(urls)) {
			addRequest(request);
		}
		CollectorPipeline collectorPipeline = getCollectorPipeline();
		pipelines.add(collectorPipeline);
		run();
		spawnUrl = true;
		destroyWhenExit = true;
		return collectorPipeline.getCollected();
	}

	protected CollectorPipeline getCollectorPipeline() {
		return new ResultItemsCollectorPipeline();
	}

	public <T> T get(String url) {
		List<String> urls = WMCollections.newArrayList(url);
		List<T> resultItemses = getAll(urls);
		if (resultItemses != null && resultItemses.size() > 0) {
			return resultItemses.get(0);
		} else {
			return null;
		}
	}

	public void stop() {
		if (stat.compareAndSet(STAT_RUNNING, STAT_STOPPED)) {
			LOGGER.info("Spider " + getUUID() + " stop success!");
		} else {
			LOGGER.info("Spider " + getUUID() + " stop fail!");
		}
	}

	public void start() {
		runAsync();
	}

	public Spider setThread(int threadNum) {
		checkIfRunning();
		this.threadNum = threadNum;
		if (threadNum <= 0) {
			throw new IllegalArgumentException("threadNum should be more than one!");
		}
		return this;
	}

	public Spider setThread(ExecutorService executorService, int threadNum) {
		checkIfRunning();
		this.threadNum = threadNum;
		if (threadNum <= 0) {
			throw new IllegalArgumentException("threadNum should be more than one!");
		}
		this.executorService = executorService;
		return this;
	}

	public boolean isExitWhenComplete() {
		return exitWhenComplete;
	}

	/**
	 * Exit when complete. 
	 * True: exit when all url of the site is downloaded.
	 * False: not exit until call stop() manually.
	 * @param exitWhenComplete
	 * @return
	 */
	public Spider setExitWhenComplete(boolean exitWhenComplete) {
		this.exitWhenComplete = exitWhenComplete;
		return this;
	}

	public boolean isSpawnUrl() {
		return spawnUrl;
	}

	public long getResultDocumentCount() {
		return resultDocumentCount.get();
	}

	public Status getStatus() {
		return Status.fromValue(stat.get());
	}

	public enum Status {
		Init(0), Running(1), Stopped(2);

		private Status(int value) {
			this.value = value;
		}

		private int value;

		int getValue() {
			return value;
		}

		public static Status fromValue(int value) {
			for (Status status : Status.values()) {
				if (status.getValue() == value) {
					return status;
				}
			}
			// default value
			return Init;
		}
	}

	/**
	 * Get thread count which is running
	 * @return
	 */
	public int getThreadAlive() {
		if (threadPool == null) {
			return 0;
		}
		return threadPool.getThreadAlive();
	}

	public Spider setSpawnUrl(boolean spawnUrl) {
		this.spawnUrl = spawnUrl;
		return this;
	}

	public Spider setExecutorService(ExecutorService executorService) {
		checkIfRunning();
		this.executorService = executorService;
		return this;
	}

	public List<SpiderListener> getSpiderListeners() {
		return listeners;
	}

	public Spider setSpiderListeners(List<SpiderListener> listeners) {
		this.listeners = listeners;
		return this;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	/**
	 * Set wait time when no url is polled.
	 * @param emptySleepTime
	 */
	public void setEmptySleepTime(int emptySleepTime) {
		this.emptySleepTime = emptySleepTime;
	}
}
