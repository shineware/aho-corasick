package kr.co.shineware.ds.advance.ahocorasick;

import kr.co.shineware.ds.advance.ahocorasick.model.AdvanceAhoCorasickNode;

import java.util.*;

/**
 * Created by shin285 on 2017. 4. 18..
 */
public class AdvanceAhoCorasickDictionary<K,V> {
	private AdvanceAhoCorasickNode<K,V> root;

	public AdvanceAhoCorasickDictionary(){
		this.root = new AdvanceAhoCorasickNode<>();
		this.root.setDepth(0);
	}

	public void save(String filename){
		root.save(filename);
	}
	public void load(String filename){
		root.load(filename);
	}

	public void put(List<K> keys,V value){
		AdvanceAhoCorasickNode<K,V> currentNode = this.root;
		for(int i=0;i<keys.size();i++){
			K key = keys.get(i);

			AdvanceAhoCorasickNode<K,V>[] children = currentNode.getChildren();

			if(children == null){
				children = new AdvanceAhoCorasickNode[1];
				AdvanceAhoCorasickNode<K,V> initNode = new AdvanceAhoCorasickNode<>();
				initNode.setParent(currentNode);
				initNode.setDepth(i+1);
				initNode.setKey(key);
				children[0] = initNode;
				currentNode.setChildren(children);
				currentNode = currentNode.getChildren()[0];
			}else{
				//retrieve children to find index.
				int idx = this.retrieveNode(children,key);
				//if children has no key.
				if(idx == -1){
					int head = 0;
					int tail = children.length-1;
					idx = 0;

					while(head<=tail){
						idx = (head+tail)/2;
						if(children[idx].getKey().hashCode() < key.hashCode()){
							head = idx+1;
						}else if(children[idx].getKey().hashCode() > key.hashCode()){
							tail = idx-1;
						}else if(children[idx].getKey() == key){
							break;
						}
					}

					AdvanceAhoCorasickNode<K,V>[] newArray = new AdvanceAhoCorasickNode[children.length + 1];
					System.arraycopy(children, 0, newArray, 0, head);
					newArray[head] = new AdvanceAhoCorasickNode<K,V>();
					newArray[head].setParent(currentNode);
					newArray[head].setDepth(i+1);
					newArray[head].setKey(key);
					System.arraycopy(children, head, newArray, head+1, children.length-head);
					currentNode.setChildren(newArray);
					idx = head;
				}
				currentNode = currentNode.getChildren()[idx];
			}
		}
		currentNode.setValue(value);
	}


	private int retrieveNode(AdvanceAhoCorasickNode<K,V>[] children, K key) {
		int head = 0;
		int tail = children.length-1;
		int idx = 0;
		while(head<=tail){
			idx = (head+tail)/2;

			if(children[idx].getKey().hashCode() < key.hashCode()){
				head = idx+1;
			}else if(children[idx].getKey().hashCode() > key.hashCode()){
				tail = idx-1;
			}else if(children[idx].getKey().hashCode() == key.hashCode()){
				return idx;
			}
		}
		return -1;
	}

	public V getValue(List<K> keys){
		AdvanceAhoCorasickNode<K,V> node = this.root;
		for(int i=0;i<keys.size();i++){
			K key = keys.get(i);
			AdvanceAhoCorasickNode<K,V>[] children = node.getChildren();
			if(children == null){
				return null;
			}

			int idx = retrieveNode(children, key);
			if(idx == -1){
				return null;
			}
			node = children[idx];
		}
		return node.getValue();
	}

	public boolean hasChild(List<K> keys){
		AdvanceAhoCorasickNode<K,V> node = this.root;
		for(int i=0;i<keys.size();i++){
			K key = keys.get(i);
			AdvanceAhoCorasickNode<K,V>[] children = node.getChildren();
			if(children == null){
				return false;
			}

			int idx = retrieveNode(children, key);
			if(idx == -1){
				return false;
			}
			node = children[idx];
		}
		return node.getChildren() != null;
	}

	public Map<List<K>,V> get(AdvanceFindContext<K,V> context, K key) {
		final Map<List<K>,V> resultMap = new HashMap<>();

		while (true) {
			final AdvanceAhoCorasickNode<K,V>[] children = context.getCurrentChildren();

			if (children == null) {
				final AdvanceAhoCorasickNode<K,V> currentNode = context.getCurrentFailNode();
				if (currentNode == null) {
					return new HashMap<>();
				} else {
					context.setCurrentNode(currentNode);
				}
				continue;
			}

			final int idx = this.retrieveNode(children, key);
			if (idx == -1) {
				if (context.getCurrentNode() != this.root) {
					context.setCurrentNode(context.getCurrentFailNode());
					continue;
				}
			} else {
				AdvanceAhoCorasickNode<K,V> childNode = children[idx];

				if (childNode.getValue() != null) {
					resultMap.put(this.getKeyFromNode(childNode), childNode.getValue());
				}

				while (childNode.getFailNode() != null) {
					if(childNode.getFailNode().getValue() != null){
						resultMap.put(this.getKeyFromNode(childNode.getFailNode()), childNode.getFailNode().getValue());
					}
					childNode = childNode.getFailNode();
				}
				context.setCurrentNode(children[idx]);
			}
			break;
		}

		return resultMap;
	}

