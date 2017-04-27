package kr.co.shineware.ds.parser.model;

import java.util.List;

/**
 * Created by shin285 on 2017. 4. 13..
 */
public class UnitTokenList{
	private List<UnitToken> unitTokenList;
	public UnitTokenList(List<UnitToken> unitTokenList) {
		this.unitTokenList = unitTokenList;
	}

	public UnitTokenList() {
		;
	}

	public String substring(int beginIndex, int endIndex){
		String substringResult = "";
		for(int i=beginIndex;i<endIndex;i++){
			substringResult += this.unitTokenList.get(i).getToken();
		}
		return substringResult;
	}

	public UnitTokenList subList(int beginIndex, int endIndex){
		return new UnitTokenList(this.unitTokenList.subList(beginIndex,endIndex));
	}

	public char charAt(int i){
		return this.unitTokenList.get(i).getToken();
	}
	public int size(){
		return this.unitTokenList.size();
	}

	public int length() {
		return this.size();
	}

	public String getPlainText() {
		String plainText = "";
		for (UnitToken unitToken : unitTokenList) {
			plainText += unitToken.getToken();
		}
		return plainText;
	}

	public int indexOf(char ch, int curIdx) {

		if (curIdx < 0) {
			curIdx = 0;
		} else if (curIdx >= this.unitTokenList.size()) {
			return -1;
		}

		for (int i = curIdx; i < this.unitTokenList.size(); i++) {
			if (unitTokenList.get(i).getToken() == ch) {
				return i;
			}
		}
		return -1;
	}
}
