package kr.co.shineware.ds.aho_corasick_hash;

import kr.co.shineware.ds.aho_corasick_hash.model.AhoCorasickNode;

import java.util.Map;

public class FindContext<V> {
    private AhoCorasickNode<V> currentNode;

    public FindContext(final AhoCorasickNode<V> findRoot) {
        this.currentNode = findRoot;
    }

    public void setCurrentNode(final AhoCorasickNode<V> newCurrentNode) {
        this.currentNode = newCurrentNode;
    }

    public AhoCorasickNode<V> getCurrentNode() {
        return currentNode;
    }

    public AhoCorasickNode<V> getCurrentFailNode() {
        return currentNode.getFailNode();
    }

    public Map<Character, AhoCorasickNode<V>> getCurrentChildren() {
        return currentNode.getChildren();
    }

    public boolean isCurrentRoot() {
        return this.currentNode.getParent() == null;
    }
}
