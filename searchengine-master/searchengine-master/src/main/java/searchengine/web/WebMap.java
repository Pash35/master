package searchengine.web;

import searchengine.model.PageEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class WebMap extends RecursiveTask<HashMap<String, PageEntity>>{
    private String ref;
    public WebMap (String ref) {
        this.ref = ref;
    }

    @Override
    protected HashMap<String, PageEntity> compute() {

        //Map для возврата
        HashMap<String, PageEntity> refMap = new HashMap<>();

        //парсим файл в потоках
        WebParse webParse = new WebParse(ref);
        HashSet<String> refWebList = webParse.parse();

        //возвращаем текущую и выходим

        PageEntity page = new PageEntity();
        //записываем content и code
        page.setContent(webParse.getContent().toString());
        page.setCode(webParse.getCode());
        refMap.put(ref, page);

        //если ссылки есть запускаем новые потоки для них
        List<WebMap> taskList = new ArrayList<>();
        for (String ref : refWebList) {
            WebMap task = new WebMap(ref);
            task.fork();
            taskList.add(task);
        }

        //ждем возвращения потоков
        for (WebMap task : taskList) {
            if (Thread.currentThread().isInterrupted()) {
                return refMap;
            }
            refMap.putAll(task.join());
        }

        return refMap;
    }
}

