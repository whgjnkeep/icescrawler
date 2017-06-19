package processor;

import ices.crawler.ResultDocument;
import ices.crawler.Spider;
import ices.crawler.TaskConfig;
import ices.crawler.pipeline.ConsolePipeline;
import ices.crawler.pipeline.FilePipeline;
import ices.crawler.processor.ResultDocumentProcessor;

/**
 * Created by Neuclil on 17-6-19.
 */
public class WebmagicProcessor implements ResultDocumentProcessor{

    private TaskConfig config = TaskConfig.custom()
                                          .setSleepTime(1000);

    @Override
    public void process(ResultDocument resultDocument) {
        resultDocument.putField("title",
                resultDocument.getHtml().css("title").toString());
        resultDocument.putField("id",
                resultDocument.getHtml().css("#121-webmagic的四个组件").toString());
    }

    @Override
    public TaskConfig getTaskConfig() {
        return config;
    }

    public static void main(String[] args) {
        Spider spider = new Spider(new WebmagicProcessor())
                                    .addPipeline(new ConsolePipeline())
                                    .addPipeline(new FilePipeline())
                                    .addUrl("http://webmagic.io/docs/zh/posts/ch1-overview/architecture.html");
        spider.start();
    }
}
