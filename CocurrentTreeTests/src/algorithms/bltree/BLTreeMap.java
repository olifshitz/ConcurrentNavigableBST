package algorithms.bltree;

import java.util.*;

public class BLTreeMap<K extends Comparable<K>,V> implements Map<K,V> {
    private final TreeNode root;

    public BLTreeMap()
    {
        this.root = new NegInfTreeNode();
    }

    private int size;
    private final Object sizeLock = new Object();

    private void incrementSize(){
        synchronized (sizeLock){
            ++size;
        }
    }

    private void decrementSize(){
        synchronized (sizeLock){
            --size;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object keyObj) {
        K key = (K) keyObj;
        TreeNode node = getNode(key);
        return node != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return values().contains((V)value);
    }

    private TreeNode getNode(K key) {
        TreeNodeVersion outNode = new TreeNodeVersion();
        while(true) {
            if(!root.findClosestNode(key, outNode)) continue;
            TreeNode node = outNode.node;
            synchronized(node){
                if(node.version != outNode.nVersion) continue;
                if(node.keysEqual(key) != outNode.foundExactly) continue;
                if (node.keysEqual(key)) return node;
                return null;
            }
        }
    }

    @Override
    public V get(Object keyObj) {
        K key = (K) keyObj;
        TreeNode node = getNode(key);
        if(node == null) return null;
        return node.value;
    }
    
    @Override
    public V put(K key, V value) {
        TreeNodeVersion outNode = new TreeNodeVersion();
        while(true){
            if(!root.findClosestNode(key, outNode)) continue;            
            TreeNode node = outNode.node;
            synchronized (node)
            {
                if(!outNode.validate()) continue;
                if(node.isMarked()) continue;
                if(node.keysEqual(key) != outNode.foundExactly) continue;
                if(outNode.foundExactly)
                {
                    return node.setValue(value);                           
                }
                
                ChildDir dir = node.getDirection(key);
                if(dir == ChildDir.This) continue;
                if(node.getChild(dir) != null) continue;
                node.setChild(dir, new TreeNode(key, value));
                incrementSize();
                return null;
            }
        }
    }
    
    @Override
    public V remove(Object keyObj) {
        K key = (K) keyObj;
        TreeNodeVersion outNode = new TreeNodeVersion();
        while(true){
            if(!root.findClosestNode(key, outNode)) continue;
            TreeNode node = outNode.node;
            TreeNode parent = outNode.parent;
            if(node == root || parent == null) return null;
            synchronized (parent){
                synchronized (node){
                    if(!outNode.validate()) continue;                    
                    if(node.isMarked() || parent.isMarked()) continue;
                    if(node.keysEqual(key) != outNode.foundExactly) continue;
                    if(!outNode.foundExactly) return null;
                    if(node.left == null || node.right == null) {
                        decrementSize();
                        V value = parent.removeSingleChild(outNode.dir, node);
                        return value;
                    }
                    
                    V oldValue = node.value;
                    if(!removeHelper(parent, node, outNode.dir)) continue;
                    return oldValue;
                }
            }
        }
    }
    
    private boolean removeHelper(TreeNode parent, TreeNode node, ChildDir dir){
        Stack<TreeNode> setChangingStack = new Stack<>();
        
        TreeNodeVersion successor = new TreeNodeVersion();
        successor.setParent(parent);
        
        node.setChanging();
        setChangingStack.push(node);
        successor.setNode(node);        
        successor.dir = dir;
        try {
            boolean first = true;
            for(TreeNode pos = successor.node.right; pos != null;pos = pos.left){
                synchronized(pos){
                if(pos.isMarked()) return false;
                pos.setChanging();
                setChangingStack.push(pos);
                successor.advance();                
                successor.setNode(pos);
                if(first){
                    successor.dir = ChildDir.Right;
                    first = false;
                } else 
                    successor.dir = ChildDir.Left;
                }
                if(!successor.validate()) return false;
            }
            final TreeNode successorParent = successor.parent;
            final TreeNode successorNode = successor.node;
            synchronized (successorParent) {
                synchronized (successorNode) {
                    if(!successor.validate()) return false;
                    if (successor.node.left != null) return false;                    
                    successor.parent.removeSingleChild(successor.dir, successor.node);
                    parent.increaseVersion();
                    node.key = successor.node.key;
                    node.value = successor.node.value;                    
                }
            }
            decrementSize();
            return true;
        } finally {
            while(!setChangingStack.isEmpty()) setChangingStack.pop().unsetChanging();
        }
    }
    
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.entrySet().stream().forEach((entry) -> {
            put(entry.getKey(), entry.getValue());
        });
    }
    
