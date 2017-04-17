package kr.co.shineware.ds.advance.ahocorasick;


import kr.co.shineware.ds.advance.ahocorasick.model.AdvanceAhoCorasickNode;

/**
 * FindContext keeps the state of trie traversal.
 *
 * @param <V> Trie element type
 */
public class AdvanceFindContext<K,V> {
	private AdvanceAhoCorasickNode<K,V> currentNode;

	public AdvanceFindContext(final AdvanceAhoCorasickNode<K,V> findRoot) {
		this.currentNode = findRoot;
	}

	public void setCurrentNode(final AdvanceAhoCorasickNode<K,V> newCurrentNode) {
		this.currentNode = newCurrentNode;
	}

	public AdvanceAhoCorasickNode<K,V> getCurrentNode() {
		return currentNode;
	}

	public AdvanceAhoCorasickNode<K,V> getCurrentFailNode() {
		return currentNode.getFailNode();
	}

	public AdvanceAhoCorasickNode<K,V>[] getCurrentChildren() {
		return currentNode.getChildren();
	}

	public boolean isCurrentRoot() {
		return this.currentNode.getParent() == null;
	}
}
