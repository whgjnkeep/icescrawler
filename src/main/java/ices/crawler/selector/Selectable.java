package ices.crawler.selector;

import java.util.List;

/**
 * Created by Neuclil on 17-6-15.
 */
public interface Selectable {

    Selectable selectFirst(Selector selector);

    Selectable selectAll(Selector selector);

    Selectable css(String selector);

    Selectable css(String selector, String attrName);

    String getResult();

    List<String> getResults();

    String toString();

    boolean match();

    List<Selectable> nodes();
}
