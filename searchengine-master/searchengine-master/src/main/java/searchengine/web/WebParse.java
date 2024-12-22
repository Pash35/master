package searchengine.web;

import lombok.Getter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;

import static java.lang.Thread.sleep;

public class WebParse {

    private Document doc;
    public String url ;
    //private String regex = "[^#]+";//кроме внутренней ссылки
    private String regex = "[A-z0-9.]+/?";
    private HashSet<String> refList = new HashSet<>();
    @Getter
    private StringBuffer content = new StringBuffer();
    @Getter
    private Integer code;
    public WebParse(String url) {
        this.url = url;
    }


    //парсим сайт
    public HashSet<String> parse(){

        // к текущей ссылке добавляем regex пример:
        // https://skillbox.ru/[A-z0-9]+/?
        // следующий поток -> https://skillbox.ru/courses/[A-z0-9]+/?
        // и т.д.
        regex = url + regex;
        Elements elements;

        try {
            sleep(150);
            Connection.Response response;
            response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .execute();
            doc = response.parse();

            //находим в теле сайта ссылки, складываем в HashSet чтобы не повторялись
            elements = doc.select("body").select("a");
            if (!elements.isEmpty()) {
                for (Element element : elements) {
                    String ref = element.absUrl("href");
                    if (ref.matches(regex)) {
                        refList.add(ref);
                    }
                }
            }
            //записываем статус и содержимое сайта
            code = response.statusCode();
            content.append(doc.html());

        } catch (InterruptedException i) {
            i.getStackTrace();
        } catch (IOException io) {
            io.getStackTrace();
        }
        return refList;
    }
}
