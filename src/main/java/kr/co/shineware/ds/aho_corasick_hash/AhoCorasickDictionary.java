package kr.co.shineware.ds.aho_corasick_hash;

import kr.co.shineware.ds.aho_corasick_hash.model.AhoCorasickNode;

import java.io.File;
import java.io.InputStream;
import java.util.*;

public class AhoCorasickDictionary<V> {

    private AhoCorasickNode<V> root;

    public AhoCorasickDictionary() {
        this.root = new AhoCorasickNode<>();
        this.root.setDepth(0);
    }

    public void save(String filename){
        root.save(filename);
    }
    public void load(String filename){
        root.load(filename);
    }

    public void save(File file){
        root.save(file);
    }
    public void load(File file){
        root.load(file);
    }

    public void load(InputStream inputStream){
        root.load(inputStream);
    }

    public void put(String keys, V value) {
        this.put(keys.toCharArray(), value);
    }

    @SuppressWarnings("unchecked")
    private void put(char[] keys, V value) {
        AhoCorasickNode<V> currentNode = this.root;
        for (int i = 0; i < keys.length; i++) {
            char key = keys[i];

            Map<Character, AhoCorasickNode<V>> children = currentNode.getChildren();

            if (children == null) {
                children = new HashMap<>();
                AhoCorasickNode<V> initNode = new AhoCorasickNode<>();
                initNode.setParent(currentNode);
                initNode.setDepth(i + 1);
                initNode.setKey(key);
                children.put(initNode.getKey(), initNode);
                currentNode.setChildren(children);
                currentNode = currentNode.getChildren().get(initNode.getKey());
            } else {
                AhoCorasickNode<V> childNode = children.get(key);
                if (childNode == null) {
                    childNode = new AhoCorasickNode<>();
                    childNode.setParent(currentNode);
                    childNode.setDepth(i + 1);
                    childNode.setKey(key);
                    currentNode.getChildren().put(key, childNode);
                }
                currentNode = currentNode.getChildren().get(key);
            }
        }
        currentNode.setValue(value);
    }

    private int retrieveNode(AhoCorasickNode<V>[] children, char key) {
        int head = 0;
        int tail = children.length - 1;
        int idx = 0;
        while (head <= tail) {
            idx = (head + tail) / 2;
            if (children[idx].getKey() < key) {
                head = idx + 1;
            } else if (children[idx].getKey() > key) {
                tail = idx - 1;
            } else if (children[idx].getKey() == key) {
                return idx;
            }
        }
        return -1;
    }

    public V getValue(String keys) {
        return this.getValue(keys.toCharArray());
    }

    public V getValue(char[] keys) {
        AhoCorasickNode<V> node = this.root;
        for (int i = 0; i < keys.length; i++) {
            char key = keys[i];
            Map<Character, AhoCorasickNode<V>> children = node.getChildren();
            if (children == null) {
                return null;
            }

            AhoCorasickNode<V> childNode = children.get(key);

            if (childNode == null) {
                return null;
            }
            node = childNode;
        }
        return node.getValue();
    }

    public boolean hasChild(char[] keys) {
        AhoCorasickNode<V> node = this.root;
        for (int i = 0; i < keys.length; i++) {
            char key = keys[i];
            Map<Character, AhoCorasickNode<V>> children = node.getChildren();
            if (children == null) {
                return false;
            }

            AhoCorasickNode<V> childNode = children.get(key);

            if (childNode == null) {
                return false;
            }
            node = childNode;
        }
        return node.getChildren() != null;
    }

    public Map<String, V> get(FindContext<V> context, char key) {
        final Map<String, V> resultMap = new HashMap<>();

        while (true) {
            final Map<Character, AhoCorasickNode<V>> children = context.getCurrentChildren();

            if (children == null) {
                final AhoCorasickNode<V> currentNode = context.getCurrentFailNode();
                if (currentNode == null) {
                    return new HashMap<>();
                } else {
                    context.setCurrentNode(currentNode);
                }
                continue;
            }

            AhoCorasickNode<V> childNode = children.get(key);

            if (childNode == null) {
                if (context.getCurrentNode() != this.root) {
                    context.setCurrentNode(context.getCurrentFailNode());
                    continue;
                }
            } else {

                if (childNode.getValue() != null) {
                    resultMap.put(this.getKeyFromNode(childNode), childNode.getValue());
                }

                while (childNode.getFailNode() != null) {
                    if (childNode.getFailNode().getValue() != null) {
                        resultMap.put(this.getKeyFromNode(childNode.getFailNode()), childNode.getFailNode().getValue());
                    }
                    childNode = childNode.getFailNode();
                }
                context.setCurrentNode(children.get(key));
            }
            break;
        }

        return resultMap;
    }

    private String getKeyFromNode(AhoCorasickNode<V> childNode) {
        AhoCorasickNode<V> currentNode = childNode;
        String key = "";
        while (currentNode != this.root) {
            key = currentNode.getKey() + key;
            currentNode = currentNode.getParent();
        }
        return key;
    }

