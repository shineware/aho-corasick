package kr.co.shineware.ds.aho_corasick;

import kr.co.shineware.ds.aho_corasick.model.hash.AhoCorasickHashDictionary;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AhoCorasickUnitTest {
    @Test
    public void test() {

        List<Integer> integerList = new ArrayList<>();
        integerList.add(1);
        integerList.add(10);
        integerList.add(110);
//        integerList.add(2);
//        integerList.add(20);
//        integerList.add(100);

        AhoCorasickHashDictionary<Integer> dic = new AhoCorasickHashDictionary<>();
        for (Integer integer : integerList) {
            dic.put(integer.toString(), integer);
        }

        dic.buildFailLink();

        for (Integer integer : integerList) {
            System.out.println(integer);
            System.out.println(dic.get(integer.toString()));
        }

    }


    @Test
    public void test_legacy(){
        List<Integer> integerList = new ArrayList<>();
        integerList.add(1);
        integerList.add(10);
        integerList.add(110);
//        integerList.add(2);
//        integerList.add(20);
//        integerList.add(100);

        AhoCorasickDictionary<Integer> dic = new AhoCorasickDictionary<>();
        for (Integer integer : integerList) {
            dic.put(integer.toString(), integer);
        }

        dic.buildFailLink();

        for (Integer integer : integerList) {
            System.out.println(integer);
            System.out.println(dic.get(integer.toString()));
        }
    }
}
