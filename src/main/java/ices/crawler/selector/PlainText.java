package ices.crawler.selector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neuclil on 17-6-15.
 */
public class PlainText extends AbstractSelectable{

    protected List<String> sourceTexts;

    public PlainText(List<String> sourceTexts) {
        this.sourceTexts = sourceTexts;
    }

    public PlainText(String text) {
        this.sourceTexts = new ArrayList<String>();
        sourceTexts.add(text);
    }

    public static PlainText create(String text) {
        return new PlainText(text);
    }

    @Override
    public Selectable selectFirst(Selector selector) {
        return null;
    }

    @Override
    public Selectable selectAll(Selector selector) {
        return null;
    }

    @Override
    protected List<String> getSourceTexts() {
        return null;
    }

    @Override
    public Selectable css(String selector) {
        return null;
    }

    @Override
    public Selectable css(String selector, String attrName) {
        return null;
    }

    @Override
    public String getResult() {
        return null;
    }

    @Override
    public List<String> getResults() {
        return null;
    }

    @Override
    public List<Selectable> nodes() {
        List<Selectable> nodes = new ArrayList<Selectable>(getSourceTexts().size());
        for (String string : getSourceTexts()) {
            nodes.add(PlainText.create(string));
        }
        return nodes;
    }
}
