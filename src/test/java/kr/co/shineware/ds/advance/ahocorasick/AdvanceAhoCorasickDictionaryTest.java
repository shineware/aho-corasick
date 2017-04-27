package kr.co.shineware.ds.advance.ahocorasick;

import kr.co.shineware.ds.parser.KoreanUnitParser;
import kr.co.shineware.ds.parser.model.UnitToken;
import kr.co.shineware.ds.parser.model.UnitTokenList;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by shin285 on 2017. 4. 19..
 */
public class AdvanceAhoCorasickDictionaryTest {

	private static final AdvanceAhoCorasickDictionary<UnitToken,String> advanceAhoCorasickDictionary
			= new AdvanceAhoCorasickDictionary<>();
	private KoreanUnitParser koreanUnitParser = new KoreanUnitParser();

	@Before
	public void setup(){

		advanceAhoCorasickDictionary.put(getUnitTokenList("되"),"NNG");

		advanceAhoCorasickDictionary.put(getUnitTokenList("감"),"NNG");
		advanceAhoCorasickDictionary.put(getUnitTokenList("ㅁ"),"NNG");
		advanceAhoCorasickDictionary.put(getUnitTokenList("기"),"NNG");
		advanceAhoCorasickDictionary.buildFailLink();
		System.out.println("Done");
	}

	private List<UnitToken> getUnitTokenList(String text) {
		return koreanUnitParser.parseToJasoList(text);
	}

	@Test
	public void getTest(){

		AdvanceFindContext<UnitToken,String> context = advanceAhoCorasickDictionary.newFindContext();
		Map<List<UnitToken>,String> result = advanceAhoCorasickDictionary.get(context,getUnitTokenList("됨"));
		Set<List<UnitToken>> set = result.keySet();
		for (List<UnitToken> unitTokenList : set) {
			UnitTokenList utl = new UnitTokenList(unitTokenList);
			System.out.println(utl.getPlainText());
			System.out.println(result.get(unitTokenList));
			System.out.println();
		}

		System.out.println(result);
	}

}