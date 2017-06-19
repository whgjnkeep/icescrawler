package ices.crawler.selector;

import java.util.List;

/**
 * Created by Neuclil on 17-6-15.
 */
public interface Selector {

    String selectFirstString(String text);

    List<String> selectAllStrings(String text);
}