    @Override
    public boolean equals(Object other){
        Map<K,V> otherMap = (Map<K,V>) other;
        return this.entrySet().equals(otherMap.entrySet());
    }
    
    @Override
    public int hashCode(){
        return this.entrySet().hashCode();
    }

    @Override
    public void clear() {
        final TreeNode instRoot = this.root;
        synchronized (instRoot) {
            root.setChild(ChildDir.Left, null);
            root.setChild(ChildDir.Right, null);
            synchronized (sizeLock){
                size = 0;
            }
        }
    }

    @Override
    public Set<K> keySet() {
        Set<Map.Entry<K, V>> entries = entrySet();
        Set<K> keys = new HashSet<>();
        entries.stream().forEach((entry) -> {
            keys.add(entry.getKey());
        });
        return keys;
    }

    @Override
    public Collection<V> values() {
        Set<Map.Entry<K, V>> entries = entrySet();
        Collection<V> values = new ArrayList<>();
        entries.stream().forEach((entry) -> {
            values.add(entry.getValue());
        });
        return values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return entrySet(null, null, true);
    }

    public Set<K> keySet(K min, K max) {
        Set<Map.Entry<K, V>> entries = entrySet(min, max);
        Set<K> keys = new HashSet<>();
        entries.stream().forEach((entry) -> {
            keys.add(entry.getKey());
        });
        return keys;
    }

    public Collection<V> values(K min, K max) {
        Set<Map.Entry<K, V>> entries = entrySet(min, max);
        Collection<V> values = new ArrayList<>();
        entries.stream().forEach((entry) -> {
            values.add(entry.getValue());
        });
        return values;
    }

    public Set<Map.Entry<K, V>> entrySet(K min, K max) {
        return entrySet(min, max, false);
    }

    private Set<Map.Entry<K, V>> entrySet(K min, K max, boolean allTree) {
        Set<Map.Entry<K,V>> result = new HashSet<>();

        while(!this.root.setChangingRange(min, max, allTree)){}
        try {
            this.root.addRangeToSet(min, max, result, allTree, false);
            return result;
        } finally {
            this.root.unsetChangingRange(min, max, allTree);
        }
    }
    
    private class TreeNodeVersion {
        public boolean foundExactly;
        
        public TreeNode parent;
        public TreeNode node;
        public long nVersion;
        public long pVersion;
        
        public ChildDir dir;
        
        public boolean validate(){
            if(node.version != nVersion) return false;
            if(parent == null) return true;
            if(parent.version != pVersion) return false;
            return !(dir == ChildDir.This || parent.getChild(dir) != node);
        }
        
        public void setParent(TreeNode parent, long version)
        {
            this.parent = parent;
            this.pVersion = version;
        }
        
        public void setParent(TreeNode parent)
        {
            this.parent = parent;
            this.pVersion = parent.version;
        }
        
        public void unsetParent()
        {
            this.parent = null;
            this.pVersion = 0;
        }
        
        public void setNodeHard(TreeNode node, long version)
        {
            unsetParent();
            this.node = node;
            this.nVersion = version;
        }
        
        public void setNode(TreeNode node)
        {
            this.node = node;
            this.nVersion = node.version;
        }
        
        public void advance()
        {
            parent = node;
            pVersion = nVersion;
        }
    }

    private class TreeNode implements Map.Entry<K,V>{
        private volatile K key;
        private volatile V value;
        private volatile TreeNode left;
        private volatile TreeNode right;
        private volatile long version;

        private TreeNode(){
            key = null;
            value = null;
        }

        public TreeNode(K key, V value)
        {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
            this.version = 0;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = value;
            this.value = value;
            return oldValue;
        }

        private synchronized V removeSingleChild(ChildDir dir, TreeNode node){
            node.setDeleted();
            if(node.left == null){
                setChild(dir, node.right);
            } else {
                setChild(dir, node.left);
            }
            return node.value;
        }        
        
        private boolean findClosestNode(K key, TreeNodeVersion outNode){
            long nodeV = this.version;            
            if(keysEqual(key)) {
                outNode.setNodeHard(this, nodeV);
                outNode.foundExactly = true;
                return true;                
            }
            while(true){
                if(nodeV != version || isMarked()) return false;
                ChildDir dir = getDirection(key);
                if(dir == ChildDir.This) return false;
                TreeNode child = this.getChild(dir);
                if(child == null) {
                    outNode.setNodeHard(this, nodeV);
                    outNode.foundExactly = false;
                    return true;   
                }
                boolean result = child.findClosestNode(key, outNode);
                if(!result) continue;
                if(nodeV != version || isMarked()) return false;
                if(outNode.parent == null){
                    outNode.setParent(this, nodeV);
                    outNode.dir = dir;
                }
                return result;
            }
        }

