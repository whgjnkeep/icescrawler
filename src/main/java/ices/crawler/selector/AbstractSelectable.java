package ices.crawler.selector;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neuclil on 17-6-15.
 */
public abstract class AbstractSelectable implements Selectable{

    protected abstract List<String> getSourceTexts();

    protected Selectable selectFirst(Selector selector, List<String> strings){
        List<String> results = new ArrayList<String>();
        for(String string : strings){
            String result = selector.selectFirstString(string);
            if(result != null)
                results.add(result);
        }
        return new PlainText(results);
    }

    protected Selectable selectAll(Selector selector, List<String> strings){
        List<String> results = new ArrayList<String>();
        for(String string : strings){
            List<String> result = selector.selectAllStrings(string);
            results.addAll(result);
        }
        return new PlainText(results);
    }

    @Override
    public List<String> getResults() {
        return getSourceTexts();
    }

    @Override
    public String getResult() {
        if(CollectionUtils.isNotEmpty(getResults())){
            return getResults().get(0);
        }else{
            return null;
        }
    }

    @Override
    public Selectable selectFirst(Selector selector) {
        return selectFirst(selector, getSourceTexts());
    }

    @Override
    public Selectable selectAll(Selector selector) {
        return selectAll(selector, getSourceTexts());
    }

    public String getFirstSourceText(){
        if(getSourceTexts() != null && getSourceTexts().size() > 0){
            return getSourceTexts().get(0);
        }
        return null;
    }

    @Override
    public boolean match() {
        return getSourceTexts() != null && getSourceTexts().size() > 0;
    }

    @Override
    public String toString() {
        return getResult();
    }
}
