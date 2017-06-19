package ices.crawler.selector;

import org.jsoup.nodes.Element;

import java.util.List;

/**
 * Created by Neuclil on 17-6-15.
 */
public interface ElementSelector {

    String selectFirstString(Element element);

    List<String> selectAllStrings(Element element);
}
