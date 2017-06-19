package ices.crawler.selector;

/**
 * Created by Neuclil on 17-6-15.
 */
public class SelectorFactory {

    public static CssSelector css(String cssQuery){
        return new CssSelector(cssQuery);
    }

    public static CssSelector css(String cssQuery, String attrName){
        return new CssSelector(cssQuery, attrName);
    }
}
