package ices.crawler.pipeline;

import ices.crawler.ResultItems;
import ices.crawler.Task;
import ices.crawler.utils.Constants;
import ices.crawler.utils.FilePersistentBase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by Neuclil on 17-6-18.
 */
public class FilePipeline extends FilePersistentBase implements Pipeline {

    public FilePipeline(String path) {
        setPath(path);
    }

    public FilePipeline() {
        this(Constants.DEFAULT_FILEPAHT);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        String path = this.path + PATH_SEPERATOR + task.getUUID() + PATH_SEPERATOR;
        int count = 1;
        try {
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(getFile(path + count + ".html")), "UTF-8"));
            count++;
            printWriter.println("url:\t" + resultItems.getRequest().getUrl());
            for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
                if (entry.getValue() instanceof Iterable) {
                    Iterable value = (Iterable) entry.getValue();
                    printWriter.println(entry.getKey() + ":");
                    for (Object o : value) {
                        printWriter.println(o);
                    }
                } else {
                    printWriter.println(entry.getKey() + ":\t" + entry.getValue());
                }
            }
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
