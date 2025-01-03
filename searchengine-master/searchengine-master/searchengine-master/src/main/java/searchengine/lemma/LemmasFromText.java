package searchengine.lemma;


import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import searchengine.logger.ExceptionLogger;

import java.io.IOException;
import java.util.*;


public class LemmasFromText {
    LuceneMorphology luceneMorph = new RussianLuceneMorphology();
    private static final String[] particlesNames = new String[]{"МЕЖД", "ПРЕДЛ", "СОЮЗ"};

    public LemmasFromText() throws IOException {
    }
    //списое лемм из строки
    public List<String> listLemma(String word) {
        List<String> wordBaseForms;
        try {
            wordBaseForms =
                    luceneMorph.getNormalForms(word);
        }
        catch (Exception e) {
            new ExceptionLogger("Class ' LuceneMorphology' returned an error in word - " + word);
            return new ArrayList<>();
        }
        return  wordBaseForms;
    }

    //список лемм и количества повторов из строки
    public HashMap<String, Integer> mapLemmas(String text) {
        HashMap<String, Integer> map = new HashMap<>();

        String[] words = text.toLowerCase()
                .replaceAll("([^а-я\\s])", " ")
                .trim().split("\s+");

        for (String word: words) {
            if (word.isBlank()) {
                continue;
            }

                List<String> wordBaseForms;
            try {
                wordBaseForms =
                        luceneMorph.getMorphInfo(word);
            }
            catch (Exception e) {
                new ExceptionLogger("Class ' LuceneMorphology' returned an error in word - " + word);
                continue;
            }

            if (wordBaseForms.stream().anyMatch(this::hasParticleProperty)) {
                continue;
            }

            List<String> wordBaseForms1 =
                luceneMorph.getNormalForms(word);

            String normalWord = wordBaseForms1.get(0);

            if (map.containsKey(normalWord)) {
                map.put(normalWord, map.get(normalWord) + 1);
            } else {
                map.put(normalWord, 1);
            }
        }
        return map;
    }

    private boolean hasParticleProperty(String word) {
        for (String property : particlesNames) {
            if (word.toUpperCase().contains(property)) {
                return true;
            }
        }
        return false;
    }


}
