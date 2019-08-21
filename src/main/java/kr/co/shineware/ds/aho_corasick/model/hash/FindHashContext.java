package kr.co.shineware.ds.aho_corasick.model.hash;

import java.util.Map;

public class FindHashContext<V> {
    private AhoCorasickHashNode<V> currentNode;

    public FindHashContext(final AhoCorasickHashNode<V> findRoot) {
        this.currentNode = findRoot;
    }

    public void setCurrentNode(final AhoCorasickHashNode<V> newCurrentNode) {
        this.currentNode = newCurrentNode;
    }

    public AhoCorasickHashNode<V> getCurrentNode() {
        return currentNode;
    }

    public AhoCorasickHashNode<V> getCurrentFailNode() {
        return currentNode.getFailNode();
    }

    public Map<Character, AhoCorasickHashNode<V>> getCurrentChildren() {
        return currentNode.getChildren();
    }

    public boolean isCurrentRoot() {
        return this.currentNode.getParent() == null;
    }
}
