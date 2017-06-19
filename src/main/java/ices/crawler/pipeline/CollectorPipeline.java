package ices.crawler.pipeline;

import java.util.List;

/**
 * Created by Neuclil on 17-6-18.
 */
public interface CollectorPipeline<T> extends Pipeline{

    public List<T> getCollected();
}