        private boolean setChangingRange(K min, K max, boolean allTree)
        {
            long nodeV = this.version;
            while(true) {
                boolean largerThanMin, smallerThanMax;
                synchronized(this){
                    if(nodeV != version || isDeleted()) return false;
                    largerThanMin = allTree || compareToKey(min) >= 0;
                    smallerThanMax = allTree || compareToKey(max) <= 0;
                    if(largerThanMin && smallerThanMax) {                    
                        if(isMarked()) return false;
                        setChanging();                    
                    }
                }
                if (largerThanMin && this.left != null) {
                    if(!this.left.setChangingRange(min, max, allTree)) {
                        unsetChanging();
                        continue;
                    }
                }
                if (smallerThanMax && this.right != null) {
                    if(!this.right.setChangingRange(min, max, allTree)) {
                        if(largerThanMin && this.left != null) this.left.unsetChangingRange(min, max, allTree);
                        unsetChanging();
                        continue;
                    }
                }
                return true;
            }
        }

        private Set<Map.Entry<K,V>> addRangeToSet(K min, K max, Set<Map.Entry<K,V>> resultSet, boolean allTree, boolean addMyself)
        {
            boolean largerThanMin = allTree || compareToKey(min) >= 0;
            boolean smallerThanMax = allTree || compareToKey(max) <= 0;
            if (largerThanMin && this.left != null) {
                this.left.addRangeToSet(min, max, resultSet, allTree, true);
            }
            if(largerThanMin && smallerThanMax && addMyself) {
                resultSet.add(new AbstractMap.SimpleEntry<>(key, value));
            }
            if (smallerThanMax && this.right != null) {
                this.right.addRangeToSet(min, max, resultSet, allTree, true);
            }
            return resultSet;
        }

        private void unsetChangingRange(K min, K max, boolean allTree)
        {
            boolean largerThanMin = allTree || compareToKey(min) >= 0;
            boolean smallerThaMax = allTree || compareToKey(max) <= 0;
            if (smallerThaMax && this.right != null) {
                this.right.unsetChangingRange(min, max, allTree);
            }
            if (largerThanMin && this.left != null) {
                this.left.unsetChangingRange(min, max, allTree);
            }
            if(largerThanMin && smallerThaMax) {
                unsetChanging();
            }
        }

        protected int compareToKey(K key){
            return this.key.compareTo(key);
        }

        protected boolean keysEqual(K key){
            return compareToKey(key) == 0;
        }

        private ChildDir getDirection(K key){
            int comparison = compareToKey(key);
            if(comparison == 0) return ChildDir.This;
            if(comparison < 0) return ChildDir.Right;
            return ChildDir.Left;
        }

        private TreeNode getChild(ChildDir dir){
            switch(dir){
                case Left:
                    return left;
                case Right:
                    return right;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private void setChild(ChildDir dir, TreeNode child){
            this.increaseVersion();
            if(child != null && keysEqual(child.key)) throw new UnsupportedOperationException("SHIT");
            switch(dir){
                case Left:
                    left = child;
                    break;
                case Right:
                    right = child;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private static final int DELETED_BIT = 1;
        private static final int CHANGING_BIT = 2;
        private static final int ANY_LOCK = DELETED_BIT|CHANGING_BIT;
        private static final int VERSION_STEP = ANY_LOCK + 1;

        private boolean isMarked(){
            boolean result = (version & ANY_LOCK) != 0;
            //if(result) System.out.println("Node marked. Key: " + key);
            return result;
        }

        private boolean isDeleted(){
            return (version & DELETED_BIT) != 0;
        }

        private void setChanging(){
            long tVersion = version;            
            tVersion |= CHANGING_BIT;
            tVersion += VERSION_STEP;
            version = tVersion;
        }

        private void unsetChanging(){
            long tVersion = version;            
            tVersion &= ~CHANGING_BIT;
            tVersion += VERSION_STEP;
            version = tVersion;
        }

        private void setDeleted(){
            long tVersion = version;            
            tVersion |= DELETED_BIT;
            tVersion += VERSION_STEP;
            version = tVersion;         
        }

        private void increaseVersion(){
            version += VERSION_STEP;
        }
    }

    private class NegInfTreeNode extends TreeNode {
        public NegInfTreeNode()
        {
        }

        @Override
        protected int compareToKey(K key){
            return -1;
        }
    }

    private enum ChildDir { None, Left, Right, This }
}

