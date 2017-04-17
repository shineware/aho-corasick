package kr.co.shineware.ds.advance.ahocorasick.model;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class AdvanceAhoCorasickNode<K,V> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AdvanceAhoCorasickNode<K,V>[] children;
	private AdvanceAhoCorasickNode<K,V> parent;
	private AdvanceAhoCorasickNode<K,V> failNode;
	private K key;
	private V value;
	private int depth;

	public String getId(){
		return "["+depth+","+key+"]";
	}
	public AdvanceAhoCorasickNode<K,V>[] getChildren() {
		return children;
	}
	public void setChildren(AdvanceAhoCorasickNode<K,V>[] children) {
		this.children = children;
	}
	public AdvanceAhoCorasickNode<K,V> getParent() {
		return parent;
	}
	public void setParent(AdvanceAhoCorasickNode<K,V> parent) {
		this.parent = parent;
	}
	
	public V getValue() {
		return value;
	}
	public void setValue(V value) {
		this.value = value;
	}	
	public AdvanceAhoCorasickNode<K,V> getFailNode() {
		return failNode;
	}
	public void setFailNode(AdvanceAhoCorasickNode<K,V> failNode) {
		this.failNode = failNode;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public K getKey() {
		return key;
	}
	public void setKey(K key) {
		this.key = key;
	}
	@Override
	public String toString() {
		return ", key=" + key + ", value="
			+ value + ", depth=" + depth + " [children=" + (children == null ? 0 : children.length) + ", parent=" + parent
				+ ", failNode=" + failNode + "]";
	}
	
	public void save(String filename) {
		ObjectOutputStream dos;
		try {
			dos = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(filename))));
//			dos = new ObjectOutputStream(new BufferedOutputStream((new FileOutputStream(filename))));
			write(dos,true);
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void write(ObjectOutputStream dos,boolean isRoot) throws Exception {
		if(!isRoot){
			dos.writeObject(this.getKey());
			dos.writeObject(this.getValue());
		}
		if(children == null) {
			dos.writeInt(0);
		} else {
			dos.writeInt(children.length);
			for(int i=0; i<children.length; i++) {
				children[i].write(dos,false);
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
	@SuppressWarnings("unchecked")
	private void load(ObjectInputStream dis,boolean isRoot) throws Exception {
		if(!isRoot){
			setKey((K)dis.readObject());
			setValue((V)dis.readObject());
		}
		int length = dis.readInt();
		if(length != 0){
			children = new AdvanceAhoCorasickNode[length];
		}
		for(int i=0; i<length; i++) {
			children[i] = new AdvanceAhoCorasickNode<K,V>();
			children[i].load(dis,false);
			children[i].setParent(this);
		}
	}
}
