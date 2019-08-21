package kr.co.shineware.ds.aho_corasick.model.hash;

import kr.co.shineware.ds.aho_corasick.model.AhoCorasickNode;

import java.util.Map;
import java.util.Objects;

public class AhoCorasickHashNode<V> {
    private Map<Character, AhoCorasickHashNode<V>> children;
    private AhoCorasickHashNode<V> parent;
    private AhoCorasickHashNode<V> failNode;
    private char key;
    private V value;
    private int depth;

    public Map<Character, AhoCorasickHashNode<V>> getChildren() {
        return children;
    }

    public void setChildren(Map<Character, AhoCorasickHashNode<V>> children) {
        this.children = children;
    }

    public AhoCorasickHashNode<V> getParent() {
        return parent;
    }

    public void setParent(AhoCorasickHashNode<V> parent) {
        this.parent = parent;
    }

    public AhoCorasickHashNode<V> getFailNode() {
        return failNode;
    }

    public void setFailNode(AhoCorasickHashNode<V> failNode) {
        this.failNode = failNode;
    }

    public char getKey() {
        return key;
    }

    public void setKey(char key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AhoCorasickHashNode<?> that = (AhoCorasickHashNode<?>) o;
        return key == that.key &&
                depth == that.depth &&
                Objects.equals(parent, that.parent) &&
                Objects.equals(failNode, that.failNode) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, failNode, key, value, depth);
    }

}
