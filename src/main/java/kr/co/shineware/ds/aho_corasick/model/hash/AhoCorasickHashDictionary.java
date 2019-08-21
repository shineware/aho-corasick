package kr.co.shineware.ds.aho_corasick.model.hash;

import java.util.*;

public class AhoCorasickHashDictionary<V> {

    private AhoCorasickHashNode<V> root;

    public AhoCorasickHashDictionary() {
        this.root = new AhoCorasickHashNode<>();
        this.root.setDepth(0);
    }

    public void put(String keys, V value) {
        this.put(keys.toCharArray(), value);
    }

    @SuppressWarnings("unchecked")
    private void put(char[] keys, V value) {
        AhoCorasickHashNode<V> currentNode = this.root;
        for (int i = 0; i < keys.length; i++) {
            char key = keys[i];

            Map<Character, AhoCorasickHashNode<V>> children = currentNode.getChildren();

            if (children == null) {
                children = new HashMap<>();
                AhoCorasickHashNode<V> initNode = new AhoCorasickHashNode<>();
                initNode.setParent(currentNode);
                initNode.setDepth(i + 1);
                initNode.setKey(key);
                children.put(initNode.getKey(), initNode);
                currentNode.setChildren(children);
                currentNode = currentNode.getChildren().get(initNode.getKey());
            } else {
                AhoCorasickHashNode<V> childNode = children.get(key);
                if (childNode == null) {
                    childNode = new AhoCorasickHashNode<>();
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

    private int retrieveNode(AhoCorasickHashNode<V>[] children, char key) {
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
        AhoCorasickHashNode<V> node = this.root;
        for (int i = 0; i < keys.length; i++) {
            char key = keys[i];
            Map<Character, AhoCorasickHashNode<V>> children = node.getChildren();
            if (children == null) {
                return null;
            }

            AhoCorasickHashNode<V> childNode = children.get(key);

            if (childNode == null) {
                return null;
            }
            node = childNode;
        }
        return node.getValue();
    }

    public boolean hasChild(char[] keys) {
        AhoCorasickHashNode<V> node = this.root;
        for (int i = 0; i < keys.length; i++) {
            char key = keys[i];
            Map<Character, AhoCorasickHashNode<V>> children = node.getChildren();
            if (children == null) {
                return false;
            }

            AhoCorasickHashNode<V> childNode = children.get(key);

            if (childNode == null) {
                return false;
            }
            node = childNode;
        }
        return node.getChildren() != null;
    }

    public Map<String, V> get(FindHashContext<V> context, char key) {
        final Map<String, V> resultMap = new HashMap<>();

        while (true) {
            final Map<Character, AhoCorasickHashNode<V>> children = context.getCurrentChildren();

            if (children == null) {
                final AhoCorasickHashNode<V> currentNode = context.getCurrentFailNode();
                if (currentNode == null) {
                    return new HashMap<>();
                } else {
                    context.setCurrentNode(currentNode);
                }
                continue;
            }

            AhoCorasickHashNode<V> childNode = children.get(key);

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

    private String getKeyFromNode(AhoCorasickHashNode<V> childNode) {
        AhoCorasickHashNode<V> currentNode = childNode;
        String key = "";
        while (currentNode != this.root) {
            key = currentNode.getKey() + key;
            currentNode = currentNode.getParent();
        }
        return key;
    }

    public FindHashContext<V> newFindHashContext() {
        return new FindHashContext<>(this.root);
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

    public Map<String, V> get(FindHashContext<V> context, String keys) {
        return this.get(context, keys.toCharArray());
    }

    public Map<String, V> get(FindHashContext<V> context, char[] keys) {
        final Map<String, V> resultMap = new HashMap<>();

        for (int i = 0; i < keys.length; i++) {
            resultMap.putAll(get(context, keys[i]));
        }
        return resultMap;
    }

    @SuppressWarnings("unused")
    private void printNodeAndValue(AhoCorasickHashNode<V> childNode) {
        AhoCorasickHashNode<V> currentNode = childNode;
        String key = "";
        while (currentNode != this.root) {
            key = currentNode.getKey() + key;
            currentNode = currentNode.getParent();
        }
        System.out.println("key : " + key);
        System.out.println("value : " + childNode.getValue());
    }

    public void buildFailLink() {
        AhoCorasickHashNode<V> currentNode = this.root;
        Queue<AhoCorasickHashNode<V>> queue = new LinkedList<>();
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

    private void linkFailNode(AhoCorasickHashNode<V> currentNode) {
        if (currentNode == this.root) {
            ;
        } else if (currentNode.getParent() == this.root) {
            currentNode.setFailNode(this.root);
        } else {
            AhoCorasickHashNode<V> travaseNode = currentNode.getParent().getFailNode();
            while (travaseNode != this.root) {
                if (travaseNode.getChildren() == null) {
                    travaseNode = travaseNode.getFailNode();
                    continue;
                }
                //
                AhoCorasickHashNode<V> childNode = travaseNode.getChildren().get(currentNode.getKey());
                if (childNode != null) {
                    currentNode.setFailNode(childNode);
                    break;
                }
                travaseNode = travaseNode.getFailNode();
            }
            if (currentNode.getFailNode() == null) {
                AhoCorasickHashNode<V> childNode = this.root.getChildren().get(currentNode.getKey());
                if (childNode != null) {
                    AhoCorasickHashNode<V> rootChildNode = childNode;
                    currentNode.setFailNode(rootChildNode);
                } else {
                    currentNode.setFailNode(this.root);
                }
            }
        }
    }

    public void travaseNodes() {
        AhoCorasickHashNode<V> currentNode = this.root;
        Queue<AhoCorasickHashNode<V>> queue = new LinkedList<>();
        queue.clear();
        queue.add(currentNode);
        Map<Integer, List<AhoCorasickHashNode<V>>> depthKeyMap = new HashMap<Integer, List<AhoCorasickHashNode<V>>>();
        while (!queue.isEmpty()) {
            currentNode = queue.remove();
            this.logNode(depthKeyMap, currentNode);
            if (currentNode.getChildren() == null) continue;
            if (currentNode.getChildren().size() != 0) {
                insertNodes(queue, currentNode.getChildren().values());
            }
        }
        for (int i = 0; ; i++) {
            List<AhoCorasickHashNode<V>> keyList = depthKeyMap.get(i);
            if (keyList == null) break;
            String keys = "";
            for (AhoCorasickHashNode<V> ahoCorasickNode : keyList) {
                String failNode = "";
                if (ahoCorasickNode.getDepth() != 0) {
                    failNode = "(" + ahoCorasickNode.getFailNode().getDepth() + ":" + ahoCorasickNode.getFailNode().getKey() + ")";
                }
                keys += ahoCorasickNode.getKey() + failNode + ", ";
            }
            System.out.println("[" + i + "]" + keys);
        }
    }

    private void logNode(Map<Integer, List<AhoCorasickHashNode<V>>> depthKeyMap,
                         AhoCorasickHashNode<V> currentNode) {
        List<AhoCorasickHashNode<V>> keyList = depthKeyMap.get(currentNode.getDepth());
        if (keyList == null) {
            keyList = new ArrayList<AhoCorasickHashNode<V>>();
        }
        keyList.add(currentNode);
        depthKeyMap.put(currentNode.getDepth(), keyList);
    }

    private void insertNodes(Queue<AhoCorasickHashNode<V>> queue,
                             Collection<AhoCorasickHashNode<V>> ahoCorasickNodes) {
        for (AhoCorasickHashNode<V> ahoCorasickNode : ahoCorasickNodes) {
            queue.add(ahoCorasickNode);
        }
    }
}
