package ices.crawler.selector;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Neuclil on 17-6-15.
 */
public class HtmlNode extends AbstractSelectable {

    private final List<Element> elements;

    public HtmlNode(List<Element> elements) {
        this.elements = elements;
    }

    public HtmlNode() {
        elements = null;
    }

    protected List<Element> getElements() {
        return elements;
    }

    @Override
    public Selectable selectFirst(Selector selector) {
        return selectAll(selector);
    }

    @Override
    public Selectable selectAll(Selector selector) {
        if (selector instanceof BaseElementSelector) {
            return selectAllElements((BaseElementSelector) selector);
        }
        return selectAll(selector, getSourceTexts());
    }

    protected Selectable selectAllElements(BaseElementSelector elementSelector) {
        ListIterator<Element> elementIterator = getElements().listIterator();
        if (!elementSelector.hasAttribute()) {
            List<Element> resultElements = new ArrayList<Element>();
            while (elementIterator.hasNext()) {
                Element element = checkElementAndConvert(elementIterator);
                List<Element> elements = elementSelector.selectAllElements(element);
                resultElements.addAll(elements);
            }
            return new HtmlNode(resultElements);
        } else {
            List<String> resultStrings = new ArrayList<String>();
            while(elementIterator.hasNext()){
                Element element = checkElementAndConvert(elementIterator);
                List<String> strings = elementSelector.selectAllStrings(element);
                resultStrings.addAll(strings);
            }
            return new PlainText(resultStrings);
        }
    }

    private Element checkElementAndConvert(ListIterator<Element> elementIterator) {
        Element element = elementIterator.next();
        if (!(element instanceof Document)) {
            Document root = new Document(element.ownerDocument().baseUri());
            Element clone = element.clone();
            root.appendChild(clone);
            elementIterator.set(root);
            return root;
        }
        return element;
    }

    @Override
    public Selectable css(String selector) {
        CssSelector cssSelector = SelectorFactory.css(selector);
        return selectAllElements(cssSelector);
    }

    @Override
    public Selectable css(String selector, String attrName) {
        CssSelector cssSelector = SelectorFactory.css(selector, attrName);
        return selectAllElements(cssSelector);
    }

    @Override
    public List<Selectable> nodes() {
        List<Selectable> selectables = new ArrayList<Selectable>();
        for(Element element : getElements()){
            List<Element> childElements = new ArrayList<Element>(1);
            childElements.add(element);
            selectables.add(new HtmlNode(childElements));
        }
        return selectables;
    }

    @Override
    protected List<String> getSourceTexts() {
        List<String> sourceTexts = new ArrayList<String>(getElements().size());
        for(Element element : getElements()){
            sourceTexts.add(element.toString());
        }
        return sourceTexts;
    }

}
