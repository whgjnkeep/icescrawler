package ices.crawler.selector;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;

import java.util.Collections;
import java.util.List;

/**
 * Created by Neuclil on 17-6-15.
 */
public class Html extends HtmlNode {

    private static volatile boolean INITED = false;

    public static boolean DISABLE_HTML_ENTITY_ESCAPE = false;

    private void disableJsoupHtmlEntityEscape() {
        if (DISABLE_HTML_ENTITY_ESCAPE && !INITED) {
            Entities.EscapeMode.base.getMap().clear();
            Entities.EscapeMode.extended.getMap().clear();
            Entities.EscapeMode.xhtml.getMap().clear();
            INITED = true;
        }
    }

    private Document document;

    public Html(String text, String url) {
        try {
            disableJsoupHtmlEntityEscape();
            this.document = Jsoup.parse(text, url);
        } catch (Exception e) {
            this.document = null;
            e.printStackTrace();
        }
    }

    public Html(String text) {
        try {
            disableJsoupHtmlEntityEscape();
            this.document = Jsoup.parse(text);
        } catch (Exception e) {
            this.document = null;
            e.printStackTrace();
        }
    }

    public Html(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    @Override
    protected List<Element> getElements() {
        return Collections.<Element>singletonList(getDocument());
    }

    public String selectDocument(Selector selector) {
        if (selector instanceof ElementSelector) {
            ElementSelector elementSelector = (ElementSelector) selector;
            return elementSelector.selectFirstString(getDocument());
        } else {
            return selector.selectFirstString(getFirstSourceText());
        }
    }

    public List<String> selectDocumentForList(Selector selector) {
        if (selector instanceof ElementSelector) {
            ElementSelector elementSelector = (ElementSelector) selector;
            return elementSelector.selectAllStrings(getDocument());
        }else{
            return selector.selectAllStrings(getFirstSourceText());
        }
    }

    public static Html create(String text){
        return new Html(text);
    }
}
