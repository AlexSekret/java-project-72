package hexlet.code.utils;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public final class Util {

    public static String getFirstElementText(Document document, String tagName) {
        Elements elements = document.getElementsByTag(tagName);
        if (!elements.isEmpty()) {
            return elements.first().text();
        } else {
            return "";
        }
    }

    public static String getMetaContent(Document document) {
        Elements metaElements = document.select("meta[name=" + "description" + "]");
        if (!metaElements.isEmpty()) {
            return metaElements.first().attr("content");
        } else {
            return "";
        }
    }
}
