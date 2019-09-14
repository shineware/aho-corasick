package kr.co.shineware.ds.aho_corasick;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AhoCorasickSpeedTest {
    private AhoCorasickDictionary<List<String>> ahoCorasickDictionary;
    private Map<String, List<String>> dictionaryMap;

    @Before
    public void setup() throws IOException {
        System.out.println("Load dictionary");
        this.ahoCorasickDictionary = new AhoCorasickDictionary<>();
        this.dictionaryMap = new HashMap<>();
        loadWordDictionary("dic.word");
        putWordDictionaryToAhocorasickDictionary();
        System.out.println("Load done");
        this.ahoCorasickDictionary.buildFailLink();
    }

    private void putWordDictionaryToAhocorasickDictionary() {
        for (String word : this.dictionaryMap.keySet()) {
            this.ahoCorasickDictionary.put(word, this.dictionaryMap.get(word));
        }
    }

    private Map<String, List<String>> loadWordDictionary(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));

        String line;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            String[] tokenEntry = line.split("\t");

            String word = Arrays.asList(tokenEntry).get(0);
            dictionaryMap.put(word, Arrays.asList(tokenEntry).subList(1, tokenEntry.length));
        }

        br.close();
        return dictionaryMap;
    }

    @Test
    public void speedTest() throws Exception {
        Collection<String> wikiTitleWords = getWikiTitleEojeol();
        long begin = System.currentTimeMillis();
        int totalCount = 10;
        Set<String> wordSet = this.dictionaryMap.keySet();

        for (int i = 0; i < 10; i++) {
            for (String word : wordSet) {
                Map<String, List<String>> morphList = this.ahoCorasickDictionary.get(word);
            }

            for (String word : wikiTitleWords) {
                Map<String, List<String>> morphList = this.ahoCorasickDictionary.get(word);
            }
        }

        long end = System.currentTimeMillis();
        System.out.println((end - begin) / totalCount + "ms");
    }

    private Collection<String> getWikiTitleEojeol() throws Exception {
        Collection<String> eojeolList = new HashSet<>();
        BufferedReader br = new BufferedReader(new FileReader("wiki.titles"));

        String line;

        while ((line = br.readLine()) != null) {
            eojeolList.addAll(Arrays.asList(line.split(" ")));
        }
        br.close();
        return eojeolList;
    }
}
