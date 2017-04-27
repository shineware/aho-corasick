package kr.co.shineware.ds.advance.ahocorasick;

/**
 * Created by shin285 on 2017. 4. 19..
 */
public class DummyKey {
	private char token;
	private int tokenIndex;
	private TYPE tokenType;


	public DummyKey(char token, int tokenIndex, TYPE tokenType) {
		this.token = token;
		this.tokenIndex = tokenIndex;
		this.tokenType = tokenType;
	}

	@Override
	public String toString() {
		return "DummyKey{" +
				"token=" + token +
				", tokenIndex=" + tokenIndex +
				", tokenType=" + tokenType +
				'}';
	}

	@Override
	public int hashCode() {
		int result = (int) token;
		result = 31 * result + tokenIndex;
		result = 31 * result + (tokenType != null ? tokenType.hashCode() : 0);
		return result;
	}

	public DummyKey(){

	}

	public char getToken() {
		return token;
	}

	public void setToken(char token) {
		this.token = token;
	}

	public int getTokenIndex() {
		return tokenIndex;
	}

	public void setTokenIndex(int tokenIndex) {
		this.tokenIndex = tokenIndex;
	}

	public TYPE getTokenType() {
		return tokenType;
	}

	public void setTokenType(TYPE tokenType) {
		this.tokenType = tokenType;
	}
}
