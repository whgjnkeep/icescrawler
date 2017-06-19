package ices.crawler.selector;

import org.apache.commons.collections.CollectionUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neuclil on 17-6-15.
 */
public class CssSelector extends BaseElementSelector {

    private String cssQuery;

    private String attrName;

    public CssSelector(String cssQuery) {
        this.cssQuery = cssQuery;
    }

    public CssSelector(String cssQuery, String attrName) {
        this.cssQuery = cssQuery;
        this.attrName = attrName;
    }

    private String getAttrValue(Element element) {
        if (attrName == null) {
            return element.outerHtml();
        } else if ("innerHtml".equalsIgnoreCase(attrName)) {
            return element.html();
        } else if ("text".equalsIgnoreCase(attrName)) {
            return getText(element);
        } else if ("allText".equalsIgnoreCase(attrName)) {
            return element.text();
        } else {
            return element.attr(attrName);
        }
    }

    protected String getText(Element element) {
        StringBuilder accm = new StringBuilder();
        for (Node node : element.childNodes()) {
            if (node instanceof TextNode) {
                TextNode textNode = (TextNode) node;
                accm.append(textNode.text());
            }
        }
        return accm.toString();
    }

    @Override
    public String selectFirstString(Element element) {
        List<Element> elements = selectAllElements(element);
        if (CollectionUtils.isEmpty(elements)) {
            return null;
        }
        return getAttrValue(elements.get(0));
    }

    @Override
    public List<String> selectAllStrings(Element element) {
        List<String> strings = new ArrayList<String>();
        List<Element> elements = selectAllElements(element);
        if (CollectionUtils.isNotEmpty(elements)) {
            for (Element el : elements) {
                String value = getAttrValue(el);
                if (value != null) {
                    strings.add(value);
                }
            }
        }
        return strings;
    }

    @Override
    public Element selectFirstElement(Element element) {
        Elements elements = element.select(cssQuery);
        if(CollectionUtils.isNotEmpty(elements)){
            return elements.get(0);
        }
        return null;
    }

    @Override
    public List<Element> selectAllElements(Element element) {
        return element.select(cssQuery);
    }

    @Override
    public boolean hasAttribute() {
        return attrName != null;
    }
}
