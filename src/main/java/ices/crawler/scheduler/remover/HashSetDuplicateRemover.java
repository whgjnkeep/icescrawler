package ices.crawler.scheduler.remover;

import ices.crawler.Request;
import ices.crawler.Task;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Neuclil on 17-6-18.
 */
public class HashSetDuplicateRemover implements DuplicateRemover{

    private Set<String> urls = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @Override
    public boolean isDuplicate(Request request, Task task) {
        return !urls.add(getUrl(request));
    }

    protected String getUrl(Request request){
        return request.getUrl();
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        urls.clear();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return urls.size();
    }

	@Override
	public String toString() {
		return "HashSetDuplicateRemover";
	}
    
}
