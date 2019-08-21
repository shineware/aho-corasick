package kr.co.shineware.ds.aho_corasick;

import kr.co.shineware.ds.aho_corasick_hash.AhoCorasickDictionary;
import kr.co.shineware.ds.aho_corasick_hash.FindContext;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AhoCorasickHashDictionaryTest {

	private static final int DATA_NUM = 100000;
	private static String[] KEYS;
	private static Integer[] VALS;

	private static final AhoCorasickDictionary<Integer> dic = new AhoCorasickDictionary<>();

	@BeforeClass
	public static void setup() {
		KEYS = new String[DATA_NUM];
		VALS = new Integer[DATA_NUM];

		for (int i = 0; i < KEYS.length; i++) {
			KEYS[i] = Integer.toString(i);
			VALS[i] = i;
		}

		for (int i = 0; i < KEYS.length; i++) {
			dic.put(KEYS[i], VALS[i]);
		}
		dic.buildFailLink();
	}

	@Test
	public void testGetWithSingleChar() {
		for (int i = 0; i < KEYS.length; i++) {
			final FindContext<Integer> context = dic.newFindHashContext();
			final String key = KEYS[i];

			for (int j = 0; j < key.length(); j++) {
				final String subKey = key.substring(0, j + 1);
				assertEquals(expectedResultMapForGetWithSingleChar(subKey), dic.get(context, key.charAt(j)));
			}
		}
	}

	private static Map<String, Integer> expectedResultMapForGetWithSingleChar(final String key) {
		final Map<String, Integer> expectedResult = new HashMap<>();
		final int len = key.length();
		for (int i = 0; i < len; i++) {
			final String subKey = key.substring(i, len);
			if (subKey.equals("0") || subKey.charAt(0) != '0') {
				expectedResult.put(subKey, Integer.parseInt(subKey));
			}
		}
		return expectedResult;
	}

	@Test
	public void testNotFound() {
		assertEquals(new HashMap<>(), dic.get(dic.newFindHashContext(), "가"));
	}
}