    public FindContext<V> newFindHashContext() {
        return new FindContext<>(this.root);
    }

    public Map<String, V> get(String keys) {
        return this.get(newFindHashContext(), keys);
    }

    public Map<String, V> get(char[] keys) {
        return this.get(newFindHashContext(), keys);
    }

    public Map<String, V> get(char key) {
        return this.get(newFindHashContext(), key);
    }

    public Map<String, V> get(FindContext<V> context, String keys) {
        return this.get(context, keys.toCharArray());
    }

    public Map<String, V> get(FindContext<V> context, char[] keys) {
        final Map<String, V> resultMap = new HashMap<>();

        for (int i = 0; i < keys.length; i++) {
            resultMap.putAll(get(context, keys[i]));
        }
        return resultMap;
    }

    @SuppressWarnings("unused")
    private void printNodeAndValue(AhoCorasickNode<V> childNode) {
        AhoCorasickNode<V> currentNode = childNode;
        String key = "";
        while (currentNode != this.root) {
            key = currentNode.getKey() + key;
            currentNode = currentNode.getParent();
        }
        System.out.println("key : " + key);
        System.out.println("value : " + childNode.getValue());
    }

    public void buildFailLink() {
        AhoCorasickNode<V> currentNode = this.root;
        Queue<AhoCorasickNode<V>> queue = new LinkedList<>();
        queue.clear();
        queue.add(currentNode);

        while (!queue.isEmpty()) {
            currentNode = queue.remove();
            this.linkFailNode(currentNode);
            if (currentNode.getChildren() == null) continue;
            if (currentNode.getChildren().size() != 0) {
                this.insertNodes(queue, currentNode.getChildren().values());
            }
        }
    }

    private void linkFailNode(AhoCorasickNode<V> currentNode) {
        if (currentNode == this.root) {
            ;
        } else if (currentNode.getParent() == this.root) {
            currentNode.setFailNode(this.root);
        } else {
            AhoCorasickNode<V> travaseNode = currentNode.getParent().getFailNode();
            while (travaseNode != this.root) {
                if (travaseNode.getChildren() == null) {
                    travaseNode = travaseNode.getFailNode();
                    continue;
                }
                //
                AhoCorasickNode<V> childNode = travaseNode.getChildren().get(currentNode.getKey());
                if (childNode != null) {
                    currentNode.setFailNode(childNode);
                    break;
                }
                travaseNode = travaseNode.getFailNode();
            }
            if (currentNode.getFailNode() == null) {
                AhoCorasickNode<V> childNode = this.root.getChildren().get(currentNode.getKey());
                if (childNode != null) {
                    AhoCorasickNode<V> rootChildNode = childNode;
                    currentNode.setFailNode(rootChildNode);
                } else {
                    currentNode.setFailNode(this.root);
                }
            }
        }
    }

    public void travaseNodes() {
        AhoCorasickNode<V> currentNode = this.root;
        Queue<AhoCorasickNode<V>> queue = new LinkedList<>();
        queue.clear();
        queue.add(currentNode);
        Map<Integer, List<AhoCorasickNode<V>>> depthKeyMap = new HashMap<Integer, List<AhoCorasickNode<V>>>();
        while (!queue.isEmpty()) {
            currentNode = queue.remove();
            this.logNode(depthKeyMap, currentNode);
            if (currentNode.getChildren() == null) continue;
            if (currentNode.getChildren().size() != 0) {
                insertNodes(queue, currentNode.getChildren().values());
            }
        }
        for (int i = 0; ; i++) {
            List<AhoCorasickNode<V>> keyList = depthKeyMap.get(i);
            if (keyList == null) break;
            String keys = "";
            for (AhoCorasickNode<V> ahoCorasickNode : keyList) {
                String failNode = "";
                if (ahoCorasickNode.getDepth() != 0) {
                    failNode = "(" + ahoCorasickNode.getFailNode().getDepth() + ":" + ahoCorasickNode.getFailNode().getKey() + ")";
                }
                keys += ahoCorasickNode.getKey() + failNode + ", ";
            }
            System.out.println("[" + i + "]" + keys);
        }
    }

    private void logNode(Map<Integer, List<AhoCorasickNode<V>>> depthKeyMap,
                         AhoCorasickNode<V> currentNode) {
        List<AhoCorasickNode<V>> keyList = depthKeyMap.get(currentNode.getDepth());
        if (keyList == null) {
            keyList = new ArrayList<AhoCorasickNode<V>>();
        }
        keyList.add(currentNode);
        depthKeyMap.put(currentNode.getDepth(), keyList);
    }

    private void insertNodes(Queue<AhoCorasickNode<V>> queue,
                             Collection<AhoCorasickNode<V>> ahoCorasickNodes) {
        for (AhoCorasickNode<V> ahoCorasickNode : ahoCorasickNodes) {
            queue.add(ahoCorasickNode);
        }
    }
}
