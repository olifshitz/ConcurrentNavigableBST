package adapters;

import algorithms.bltree.BLTreeMap;
import main.support.BBSTInterface;
import main.Globals;
import main.support.KSTNode;
import main.support.OperationListener;
import main.support.Random;
import java.util.*;
import org.deuce.transform.Exclude;

@Exclude
public class BLTreeAdapter<K extends Comparable<K>> extends AbstractAdapter<K> implements BBSTInterface<K> {
    final BLTreeMap<K,K> tree = new BLTreeMap<>();

    public final boolean contains(final K key) {
        return tree.containsKey(key);
    }
    
    public final boolean add(final K key, final Random rng) {
        tree.put(key, key);
        return true;
    }

    public final K get(final K key) {
        return tree.get(key);
    }

    public final boolean remove(final K key, final Random rng) {
        return tree.remove(key) != null;
    }

    @Override
    public final int rangeQuery(final K lo, final K hi, final int rangeSize, final Random rng) {
        Iterator<Map.Entry<K,K>> iter = tree.entryIterator(lo, hi);
        ArrayList<K> copy = new ArrayList<>();
        while (iter.hasNext())
            copy.add(iter.next().getKey());
        return copy.size();
    }

    @Override
    public final Object partialSnapshot(final int size, final Random rng) {
        assert size == Globals.DEFAULT_RQ_SIZE;
        final Object[] result = new Object[size];
        final Iterator it = tree.keySet().iterator();
        int i = 0;
        while (i < size && it.hasNext()) {
            result[i++] = it.next();
        }
        return result;
    }

    public final void addListener(final OperationListener l) {

    }

    public final int size() {
        return tree.size();
    }

    public final KSTNode<K> getRoot() {
        return null;
    }

    public final int getSumOfDepths() {
        return 0;
    }

    public final int sequentialSize() {
        return tree.size();
    }
}
