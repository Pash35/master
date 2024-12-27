package searchengine.web;

import lombok.Getter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
<<<<<<< HEAD
import searchengine.logger.ExceptionLogger;
=======
>>>>>>> c50fbabb287430063d38ef8b6a33f7a3358b3beb

import java.io.IOException;
import java.util.HashSet;

import static java.lang.Thread.sleep;

public class WebParse {

<<<<<<< HEAD
    private String url ;

=======
    private Document doc;
    public String url ;
    //private String regex = "[^#]+";//кроме внутренней ссылки
    private String regex = "[A-z0-9.]+/?";
    private HashSet<String> refList = new HashSet<>();
>>>>>>> c50fbabb287430063d38ef8b6a33f7a3358b3beb
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
<<<<<<< HEAD
        String regex = "[A-z0-9.]+/?";
        HashSet<String> refList = new HashSet<>();
        Document doc;
        Elements elements;

        regex = url + regex;
=======
        regex = url + regex;
        Elements elements;
>>>>>>> c50fbabb287430063d38ef8b6a33f7a3358b3beb

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

<<<<<<< HEAD
        } catch (InterruptedException | IOException i) {
            new ExceptionLogger("Interrupted in class WebParse");
=======
        } catch (InterruptedException i) {
            i.getStackTrace();
        } catch (IOException io) {
            io.getStackTrace();
>>>>>>> c50fbabb287430063d38ef8b6a33f7a3358b3beb
        }
        return refList;
    }
}
