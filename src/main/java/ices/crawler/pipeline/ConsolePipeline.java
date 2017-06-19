package ices.crawler.pipeline;

import ices.crawler.ResultItems;
import ices.crawler.Task;

import java.util.Map;

/**
 * Created by Neuclil on 17-6-18.
 */
public class ConsolePipeline implements Pipeline{

    @Override
    public void process(ResultItems resultItems, Task task) {
        if(resultItems.getRequest() == null)
        System.out.println("get page: " + resultItems.getRequest().getUrl());
        for (Map.Entry<String, Object> entry :resultItems.getAll().entrySet()) {
            System.out.println(entry.getKey() + ":\t" + entry.getValue());
        }
    }

	@Override
	public String toString() {
		return "ConsolePipeline";
	}
}