	private List<K> getKeyFromNode(AdvanceAhoCorasickNode<K,V> childNode){
		AdvanceAhoCorasickNode<K,V> currentNode = childNode;
		List<K> key = new ArrayList<>();
		while(currentNode != this.root){
			key.add(0,currentNode.getKey());
			currentNode = currentNode.getParent();
		}
		return key;
	}

	public AdvanceFindContext<K,V> newFindContext() {
		return new AdvanceFindContext<>(this.root);
	}

	public Map<List<K>, V> get(List<K> keys) {
		return this.get(newFindContext(), keys);
	}

	public Map<List<K>, V> get(K key) {
		return this.get(newFindContext(), key);
	}

	public Map<List<K>,V> get(AdvanceFindContext<K,V> context, List<K> keys) {
		final Map<List<K>,V> resultMap = new HashMap<>();

		for (int i = 0; i < keys.size(); i++) {
			resultMap.putAll(get(context, keys.get(i)));
		}
		return resultMap;
	}

	@SuppressWarnings("unused")
	private void printNodeAndValue(AdvanceAhoCorasickNode<K,V> childNode) {
		AdvanceAhoCorasickNode<K,V> currentNode = childNode;
		String key = "";
		while(currentNode != this.root){
			key = currentNode.getKey()+key;
			currentNode = currentNode.getParent();
		}
		System.out.println("key : "+key);
		System.out.println("value : "+childNode.getValue());
	}

	public void buildFailLink() {
		AdvanceAhoCorasickNode<K,V> currentNode = this.root;
		Queue<AdvanceAhoCorasickNode<K,V>> queue = new LinkedList<>();
		queue.clear();
		queue.add(currentNode);

		while(!queue.isEmpty()){
			currentNode = queue.remove();
			this.linkFailNode(currentNode);
			if(currentNode.getChildren() == null)continue;
			if(currentNode.getChildren().length != 0){
				this.insertNodes(queue,currentNode.getChildren());
			}
		}
	}

	private void linkFailNode(AdvanceAhoCorasickNode<K,V> currentNode) {
		if(currentNode == this.root){
			;
		}
		else if(currentNode.getParent() == this.root){
			currentNode.setFailNode(this.root);
		}
		else{
			AdvanceAhoCorasickNode<K,V> travaseNode = currentNode.getParent().getFailNode();
			while(travaseNode != this.root){
				if(travaseNode.getChildren() == null){
					travaseNode = travaseNode.getFailNode();
					continue;
				}
				//
				int idx = this.retrieveNode(travaseNode.getChildren(),currentNode.getKey());
				//
				if(idx != -1){
					currentNode.setFailNode(travaseNode.getChildren()[idx]);
					break;
				}
				travaseNode = travaseNode.getFailNode();
			}
			if(currentNode.getFailNode() == null){
				int idx = this.retrieveNode(this.root.getChildren(),currentNode.getKey());
				if(idx != -1){
					AdvanceAhoCorasickNode<K,V> rootChildNode = this.root.getChildren()[idx];
					currentNode.setFailNode(rootChildNode);
				}else{
					currentNode.setFailNode(this.root);
				}
			}
		}
	}
/*
	public void travaseNodes(){
		AhoCorasickNode<V> currentNode = this.root;
		Queue<AhoCorasickNode<V>> queue = new LinkedList<>();
		queue.clear();
		queue.add(currentNode);
		Map<Integer,List<AhoCorasickNode<V>>> depthKeyMap = new HashMap<Integer, List<AhoCorasickNode<V>>>();
		while(!queue.isEmpty()){
			currentNode = queue.remove();
			this.logNode(depthKeyMap,currentNode);
			if(currentNode.getChildren() == null)continue;
			if(currentNode.getChildren().length != 0){
				insertNodes(queue,currentNode.getChildren());
			}
		}
		for(int i=0;;i++){
			List<AhoCorasickNode<V>> keyList = depthKeyMap.get(i);
			if(keyList == null)break;
			String keys = "";
			for (AhoCorasickNode<V> ahoCorasickNode : keyList) {
				String failNode = "";
				if(ahoCorasickNode.getDepth() != 0){
					failNode = "("+ahoCorasickNode.getFailNode().getDepth()+":"+ahoCorasickNode.getFailNode().getKey()+")";
				}
				keys += ahoCorasickNode.getKey()+failNode+", ";
			}
			System.out.println("["+i+"]"+keys);
		}
	}

	private void logNode(Map<Integer, List<AhoCorasickNode<V>>> depthKeyMap,
						 AhoCorasickNode<V> currentNode) {
		List<AhoCorasickNode<V>> keyList = depthKeyMap.get(currentNode.getDepth());
		if(keyList == null){
			keyList = new ArrayList<AhoCorasickNode<V>>();
		}
		keyList.add(currentNode);
		depthKeyMap.put(currentNode.getDepth(), keyList);
	}*/

	private void insertNodes(Queue<AdvanceAhoCorasickNode<K,V>> queue,
							 AdvanceAhoCorasickNode<K,V>[] ahoCorasickNodes) {
		for (AdvanceAhoCorasickNode<K,V> ahoCorasickNode : ahoCorasickNodes) {
			queue.add(ahoCorasickNode);
		}
	}
}
