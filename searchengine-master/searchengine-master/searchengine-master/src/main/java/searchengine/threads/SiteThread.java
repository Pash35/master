package searchengine.threads;

import lombok.RequiredArgsConstructor;
import searchengine.model.PageEntity;
import searchengine.services.SiteService;
import searchengine.web.WebMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@RequiredArgsConstructor
public class SiteThread extends Thread{

    private String ref;
    private HashMap<String, PageEntity> mapFile;
    private final SiteService siteService;

    public void setRef(String ref) {
        this.ref = ref;
    }

    @Override
    public void run() {

        HashMap<String, PageEntity> hashMap = new HashMap<>();
        try {
            ForkJoinPool pool = new ForkJoinPool();
            WebMap webMap = new WebMap(ref);
            mapFile = pool.invoke(webMap);
            //заносим в базу данные со страниц сайтов
            for (Map.Entry<String, PageEntity> entry : mapFile.entrySet()) {
                if (!Thread.currentThread().isInterrupted() && entry.getValue().getCode() != null) {
                    hashMap.put(entry.getKey(), entry.getValue());
                } else if (entry.getValue().getCode() == null) {
                    throw new IOException("Ошибка индексации: главная \n" +
                            "страница сайта недоступна\n");
                } else {
                    //выбрасываем исключение при запросе на останов потока
                    throw new InterruptedException("Индексация остановлена пользователем");
                }
            }
            siteService.update(ref, hashMap);
        } catch (IOException | InterruptedException io) {
            siteService.updateError(ref, io.getMessage());
        }

    }

}
