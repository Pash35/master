package searchengine;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import searchengine.config.LemmasFromText;

import java.io.IOException;
import java.util.List;

public class TestMain {
    public static void main(String[] args) throws IOException {



        LemmasFromText lemmas = new LemmasFromText();
        lemmas.mapLemmas("Повторное появление леопарда в Осетии позволяет предположить, " +
                "что леопард постоянно обитает в некоторых районах Северного Кавказа.");

        System.out.println(lemmas.mapLemmas("Повторное появление леопарда в Осетии позволяет предположить, " +
                "что леопард постоянно обитает в некоторых районах Северного Кавказа."));

    }
}
