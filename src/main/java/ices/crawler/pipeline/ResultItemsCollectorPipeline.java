package ices.crawler.pipeline;

import ices.crawler.ResultItems;
import ices.crawler.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neuclil on 17-6-18.
 */
public class ResultItemsCollectorPipeline implements CollectorPipeline<ResultItems>{

    private List<ResultItems> collector = new ArrayList<ResultItems>();

    @Override
    public void process(ResultItems resultItems, Task task) {
        collector.add(resultItems);
    }

    @Override
    public List<ResultItems> getCollected() {
        return collector;
    }
}
