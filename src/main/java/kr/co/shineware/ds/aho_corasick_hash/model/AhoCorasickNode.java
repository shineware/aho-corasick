package kr.co.shineware.ds.aho_corasick_hash.model;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class AhoCorasickNode<V> implements Serializable{
    private Map<Character, AhoCorasickNode<V>> children;
    private AhoCorasickNode<V> parent;
    private AhoCorasickNode<V> failNode;
    private char key;
    private V value;
    private int depth;

    public Map<Character, AhoCorasickNode<V>> getChildren() {
        return children;
    }

    public void setChildren(Map<Character, AhoCorasickNode<V>> children) {
        this.children = children;
    }

    public AhoCorasickNode<V> getParent() {
        return parent;
    }

    public void setParent(AhoCorasickNode<V> parent) {
        this.parent = parent;
    }

    public AhoCorasickNode<V> getFailNode() {
        return failNode;
    }

    public void setFailNode(AhoCorasickNode<V> failNode) {
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

    public void save(String filename) {
        ObjectOutputStream dos;
        try {
            dos = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(filename))));
            write(dos,true);
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(File file) {
        ObjectOutputStream dos;
        try {
            dos = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(file))));
            write(dos,true);
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void write(ObjectOutputStream dos,boolean isRoot) throws Exception {
        if(!isRoot){
            dos.writeChar(this.getKey());
            dos.writeObject(this.getValue());
        }
        if(children == null || children.size() == 0) {
            dos.writeInt(0);
        } else {
            dos.writeInt(children.size());
            for (Character character : children.keySet()) {
                children.get(character).write(dos, false);
            }
        }
    }
    public void load(String filename) {
        ObjectInputStream dis;
        try {
            dis = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(filename))));
            load(dis,true);
            dis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(File file){
        ObjectInputStream dis;
        try {
            dis = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
            load(dis,true);
            dis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(InputStream inputStream){
        ObjectInputStream dis;
        try {
            dis = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(inputStream)));
            load(dis,true);
            dis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void load(ObjectInputStream dis,boolean isRoot) throws Exception {
        if(!isRoot){
            setKey(dis.readChar());
            setValue((V)dis.readObject());
        }
        int length = dis.readInt();
        if(length != 0){
            children = new HashMap<>();
        }
        for(int i=0; i<length; i++) {
            AhoCorasickNode ahoCorasickNode = new AhoCorasickNode<V>();
            ahoCorasickNode.load(dis, false);
            ahoCorasickNode.setParent(this);
            children.put(ahoCorasickNode.getKey(), ahoCorasickNode);
        }
    }
}
