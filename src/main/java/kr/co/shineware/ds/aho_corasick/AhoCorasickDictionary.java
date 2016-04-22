package kr.co.shineware.ds.aho_corasick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import kr.co.shineware.ds.aho_corasick.model.AhoCorasickNode;

public class AhoCorasickDictionary<V> {
	private AhoCorasickNode<V> root;
	private AhoCorasickNode<V> currentNode;

	public AhoCorasickDictionary(){
		this.root = new AhoCorasickNode<>();
		this.root.setDepth(0);
	}

	public void save(String filename){
		root.save(filename);
	}
	public void load(String filename){
		root.load(filename);
	}

	public void put(String keys,V value){
		this.put(keys.toCharArray(), value);
	}

	@SuppressWarnings("unchecked")
	private void put(char[] keys, V value) {
		AhoCorasickNode<V> currentNode = this.root;
		for(int i=0;i<keys.length;i++){
			char key = keys[i];

			AhoCorasickNode<V>[] children = currentNode.getChildren();

			if(children == null){
				children = new AhoCorasickNode[1];
				AhoCorasickNode<V> initNode = new AhoCorasickNode<>();
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
						if(children[idx].getKey() < key){
							head = idx+1;
						}else if(children[idx].getKey() > key){
							tail = idx-1;
						}else if(children[idx].getKey() == key){
							break;
						}			
					}

					AhoCorasickNode<V>[] newArray = new AhoCorasickNode[children.length + 1];
					System.arraycopy(children, 0, newArray, 0, head);
					newArray[head] = new AhoCorasickNode<V>();
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
	private int retrieveNode(AhoCorasickNode<V>[] children, char key) {
		int head = 0;
		int tail = children.length-1;
		int idx = 0;
		while(head<=tail){
			idx = (head+tail)/2;
			if(children[idx].getKey() < key){
				head = idx+1;
			}else if(children[idx].getKey() > key){
				tail = idx-1;
			}else if(children[idx].getKey() == key){
				return idx;
			}
		}
		return -1;
	}

	public V getValue(String keys){
		return this.getValue(keys.toCharArray());
	}
	public V getValue(char[] keys){
		AhoCorasickNode<V> node = this.root;		
		for(int i=0;i<keys.length;i++){
			char key = keys[i];
			AhoCorasickNode<V>[] children = node.getChildren();
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
	
	public boolean hasChild(char[] keys){
		AhoCorasickNode<V> node = this.root;		
		for(int i=0;i<keys.length;i++){
			char key = keys[i];
			AhoCorasickNode<V>[] children = node.getChildren();
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

	public void initCurrentNode(){
		this.currentNode = null;
	}

	public Map<String,V> get(char key){
		Map<String,V> resultMap = new HashMap<>();
		if(this.currentNode == null){
			this.currentNode = this.root;
		}

		while(true){
			if(currentNode.getChildren() == null){
				currentNode = currentNode.getFailNode();
				if(currentNode == null){
					return null;
				}
				continue;
			}
			int idx = this.retrieveNode(currentNode.getChildren(),key);
			if(idx == -1){
				if(currentNode == this.root){
					currentNode = this.root;
				}else{
					currentNode = currentNode.getFailNode();
					continue;
				}
			}else{
				AhoCorasickNode<V> childNode = currentNode.getChildren()[idx];
				if(childNode.getValue() != null){
					resultMap.put(this.getKeyFromNode(childNode), childNode.getValue());
				}

				while(childNode.getFailNode() != null){
					if(childNode.getFailNode().getValue() != null){
						resultMap.put(this.getKeyFromNode(childNode.getFailNode()), childNode.getFailNode().getValue());
					}
					childNode = childNode.getFailNode();
				}
				currentNode = currentNode.getChildren()[idx];
//				currentNode = childNode;
			}
			break;
		}
		if(resultMap.size() == 0){
			return null;
		}
		return resultMap;
	}	

	private String getKeyFromNode(AhoCorasickNode<V> childNode){
		AhoCorasickNode<V> currentNode = childNode;
		String key = "";
		while(currentNode != this.root){
			key = currentNode.getKey()+key;
			currentNode = currentNode.getParent();
		}
		return key;
	}

	public Map<String,V> get(String keys){
		return this.get(keys.toCharArray());
	}

	public Map<String,V> get(char[] keys) {
		Map<String,V> resultMap = new HashMap<>();
		if(currentNode == null){
			currentNode = this.root;
		}
		for(int i=0;i<keys.length;i++){
			char key = keys[i];
			if(currentNode.getChildren() == null){
				currentNode = currentNode.getFailNode();				
				i--;				
				continue;
			}
			int idx = this.retrieveNode(currentNode.getChildren(),key);
			if(idx == -1){
				if(currentNode == this.root){
					currentNode = this.root;
				}else{
					currentNode = currentNode.getFailNode();
					i--;
				}
			}else{				
				AhoCorasickNode<V> childNode = currentNode.getChildren()[idx];				
				if(childNode.getValue() != null){
					resultMap.put(this.getKeyFromNode(childNode), childNode.getValue());
				}
				while(childNode.getFailNode() != null){
					if(childNode.getFailNode().getValue() != null){
						resultMap.put(this.getKeyFromNode(childNode.getFailNode()), childNode.getFailNode().getValue());
					}					
					childNode = childNode.getFailNode();
				}
				currentNode = currentNode.getChildren()[idx];
			}
		}
		if(resultMap.size() == 0){
			return null;
		}
		return resultMap;
	}

	@SuppressWarnings("unused")
	private void printNodeAndValue(AhoCorasickNode<V> childNode) {
		AhoCorasickNode<V> currentNode = childNode;
		String key = "";
		while(currentNode != this.root){
			key = currentNode.getKey()+key;
			currentNode = currentNode.getParent();
		}
		System.out.println("key : "+key);
		System.out.println("value : "+childNode.getValue());
	}

	public void buildFailLink() {
		AhoCorasickNode<V> currentNode = this.root;
		Queue<AhoCorasickNode<V>> queue = new LinkedList<>();
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

	private void linkFailNode(AhoCorasickNode<V> currentNode) {
		if(currentNode == this.root){
			;
		}
		else if(currentNode.getParent() == this.root){
			currentNode.setFailNode(this.root);
		}
		else{
			AhoCorasickNode<V> travaseNode = currentNode.getParent().getFailNode();
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
					AhoCorasickNode<V> rootChildNode = this.root.getChildren()[idx];
					currentNode.setFailNode(rootChildNode);
				}else{
					currentNode.setFailNode(this.root);
				}
			}
		}
	}

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
	}

	private void insertNodes(Queue<AhoCorasickNode<V>> queue,
			AhoCorasickNode<V>[] ahoCorasickNodes) {
		for (AhoCorasickNode<V> ahoCorasickNode : ahoCorasickNodes) {
			queue.add(ahoCorasickNode);
		}
	}
	
	public boolean isRoot(){
		if(this.currentNode.getParent() == null){
			return true;
		}else{
			return false;
		}
	}

}
