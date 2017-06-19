package downloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by Neuclil on 17-6-13.
 */
public class JsoupParser {

    public static void testParseTitle(String html) {
        Document doc = Jsoup.parse(html);
        String title = doc.title();
        System.out.println("title :" + title);
    }

    public static void testParseLinks(String html) {
        Document doc = Jsoup.parse(html);
        Elements links = doc.select("a[href]");
        for(Element link : links){
            System.out.println("\nlink :" + link.attr("href"));
            System.out.println("text: " + link.text());
        }
    }

    public static void testExtractImage(String html) {
        Document doc = Jsoup.parse(html);
        System.out.println("Getting all images...");
        Elements images = doc.getElementsByTag("img");
        for(Element image : images){
            System.out.println("img: " + image.attr("abs:src"));
        }
    }
}
