package ices.crawler.selector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neuclil on 17-6-15.
 */
public abstract class BaseElementSelector implements Selector, ElementSelector {

    @Override
    public String selectFirstString(String text) {
        if (text != null) {
            return selectFirstString(Jsoup.parse(text));
        }
        return null;
    }

    @Override
    public List<String> selectAllStrings(String text) {
        if(text != null){
            return selectAllStrings(Jsoup.parse(text));
        }else{
            return new ArrayList<String>();
        }
    }

    public abstract Element selectFirstElement(Element element);

    public abstract List<Element> selectAllElements(Element element);

    public abstract boolean hasAttribute();
}
